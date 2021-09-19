package de.ventolotl.packetbuilder

import com.comphenix.protocol.PacketType
import com.comphenix.protocol.ProtocolLibrary
import com.comphenix.protocol.ProtocolManager
import com.comphenix.protocol.events.PacketContainer
import org.bukkit.Bukkit
import org.bukkit.entity.Player

private val protocol: ProtocolManager = ProtocolLibrary.getProtocolManager()

class PacketBuilder(private val packet: PacketContainer) {
    companion object {
        /** Constructs the [PacketBuilder] with the given [type]. */
        @JvmStatic
        fun with(type: PacketType, creation: Builder.() -> Unit): PacketBuilder {
            val packet = protocol.createPacket(type)
            creation(Builder(packet))
            return PacketBuilder(packet)
        }
    }

    /** Sends the [packet] to the given [player]. */
    fun send(player: Player) {
        protocol.sendServerPacket(player, this.packet)
    }

    /** Sends the [packet] to the given [players]. */
    fun send(players: Iterable<Player>) {
        players.forEach { player -> send(player) }
    }

    /**
     * Sends the [packet] to all players except
     * the given [player].
     */
    fun sendExcept(player: Player) {
        val players = Bukkit.getOnlinePlayers()
        players
            .filter { other -> other != player }
            .forEach { receiver -> send(receiver) }
    }
}

/** Sends a sequence of [PacketBuilder]s to the given [player]. */
fun Iterable<PacketBuilder>.send(player: Player) {
    this.forEach { packet -> packet.send(player) }
}

/** Sends a sequence of [PacketBuilder]s to the given [players]. */
fun Iterable<PacketBuilder>.send(players: Iterable<Player>) {
    players.forEach(this::send)
}

/**
 * Sends a sequence of [PacketBuilder]s to
 * all players except the given [player].
 */
fun Iterable<PacketBuilder>.sendExcept(player: Player) {
    this.forEach { packet -> packet.sendExcept(player) }
}

/** Defines various [IndexEntry]s used to determine the next index. */
enum class IndexEntry {
    INT,
    DOUBLE,
    BYTE,
    CHAT_COMPONENT,
    BOOLEAN,
    INT_ARRAY,
    DATA_WATCHER,
    ITEM,
    ITEM_SLOT,
    UUID,
}

/** Indexes fields orders with various [IndexEntry]s. */
class Indexer {
    private val map = hashMapOf<IndexEntry, Int>()

    /**
     * Indexes the next field of the given [entry].
     *
     * If the first entry is `INT`, it returns 0.
     * If the next entry is `INT`, it returns 1.
     * If the third entry is `BYTE`, it returns 0.
     */
    fun next(entry: IndexEntry): Int {
        map[entry] = map.getOrPut(entry) { 0 } + 1
        return map[entry]?.minus(1) ?: 0
    }
}

class Builder(
    packet: PacketContainer,
) {
    private val indexer = Indexer()

    val byte = ByteWriter(packet, indexer)
    val intArray = IntArrayWriter(packet, indexer)
    val boolean = BooleanWriter(packet, indexer)
    val entityId = IntWriter(packet, indexer)
    val entityType = IntWriter(packet, indexer)
    val armorPosition = IntWriter(packet, indexer)
    val itemSlot = ItemSlotWriter(packet, indexer)
    val uuid = UUIDWriter(packet, indexer)
    val position = PositionWriter.create(packet, indexer)
    val rotation = RotationWriter(packet, indexer)
    val rotationYaw = RotationHeadWriter(packet, indexer)
    val dataWatcher = DataWatcherWriter(packet, indexer)
    val item = ItemWriter(packet, indexer)
    val chatComponent = ChatComponentWriter(packet, indexer)
}

infix fun <T : Any> Writer<T>.with(value: T) {
    invoke(value)
}

open class Writer<T : Any>(
    private val entry: IndexEntry,
    internal val packet: PacketContainer,
    private val indexer: Indexer,
) {
    open operator fun invoke(value: T) {
        val modifier = packet.modifier
        modifier.write(indexer.next(entry), value)
    }

    protected fun next(): Int {
        return indexer.next(entry)
    }
}
