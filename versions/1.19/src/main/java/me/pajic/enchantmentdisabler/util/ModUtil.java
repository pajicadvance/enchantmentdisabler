package me.pajic.enchantmentdisabler.util;

import me.pajic.enchantmentdisabler.Main;
import net.minecraft.core.Registry;
import net.minecraft.world.item.enchantment.Enchantment;

import java.util.List;

public class ModUtil {

    public static int getMaxLevel(Enchantment enchantment) {
        for (String entry : Main.CONFIG.maxLevelNest.maxLevels()) {
            String[] enchantmentLevelPair = entry.split("/", 2);
            if (enchantmentLevelPair[0].equals(Registry.ENCHANTMENT.getKey(enchantment).toString())) {
                return Integer.parseInt(enchantmentLevelPair[1]);
            }
        }
        return enchantment.getMaxLevel();
    }

    public static List<Enchantment> filterList(List<Enchantment> list) {
        return list.stream().filter(enchantment ->
                !Main.CONFIG.disabledEnchantments().contains(
                        Registry.ENCHANTMENT.getKey(enchantment).toString()
                )
        ).toList();
    }
}
