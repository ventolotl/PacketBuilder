# PacketBuilder
PacketBuilder provides nice features for Kotlin when you construct packets with [ProtocolLib](https://github.com/aadnk/ProtocolLib).

## Usage
```kotlin
val packet = PacketBuilder.with(<PacketType>) {
    // Insert content
}
packet.send(player) // Sends the packet to the specified player
packet.send(players) // Sends the packet to the specified players
packet.sendExcept(player) // Sends the packet to everyone except the specified player
```

## Syntax
This Library supports two syntaxes for creating packets. You can either use the `invoke` or `infix` pattern.

### Invoke Syntax
The `invoke` syntax uses parentheses.
```kotlin
val packet = PacketBuilder.with(<PacketType>) {
    entityId(100)
}
```
### Infix Syntax
The `infix` syntax uses `with`.
```kotlin
val packet = PacketBuilder.with(<PacketType>) {
    entityId with 100
}
```

## Examples

Creating a teleport packet:
```kotlin
val location = player.location
PacketBuilder.with(PacketType.Play.Server.ENTITY_TELEPORT) {
    entityId(player.entityId) // Sets the entityID
    position(location) // Sets the position
    rotation(Rotation(location.yaw, location.pitch)) // Sets the rotation
    boolean(false) // Sets if the entity is onGround
}.send(player)
```


## Troubleshooting
If you have trouble creating a certain packet, look it up on the [Wiki](https://wiki.vg/Protocol).


## Auto-Translation

Packets that were changed throughout the game are supported.
