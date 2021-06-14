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

## Description
You can configure an Item to be used as cleaning item. When you hold that item and: 
* `right-click` on a block with an inventory, that blocks inventory will get sorted.
* `shift + right-click`, your Inventory gets sorted.

The cleaningitem is configured for the whole server. 
You can select a craftable or stackable item for your cleaning item. If you do so players can craft their items themself, using sources to "pay" for the sorting, like the gold for the golden hoe.
If you pick a stackabale item for example a gold nugget, then the item gets removed from your inventory, like you're "paying" an item, in this example a gold nugget.

**SHIFT + RIGHT CLICK to sort own inventory**

![Sorting Own Inventory ](https://github.com/tom2208/ChestCleaner/blob/master/gifs/cleaningitem_sort_own_inventory.gif)

### Properties
When you change the item's name or lore, the previous cleaningitems will not work anymore. For example, first you have iron_hoe as cleaningitem. Every iron_hoe works as cleaningitem. Then you give it a name, like "mycleaningtool". Now all iron_hoes that don't have that name will not work anymore.

### Durability
You can activate or deactivate the cleaningitem durabilityLoss using the cleaningitem durabilityLoss toggle. 
When it is active, using the cleaningItem will reduce its durability, if it has one (eg a tool), or remove the item altogether if doesn't have durability, like paying with the item for sorting. 

**Sorting with an stackable item (durabilityLoss=true)**

![Sorting With Item](https://github.com/tom2208/ChestCleaner/blob/master/gifs/cleaningitem_item_sorting.gif) 

**Sorting with a tool (durabilityLoss=true)**

![Sorting With Tool](https://github.com/tom2208/ChestCleaner/blob/master/gifs/cleaningitem_tool_sorting.gif)

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
