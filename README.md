# PacketBuilder
This Library provides features constructing packets in Kotlin easier when using [ProtocolLib](https://github.com/aadnk/ProtocolLib).

## Usage
```kotlin
val packet = PacketBuilder.with(<PacketType>) {
    // Insert content
}
packet.send(player) // Sends the packet to the specified player
packet.send(player) // Sends the packet to the specified players
packet.sendExcept(player) // Sends the packet to everyone except the specified player
```


## Examples

Creating a teleport packet
```kotlin
val location = player.location
PacketBuilder.with(PacketType.Play.Server.ENTITY_TELEPORT) {
    entityId(player.entityId) // Sets the entityID
    position(location) // Sets the position
    rotation(Rotation(location.yaw, location.pitch)) // Sets the rotation
    boolean(false) // Sets if the entity is onGround
}.send(player)
```

Creating an equipment packet
```kotlin
val player = ..
PacketBuilder.with(PacketType.Play.Server.ENTITY_EQUIPMENT) {
    entityId(player.entityId) // Sets the entityId
    itemSlot(EnumWrappers.ItemSlot.HEAD) // Sets the ItemSlot
    item(player.itemInHand) // Sets the ItemStack
}.send(player)
```


## Troubleshooting
If you have trouble creating a certain packet, look it up on the [Wiki](https://wiki.vg/Protocol).


## Auto-Translation

Packets that were changed throughout the game are supported.
