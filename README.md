# Enchantment Disabler
Enchantment Disabler is a Fabric mod that effectively disables enchantments by preventing them from appearing anywhere in the game, including:
- Loot chests
- Villager trades
- Enchantment table
## Configuration
The configuration file is located in `.minecraft/config/enchantmentdisabler.json`.

It lists all enchantments currently registered in the game (including modded ones).

Control whether or not you want to prevent an enchantment from showing up in the game by changing the `disabled` value to either `true` or `false`.

Enchantments which are disabled by default are Mending, Thorns, Curse of Vanishing and Curse of Binding (personal preference, you can change this, of course).

The modified config will only be applied after restarting the game.
