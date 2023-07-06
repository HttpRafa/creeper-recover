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
  "configVersion": 2,
  "plugin": {
    "bStats": true,
    "ignoreUpdates": false
  },
  "recover": {
    "recoverSpeed": 3,
    "recoverDelay": 100,
    "blockRecoverSound": "BLOCK_ROOTED_DIRT_PLACE",
    "blockBlacklist": []
  },
  "target": [
    {
      "type": "WORLD",
      "ignore": true,
      "whitelist": [],
      "blacklist": []
    },
    {
      "type": "ENTITY",
      "ignore": true,
      "entityTypes": [
        "CREEPER",
        "PRIMED_TNT"
      ]
    },
    {
      "type": "HEIGHT_RANGE",
      "ignore": true,
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
recoverSpeed: The time in milliseconds between each block that is being recovered.
recoverDelay: The time in milliseconds to wait before starting the recovery
blockRecoverSound: Is the sound played when the plugin places a block.
blockBlacklist: Blocks in this list are ignored by the plugin. 
target: In the list, rules are specified where the plugin should take effect.

all: If "all" is set to true all entities will be affected. So if you want only TNT to be recovered then set "all" to false and define TNT in the entityTypes setting.
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
### Affect only some worlds
#### Whitelist: Only affect certain worlds
#### Blacklist: Affect all world except those in the blacklist
```
{
      "type": "WORLD",
      "ignore": false,
      "whitelist": [],
      "blacklist": []
}
```
### Affect only TnT and Creepers
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

### Affect only from Y-0 to Y-320
```
{
      "type": "HEIGHT_RANGE",
      "ignore": false,
      "from": 0,
      "to": 320
}
```

### Affect only at Y-60
```
{
      "type": "HEIGHT_FIXED",
      "ignore": false,
      "fixed": 60
}
```

# Pages
#### [Modrinth](https://modrinth.com/plugin/creeper-recover)
#### [SpigotMC](https://www.spigotmc.org/resources/creeper-recover.98836/)
#### [DeinPlugin](https://deinplugin.net/storage/c97b3869-d8ec-4177-8d8c-b7792c96eedc)