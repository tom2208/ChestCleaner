Welcome to the ChestCleaner wiki! Here you can find tutorials and explanations for all (if the wiki is finished) features of the plugin.

![Sorting Example](https://github.com/tom2208/ChestCleaner/blob/master/assets/gif/sorting.gif)

# Installation
* Download the **ChestCleaner.jar** from spigot: https://www.spigotmc.org/resources/chestcleaner-sorting-plugin-api.40313/
* Move the **ChestCleaner.jar** from your download folder into the plugin folder of your spigot server.
* Start the server. If everything works you will see "_[ChestCleaner] Enabling ChestCleaner_"  without an error message in your console. 

# FAQ

## My players can't use Click Sort, what can i do?
There are a few thing that you may need to do:
### 1. Players who want to use Click Sort need the permission to do so.
Either you give the players op (`/op <playername>`) or
you will need a permission plugin. One example for such a plugin is [LuckPerms](https://www.spigotmc.org/resources/luckperms.28140/). Just install it on your server. To add permissions to the default player group (every player who joins the server) use: `/lp group default permission set <permission> <true/false>` in our case of Click Sort use: `/lp group default permission set chestcleaner.clicksort true`. Now every player has the permission to use the click sort feature. To change their individual perferences your players will need the permission `chestcleaner.cmd.sortingconfig.clicksort` too.

### 2. Plugin Configuration
You could try setting the globale default perference with: `/sortingadmin ClickSort <true/false>` and the individual with `/sortingconfig ClickSort <true/false>`.

### 3. Key Binding
Click Sort uses the select block button of minecraft, the button which picks you a block you are looking at in creative mode or the button which creates a stack of an item in the creative inventory if you are clicking on it. The default is the middle mouse button. You may have changed it.
Since 2.4.1 you have to use right-click next to the gui for click sort.

***
If your question isn't solved after reading this: read the wiki or join our discord server: [Discord](https://discord.gg/tyjVfDq)


# Clicksort

Use right click outside the gui (inventory) you want to sort. Make sure you enabled clicksort for more information take a look at the FAQ.

# Blacklist Commands

## Description
There are two types of blacklists: **stacking** and **inventory**. 

Both blacklists contain item names and are configured the same way, however they have different meanings.

**The stacking blacklist** defines items which will not get stacked together if they get sorted. For instance, usually a 32 dirt stack and a 21 dirt stack would get stacked together to a 53 dirt stack. With dirt on the stacking blacklist you preserve the two original stacks. 

**The inventory blacklist** defines blocks that get ignored if you want to try sorting an inventory of a block (only works for "openEvent = false" mode). For instance, if you don't want your player to sort hoppers, then you add hopper to the inventory blacklist.

## Commands

| Command                                        | Explanation                                                  | Permission                       |
| ---------------------------------------------- | ------------------------------------------------------------ | -------------------------------- |
| /blacklist \<blacklist\> add                   | Adds the material of the item you are holding in your main hand to the specified blacklist. | chestcleaner.cmd.admin.blacklist |
| /blacklist \<blacklist\> add \<materialId\>    | Adds the specified material to the specified blacklist.      | chestcleaner.cmd.admin.blacklist |
| /blacklist \<blacklist\> remove                | Removes the material of the item you are holding in your main hand from the specified blacklist. | chestcleaner.cmd.admin.blacklist |
| /blacklist \<blacklist\> remove \<materialId\> | Removes the material of the item you are holding in your main hand from the specified blacklist. | chestcleaner.cmd.admin.blacklist |
| /blacklist \<blacklist\> list                  | Prints out the specified blacklist.                          | chestcleaner.cmd.admin.blacklist |
| /blacklist \<blacklist\> clear                 | Removes every entry of the specified blacklist.              | chestcleaner.cmd.admin.blacklist |

## Permissions

| Permission                       | Explanation                                     |
| -------------------------------- | ----------------------------------------------- |
| chestcleaner.cmd.admin.blacklist | Allows the player to use all blacklist commands |

## Config
* the blacklists are saved/configured in config.yml
```yaml
blacklist:
  stacking:
    - air
    - bed
  inventory:
    - furnace
  autorefill:
    - stone
```

# CleanInventory Commands

## Description
This commands sorts an inventory that is associated with a block (like a chest).

**Be careful when using a protection plugin, as this command allows a player to sort any block in any world.**

## Commands

| Command                                    | Explanation                                                  | Permission                             |
| ------------------------------------------ | ------------------------------------------------------------ | -------------------------------------- |
| /cleaninventory                            | Sorts the inventory you are looking at.                      | chestcleaner.cmd.cleaninventory.sort   |
| /cleaninventory \<x\> \<y\> \<z\>          | Sorts the inventory at the position specified by x y z.      | chestcleaner.cmd.cleaninventory.sort   |
| /cleaninventory \<x\> \<y\> \<z\> \<word\> | Sorts the inventory at the position specified by x y z and a world name (for instance "nether_world"). | chestcleaner.cmd.cleaninventory.sort   |
| /cleaninventory own                        | Sorts your own inventory.                                    | chestcleaner.cmd.cleaninventory.own    |
| /cleaninventory \<player>                  | Sorts the inventory of a player.                             | chestcleaner.cmd.cleaninventory.others |

## Permissions
Only Server OPs have this permission by default.

| Permission                             | Explanation                                                  |
| -------------------------------------- | ------------------------------------------------------------ |
| chestcleaner.cmd.cleaninventory.sort   | Allows you to use /cleaninventory to sort block inventories in all world. |
| chestcleaner.cmd.cleaninventory.own    | Allows you to sort your own inventory with /cleaninventory.  |
| chestcleaner.cmd.cleaninventory.others | Allows you to sort player inventory with /cleaninventory.    |

# CleaningItem Commands

## Description
You can configure an Item to be used as cleaning item. When you hold that item and: 
* `right-click` on a block with an inventory, that blocks inventory will get sorted.
* `shift + right-click`, your Inventory gets sorted.

The cleaningitem is configured for the whole server. 
You can select a craftable or stackable item for your cleaning item. If you do so players can craft their items themself, using sources to "pay" for the sorting, like the gold for the golden hoe.
If you pick a stackabale item for example a gold nugget, then the item gets removed from your inventory, like you're "paying" an item, in this example a gold nugget.

**SHIFT + RIGHT CLICK to sort own inventory**

![Sorting Own Inventory ](https://github.com/tom2208/ChestCleaner/blob/master/assets/gif/cleaningitem_sort_own_inventory.gif?raw=true)

### Properties
When you change the item's name or lore, the previous cleaningitems will not work anymore. For example, first you have iron_hoe as cleaningitem. Every iron_hoe works as cleaningitem. Then you give it a name, like "mycleaningtool". Now all iron_hoes that don't have that name will not work anymore.

### Durability
You can activate or deactivate the cleaningitem durabilityLoss using the cleaningitem durabilityLoss toggle. 
When it is active, using the cleaningItem will reduce its durability, if it has one (eg a tool), or remove the item altogether if doesn't have durability, like paying with the item for sorting. 

**Sorting with an stackable item (durabilityLoss=true)**

![Sorting With Item](https://github.com/tom2208/ChestCleaner/blob/master/assets/gif/cleaningitem_item_sorting.gif?raw=true) 

**Sorting with a tool (durabilityLoss=true)**

![Sorting With Tool](https://github.com/tom2208/ChestCleaner/blob/master/assets/gif/cleaningitem_tool_sorting.gif?raw=true)

### Disable
You can disable the cleaningitem by setting the cleaningitem active toggle (in config.yml or with command). Then only [Autosorting](Autosort) and the [/cleaninventory](#CleanInventory-Commands) command work for sorting.

### openEvent
There are two modes for the cleaningitem. Which mode is used is set by the cleaningitem.openEvent toggle.

When the openEvent mode is **true** the plugin will start sorting when you have opened an inventory, as a player, while holding your cleaning item. Thats usefull if you dont want other players to sort the chests of others, because you will only be able to sort inventories you are able to open. The disadvantage is: that mode has some conflicts with other plugins, inventories that are opened by other plugins will get sorted.

When the openEvent mode is **false** the plugin sorts the block the player is looking on, like the **cleaninventory** command. The problem is, players are able to sort every chest.

Technically:
* When openEvent is **true**, the sorting will take place in the InventoryOpenEvent.
* When openEvent is **false**, the sorting will take place in the PlayerInteractEvent.

 Permission for to change the toggle: `chestcleaner.cmd.admin.cleaningitem.setopenevent`

 ## Commands

| Command                                     | Explanation                                                  | Permission                                            |
| ------------------------------------------- | ------------------------------------------------------------ | ----------------------------------------------------- |
| /cleaningitem get                           | Gives you a cleaning item.                                   | chestcleaner.cmd.cleaningitem.get                     |
| /cleaningitem give \<player/@all\>          | Give a player or all players (@all) a cleaning item.         | chestcleaner.cmd.cleaningitem.give                    |
| /cleaningitem set                           | Sets the cleaning item to the item you are holding.          | chestcleaner.cmd.admin.cleaningitem.setitem           |
| /cleaningitem name \<name\>                 | Sets the name of the cleaning item. (Does not rename existing cleaning items, that means they will not work anymore after running this command). | chestcleaner.cmd.admin.cleaningitem.rename            |
| /cleaningitem lore \<lore\>                 | Sets the lore of the cleaning item. (Does not change the lore of existing cleaning items, that means they will not work anymore after running this command). | chestcleaner.cmd.admin.cleaningitem.setlore           |
| /cleaningitem active \<true/false\>         | Activates or deactivates the cleaning item (does not remove the existing items, they will just lose their functionality while deactivated). | chestcleaner.cmd.admin.cleaningitem.setactive         |
| /cleaningitem durabilityLoss \<true/false\> | Activates or deactivates the durability loss of the cleaning item. Durability loss means: after using your cleaning item it will lose durability if possible, otherwise it gets removed form the inventory. | chestcleaner.cmd.admin.cleaningitem.setdurabilityloss |
| /cleaningitem openEvent \<true/false\>      | Activates or deactivates the openEvent.                      | chestcleaner.cmd.admin.cleaningitem.setopenevent      |

## Permissions

| Permission                                            | Explanation                                                  |
| ----------------------------------------------------- | ------------------------------------------------------------ |
| chestcleaner.cmd.cleaningitem.get                     | Allows you to get a cleaning item with the /cleaningitem command. |
| chestcleaner.cmd.cleaningitem.give                    | Allows you to give a player or all players a cleaning item with the /cleaningitem command. |
| chestcleaner.cmd.admin.cleaningitem.setitem           | Allows you to use the /cleaningitem set command.             |
| chestcleaner.cmd.admin.cleaningitem.rename            | Allows you to use the /cleaningitem name command.            |
| chestcleaner.cmd.admin.cleaningitem.setlore           | Allows you to use the /cleaningitem lore command.            |
| chestcleaner.cmd.admin.cleaningitem.setactive         | Allows you to use the /cleaningitem active command.          |
| chestcleaner.cmd.admin.cleaningitem.setdurabilityloss | Allows you to use the /cleaningitem durabilityloss command.  |
| chestcleaner.cmd.admin.cleaningitem.setopenevent      | Allows you to use the /cleaningitem openevent command.       |
| chestcleaner.cmd.admin.cleaningitem.*                 | Allows you to use all cleaning item commands.                |

## Config
* the cleaningitem is saved/configured in config.yml
```yaml
cleaningItem:
  active: true
  durability: true
  openEvent: true
  item:
    ==: org.bukkit.inventory.ItemStack
    v: 2230
    type: IRON_HOE
    meta:
      ==: ItemMeta
      meta-type: UNSPECIFIC
      display-name: example name
      lore:
      - example lore
```

# CleanInventory Commands

## Description
This commands sorts an inventory that is associated with a block (like a chest).

**Be careful when using a protection plugin, as this command allows a player to sort any block in any world.**

## Commands

| Command                                     | Explanation                                                  | Permission                             |
| ------------------------------------------- | ------------------------------------------------------------ | -------------------------------------- |
| /cleaninventory                             | Sorts the inventory of the block you're look at, if it has one. Has a range of 12 blocks, thus if you are too far away from a block with an inventory, it won't sort it. | chestcleaner.cmd.cleaninventory.sort   |
| /cleaninventory \<x> \<y> \<z>              | Sorts the inventory of the block in the location x/y/z (x,y,z are integers) in the world you currently are (nether for example) if that block has an inventory. _Example: `/cleaninventory 234 34 -34`_ | chestcleaner.cmd.cleaninventory.sort   |
| /cleaninventory \<x> \<y> \<z> \<worldname> | Sorts the inventory of the block in the location x/y/z (x,y,z are integers) in the world with the name \<worldname\> if that block has an inventory. _Example: `/cleaninventory 234 34 -34 world_nether`_ | chestcleaner.cmd.cleaninventory.sort   |
| /cleaninventory own                         | Sorts your own inventory (but not the hotbar).               | chestcleaner.cmd.cleaninventory.own    |
| /cleaninventory \<playername>               | Sorts the inventory of the player with the name \<playername>. | chestcleaner.cmd.cleaninventory.others |

## Permissions
| Permission                             | Explanation                                                  |
| -------------------------------------- | ------------------------------------------------------------ |
| chestcleaner.cmd.cleaninventory.sort   | Allows you to sort every block with an inventory using /cleaninventory. |
| chestcleaner.cmd.cleaninventory.own    | Allows you to sort your own inventory using `/cleaninventory own` |
| chestcleaner.cmd.cleaninventory.others | Allows you to sort the inventory of every player using /cleaninventory. |

# SortingAdmin Command

## Description
ChestCleaner has server defaults for all configuration options. 
Players with certain permissions can overwrite some of these defaults for themself.
These player settings will not affect the server defaults or other players.
Following settings may be changed by players:

Most of the default server settings can be changed using the `/sortingadmin` command, whereas the player settings can be changed using the `/sortingconfig` command.


## Commands
| Command                                                    | Explanation                                                  | Permission                                                   |
| ---------------------------------------------------------- | ------------------------------------------------------------ | ------------------------------------------------------------ |
| /sortingadmin autosort                                     | Tells you the current configuration for autosort of the server. | chestcleaner.cmd.admin.config                                |
| /sortingadmin autosort \<true/false>                       | Sets the autosort toggle for the server. It will affect every player that hasn't overwritten the toggle for themself (using /sortingconfig). | chestcleaner.cmd.admin.config                                |
| /sortingadmin pattern                                      | Tells you the current configuration for the default pattern of the server. | chestcleaner.cmd.admin.config                                |
| /sortingadmin pattern \<pattern>                           | Sets the default pattern for the server. This will affect every player that hasn't overwritten the pattern for themself (using /sortingconfig). | chestcleaner.cmd.admin.config                                |
| /sortingadmin categories                                   | Tells the default category order.                            | chestcleaner.cmd.admin.config                                |
| /sortingadmin categories set \<name0> \<name1> ...         | Set the default category order.                              | chestcleaner.cmd.admin.config                                |
| /sortingadmin categories addFromBook                       | Adds the category from the book in hand.                     | chestcleaner.cmd.admin.config                                |
| /sortingadmin categories getAsBook \<name>                 | Drops you the category with the name \<name> as a book.      | chestcleaner.cmd.admin.config                                |
| /sortingadmin cooldown \<cooldown>                         | Gives you the current configuration of the cooldown with the id \<cooldown>. | chestcleaner.cmd.admin.config                                |
| /sortingadmin cooldown \<cooldown> active                  | Tells you if the configuration for the cooldown with the id \<cooldown> is active or not. | chestcleaner.cmd.admin.config                                |
| /sortingadmin cooldown \<cooldown> active \<true/false>    | Actrivates (true) or deactivates the cooldown with the id \<cooldown>. | chestcleaner.cmd.admin.config **and** chestcleaner.cmd.admin.cooldown |
| /sortingadmin cooldown \<cooldown> set timeInMilliseconds  | Sets the cooldown with the id \<cooldown> in milliseconds (1second = 1000 milliseconds). | chestcleaner.cmd.admin.config                                |
| /sortingadmin chatNotification                             | Tells you if the chat notifications are enabled.             | chestcleaner.cmd.admin.config                                |
| /sortingadmin chatNotification \<true/false>               | Enables or disables the chat notifications.                  | chestcleaner.cmd.admin.config                                |
| /sortingadmin sortingSound active                          | Tells you if the sorting sound is active.                    | chestcleaner.cmd.admin.config                                |
| /sortingadmin sortingSound active <true/false>             | Activates or deactivates the sorting sound.                  | chestcleaner.cmd.admin.config                                |
| /sortingadmin sortingSound set \<sound>                    | Sets the sorting sound to the sound with the name \<sound>   | chestcleaner.cmd.admin.config                                |
| /sortingadmin sortingSound set \<sound> \<volume>          | Sets the sorting sound to the sound with the name \<sound> with the volume \<volume>. | chestcleaner.cmd.admin.config                                |
| /sortingadmin sortingSound set \<sound> \<volume> \<pitch> | Sets the sorting sound to the sound with the name \<sound> with the volume \<volume> and the pitch \<pitch>. | chestcleaner.cmd.admin.config                                |
| /sortingadmin clickSort                                    | Tells you if the click sort is active.                       | chestcleaner.cmd.admin.config                                |
| /sortingadmin clickSort \<true/false>                      | Activates or deactivates click sort.                         | chestcleaner.cmd.admin.config                                |
| /sortingadmin refill \<type>                               | Tells you if a type of auto refill is active or not (server default). | chestcleaner.cmd.admin.config                                |
| /sortingadmin refill \<type> \<true/false>                 | Activates or deactivates the auto refill with the type \<type> (server default). | chestcleaner.cmd.admin.config                                |
| /sortingadmin refill \<true/false>                         | Activates or deactivates the auto refill of all types (server default). | chestcleaner.cmd.admin.config                                |

## Permissions
| Permission                      | Explanation                                |
| ------------------------------- | ------------------------------------------ |
| chestcleaner.cmd.admin.config   | Allows you to configure nearly everything. |
| chestcleaner.cmd.admin.cooldown | Allows you to to set the cooldowns         |

# SortingConfig Command

## Description
This command lets the user make his individual configurations. This configurations overrides the server defaults you can configure with the `/sortingconfig` command (for more read [administration](Administration)).

## Commands
| Command                                             |                         Explanation                          | Permission                                                   |
| --------------------------------------------------- | :----------------------------------------------------------: | ------------------------------------------------------------ |
| /sortingconfig autosort                             |      Tells you your current configuration for autosort.      |                                                              |
| /sortingconfig autosort \<true/false>               |                  Sets your autosort toggle.                  | chestcleaner.cmd.sortingconfig.setautosort                   |
| /sortingconfig pattern                              |                Tells you the current pattern.                |                                                              |
| /sortingconfig pattern \<pattern>                   |                      Sets your pattern.                      | chestcleaner.cmd.sortingconfig.pattern                       |
| /sortingconfig categories                           |                  Tells your category order.                  |                                                              |
| /sortingconfig categories set \<name0> \<name1> ... |               Set your default category order.               | chestcleaner.cmd.sortingconfig.categories                    |
| /sortingconfig categories reset                     | Restets your category configurations to the server default.  | chestcleaner.cmd.sortingconfig.categories.reset              |
| /sortingconfig categories list                      |   Prints out the first page of the list of all categories.   |                                                              |
| /sortingconfig categories list \<page>              | Prints out the \<page>. page of the list of all categories.  |                                                              |
| /sortingconfig chatNotification                     |      Tells you if your chat notifications are disabled.      |                                                              |
| /sortingconfig chatNotification \<true/false>       |       Activates or deactives your chat notifications.        | chestcleaner.cmd.sortingconfig.setchatnotification           |
| /sortingconfig refill \<type>                       |     Tells you if a type of auto refill is active or not.     |                                                              |
| /sortingconfig refill \<type> \<true/false>         | Activates or deactivates the auto refill with the type \<type>. | chestcleaner.cmd.sortingconfig.refill.blocks **or** chestcleaner.cmd.sortingconfig.refill.breakables **or** chestcleaner.cmd.sortingconfig.refill.consumables |
| /sortingconfig refill \<true/false>                 |    Activates or deactivates the auto refill of all types.    | chestcleaner.cmd.sortingconfig.refill.blocks **and** chestcleaner.cmd.sortingconfig.refill.breakables **and** chestcleaner.cmd.sortingconfig.refill.consumables |
| /sortingconfig sortingSound                         |          Tells you if the sorting sound is actived.          |                                                              |
| /sortingconfig sortingSound \<true/false>           |     Activates or deactivates the sorting sound for you.      | chestcleaner.cmd.sortingconfig.setsortingsound               |
| /sortingconfig clickSort                            |           Tells you if the click sort is actived.            |                                                              |
| /sortingconfig clickSort \<true/false>              |         Activates or deactivates click sort for you.         | chestcleaner.cmd.sortingconfig.clicksort                     |
| /sortingconfig reset                                |            Resets your individual configurations.            | chestcleaner.cmd.sortingconfig.reset                         |
