# CreeperRecover
Recovers all destroyed blocks by Creeper or other Entities

# Images
![Example 1](https://i.postimg.cc/vHJMc4Qj/2021-12-31-17-53-38.gif)

# Commands
![Command 1](https://raw.githubusercontent.com/HttpRafa/CreeperRecover/master/images/command1.png)
![Command 2](https://raw.githubusercontent.com/HttpRafa/CreeperRecover/master/images/command2.png)

# Config
```
{
  "configVersion": 1,
  "plugin": {
    "bStats": true,
    "ignoreUpdates": false
  },
  "recover": {
    "recoverSpeed": 3,
    "blockRecoverSound": "BLOCK_GRAVEL_PLACE"
  },
  "target": [
    {
      "type": "ENTITY",
      "ignore": false,
      "all": true,
      "entityTypes": [
        "CREEPER",
        "PRIMED_TNT"
      ]
    },
    {
      "type": "HEIGHT_RANGE",
      "ignore": false,
      "from": -64,
      "to": 320
    },
    {
      "type": "HEIGHT_FIXED",
      "ignore": true,
      "fixed": 32
    }
  ]
}
```
## Options
```
configVersion: Is set to the current config version.
bStats: Whether bStats is enabled or disabled. To support me please leave it active.
ignoreUpdates: If you don't want to receive a message in the console when the plugin has a update.
recoverSpeed: The amount of ticks between each block that is being recovered.
blockRecoverSound: Is the sound played when the plugin places a block.
target: In the list, rules are specified where the plugin should take effect.
```

### Example Entities
```
CREEPER
PRIMED_TNT
SMALL_FIREBALL
FIREBALL
WITHER_SKULL
DRAGON_FIREBALL
```

# Config Examples[target]
### Effect all entities
```
{
      "type": "ENTITY",
      "ignore": false,
      "all": true
}
```
### Effect only TnT and Creepers
```
{
      "type": "ENTITY",
      "ignore": false,
      "entityTypes": [
        "CREEPER",
        "PRIMED_TNT"
      ]
}
```

### Effect only from Y-0 to Y-320
```
{
      "type": "HEIGHT_RANGE",
      "ignore": false,
      "from": 0,
      "to": 320
}
```

### Effect only at Y-60
```
{
      "type": "HEIGHT_FIXED",
      "ignore": false,
      "fixed": 60
}
```

# Download
#### SpigotMC: https://www.spigotmc.org/resources/creeper-recover.98836/.
#### DeinPlugin: Hopefully in the future
