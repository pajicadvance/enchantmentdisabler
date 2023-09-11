# Enchantment Disabler
Enchantment Disabler is a Fabric mod that effectively disables enchantments specified by the config by preventing them from appearing anywhere in the game.

The mod doesn't technically remove enchantments from the game's registry, but rather prevents them from being added to tools, armor and enchanted books from loot chests, villager trades and the enchanting table, which is basically equivalent to playing the game without those enchantments. This approach should be entirely safe and mostly mod-compatible compared to actually deleting the enchantments from the game's registry.
## Disclaimer
This is my first mod ever. While it works for my use case and in my limited testing, this does not mean that the mod is 100% bullet-proof, and I cannot guarantee that it does not contain major flaws or oversights. If you run into any problems, please open an issue report.
## Configuration
The configuration file is located in `.minecraft/config/enchantmentdisabler.json`.

It lists all enchantments currently registered in the game (including modded ones).

Control whether or not you want to prevent an enchantment from showing up in the game by changing the `disabled` value to either `true` or `false`.

Enchantments which are disabled by default are Mending, Thorns, Curse of Vanishing and Curse of Binding (personal preference, you can change this, of course).

The modified config will only be applied after restarting the game.
