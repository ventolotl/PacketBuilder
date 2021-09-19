package de.ventolotl.packetbuilder

import com.comphenix.protocol.events.PacketContainer
import com.comphenix.protocol.utility.MinecraftVersion
import com.comphenix.protocol.wrappers.EnumWrappers
import com.comphenix.protocol.wrappers.WrappedChatComponent
import com.comphenix.protocol.wrappers.WrappedDataWatcher
import org.bukkit.inventory.ItemStack
import org.bukkit.util.Vector
import java.util.*

class BooleanWriter(
    packet: PacketContainer,
    indexer: Indexer,
) : Writer<Boolean>(IndexEntry.BOOLEAN, packet, indexer) {
    override fun invoke(value: Boolean) {
        packet.booleans.write(next(), value)
    }
}

class IntWriter(
    packet: PacketContainer,
    indexer: Indexer,
) : Writer<Int>(IndexEntry.INT, packet, indexer) {
    override fun invoke(value: Int) {
        packet.integers.write(next(), value)
    }
}

class IntArrayWriter(
    packet: PacketContainer,
    indexer: Indexer,
) : Writer<Array<Int>>(IndexEntry.INT_ARRAY, packet, indexer) {
    override fun invoke(value: Array<Int>) {
        packet.integerArrays.write(next(), value.toIntArray())
    }

    private fun Array<Int>.toIntArray(): IntArray {
        val intArray = IntArray(this.size)
        for ((i, value) in this.withIndex()) {
            intArray[i] = value
        }
        return IntArray(this.size)
    }

    operator fun invoke(vararg values: Int) {
        packet.integerArrays.write(next(), values)
    }
}

class ByteWriter(
    packet: PacketContainer,
    indexer: Indexer,
) : Writer<Byte>(IndexEntry.BYTE, packet, indexer) {
    override fun invoke(value: Byte) {
        packet.bytes.write(next(), value)
    }
}

class ItemSlotWriter(
    packet: PacketContainer,
    indexer: Indexer,
) : Writer<EnumWrappers.ItemSlot>(IndexEntry.ITEM_SLOT, packet, indexer) {
    override fun invoke(value: EnumWrappers.ItemSlot) {
        packet.itemSlots.write(next(), value)
    }
}

class UUIDWriter(
    packet: PacketContainer,
    indexer: Indexer,
) : Writer<UUID>(IndexEntry.UUID, packet, indexer) {
    override fun invoke(value: UUID) {
        packet.uuiDs.write(next(), value)
    }
}

abstract class PositionWriter(
    entry: IndexEntry,
    packet: PacketContainer,
    indexer: Indexer,
) : Writer<Vector>(entry, packet, indexer) {
    companion object {
        @JvmStatic
        fun create(
            packet: PacketContainer,
            indexer: Indexer,
        ): PositionWriter {
            return when (MinecraftVersion.COMBAT_UPDATE.atOrAbove()) {
                true -> ModernPositionWriter(packet, indexer)
                false -> LegacyPositionWriter(packet, indexer)
            }
        }
    }
}

class ModernPositionWriter(
    packet: PacketContainer,
    indexer: Indexer,
) : PositionWriter(IndexEntry.DOUBLE, packet, indexer) {
    override fun invoke(value: Vector) {
        packet.doubles.write(next(), value.x).write(next(), value.y).write(next(), value.z)
    }
}

private const val POSITION_CONVERT_FACTOR = 32.0

class LegacyPositionWriter(
    packet: PacketContainer,
    indexer: Indexer,
) : PositionWriter(IndexEntry.INT, packet, indexer) {
    override fun invoke(value: Vector) {
        packet.integers
            .write(next(), (value.x * POSITION_CONVERT_FACTOR).floor())
            .write(next(), (value.y * POSITION_CONVERT_FACTOR).floor())
            .write(next(), (value.z * POSITION_CONVERT_FACTOR).floor())
    }

    private fun Double.floor(): Int {
        val i = this.toInt()
        return if (this < i.toDouble()) i - 1 else i
    }
}

private const val ROTATION_CONVERT_FACTOR = 256.0f / 360.0f

class RotationHeadWriter(
    packet: PacketContainer,
    indexer: Indexer,
) : Writer<Float>(IndexEntry.BYTE, packet, indexer) {
    override fun invoke(value: Float) {
        packet.bytes.write(next(), (value * ROTATION_CONVERT_FACTOR).toInt().toByte())
    }
}

class RotationWriter(
    packet: PacketContainer,
    indexer: Indexer,
) : Writer<Rotation>(IndexEntry.BYTE, packet, indexer) {
    override fun invoke(value: Rotation) {
        packet.bytes
            .write(next(), (value.yaw * ROTATION_CONVERT_FACTOR).toInt().toByte())
            .write(next(), (value.pitch * ROTATION_CONVERT_FACTOR).toInt().toByte())
    }
}

data class Rotation(val yaw: Float, val pitch: Float)

class DataWatcherWriter(
    packet: PacketContainer,
    indexer: Indexer,
) : Writer<WrappedDataWatcher>(IndexEntry.DATA_WATCHER, packet, indexer) {
    override fun invoke(value: WrappedDataWatcher) {
        val modifier = packet.watchableCollectionModifier
        modifier.write(next(), value.watchableObjects)
    }
}

class ItemWriter(
    packet: PacketContainer,
    indexer: Indexer,
) : Writer<ItemStack>(IndexEntry.ITEM, packet, indexer) {
    override fun invoke(value: ItemStack) {
        val modifier = packet.itemModifier
        modifier.write(next(), value)
    }
}

class ChatComponentWriter(
    packet: PacketContainer,
    indexer: Indexer,
) : Writer<WrappedChatComponent>(IndexEntry.CHAT_COMPONENT, packet, indexer) {
    override fun invoke(value: WrappedChatComponent) {
        val modifier = packet.chatComponents
        modifier.write(next(), value)
    }
}
