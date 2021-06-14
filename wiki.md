## Blacklist Commands

| Command                                        | Explanation                                                  | Permission                       |
| ---------------------------------------------- | ------------------------------------------------------------ | -------------------------------- |
| /blacklist \<blacklist\> add                   | Adds the material of the item you are holding in your main hand to the specified blacklist. | chestcleaner.cmd.admin.blacklist |
| /blacklist \<blacklist\> add \<materialId\>    | Adds the specified material to the specified blacklist.      | chestcleaner.cmd.admin.blacklist |
| /blacklist \<blacklist\> remove                | Removes the material of the item you are holding in your main hand from the specified blacklist. | chestcleaner.cmd.admin.blacklist |
| /blacklist \<blacklist\> remove \<materialId\> | Removes the material of the item you are holding in your main hand from the specified blacklist. | chestcleaner.cmd.admin.blacklist |
| /blacklist \<blacklist\> list                  | Prints out the specified blacklist.                          | chestcleaner.cmd.admin.blacklist |
| /blacklist \<blacklist\> clear                 | Removes every entry of the specified blacklist.              | chestcleaner.cmd.admin.blacklist |

Permissions:

| Permission                       | Explanation                               |
| -------------------------------- | ----------------------------------------- |
| chestcleaner.cmd.admin.blacklist | Allows you to use all blacklist commands. |



| Permission                       | Explanation                                     |
| -------------------------------- | ----------------------------------------------- |
| chestcleaner.cmd.admin.blacklist | Allows the player to use all blacklist commands |

## CleanInventory Commands

| Command                                    | Explanation                                                  | Permission                             |
| ------------------------------------------ | ------------------------------------------------------------ | -------------------------------------- |
| /cleaninventory                            | Sorts the inventory you are looking at.                      | chestcleaner.cmd.cleaninventory.sort   |
| /cleaninventory \<x\> \<y\> \<z\>          | Sorts the inventory at the position specified by x y z.      | chestcleaner.cmd.cleaninventory.sort   |
| /cleaninventory \<x\> \<y\> \<z\> \<word\> | Sorts the inventory at the position specified by x y z and a world name (for instance "nether_world"). | chestcleaner.cmd.cleaninventory.sort   |
| /cleaninventory own                        | Sorts your own inventory.                                    | chestcleaner.cmd.cleaninventory.own    |
| /cleaninventory \<player>                  | Sorts the inventory of a player.                             | chestcleaner.cmd.cleaninventory.others |

Permissions:

| Permission                             | Explanation                                                  |
| -------------------------------------- | ------------------------------------------------------------ |
| chestcleaner.cmd.cleaninventory.sort   | Allows you to use /cleaninventory to sort block inventories in all world. |
| chestcleaner.cmd.cleaninventory.own    | Allows you to sort your own inventory with /cleaninventory.  |
| chestcleaner.cmd.cleaninventory.others | Allows you to sort player inventory with /cleaninventory.    |

## CleaningItem Commands

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

Permissions:

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

