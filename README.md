# Enchantment Disabler
Enchantment Disabler is a mod for Fabric and NeoForge that effectively disables enchantments specified by the config by preventing them from appearing anywhere in the game.

The mod doesn't technically remove enchantments from the game's registry, but rather prevents them from being added to tools, armor and enchanted books from loot chests, villager trades and the enchanting table, which is basically equivalent to playing the game without those enchantments. This approach should be entirely safe and mostly mod-compatible compared to actually deleting the enchantments from the game's registry.

**Enchantment disabling does NOT apply retroactively - enchantments you obtained before installing the mod will be unaffected.**

Disabled enchantments are also hidden from creative menus, [EMI](https://modrinth.com/mod/emi) and [REI](https://modrinth.com/mod/rei).

The mod also contains additional tweaks, which are disabled by default and fully configurable:

- Limit the maximum enchantment level for every enchantment
- Limit the amount of times enchanted book and item villager trades can be used
- Permanently disable restocking of enchanted book and item villager trades
- Entirely disable the enchanting table
- Limit the amount of bookshelves that the enchanting table can accept
- Modify the amount of lapis and XP levels required to enchant items in the enchanting table (Requires the mod on the client in order to correctly display the costs)
- Set a limit on how strong enchantments can be on looted equipment

## Requirements
Fabric version requires [Fabric API](https://modrinth.com/mod/fabric-api) and [Fzzy Config](https://modrinth.com/mod/fzzy-config). [ModMenu](https://modrinth.com/mod/modmenu) is required for accessing the in-game config menu.

NeoForge version requires [Fzzy Config](https://modrinth.com/mod/fzzy-config) and [Kotlin for Forge](https://modrinth.com/mod/kotlin-for-forge). The in-game configuration can be accessed using the built-in Mods menu.

## Version Support
- 3.0.0 for Minecraft 1.21+ is the current active and supported version.
- Older versions are unsupported.