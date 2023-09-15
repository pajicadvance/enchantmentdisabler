package me.pajic.enchantmentdisabler.util;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentLevelEntry;

import java.util.List;

public class EnchantmentUtil {

    public static List<Enchantment> ENCHANTMENT_BLACKLIST;

    public static boolean preventEnchantmentAdditionToList(List<EnchantmentLevelEntry> list, EnchantmentLevelEntry entry) {

        if (ENCHANTMENT_BLACKLIST.contains(entry.enchantment)) {
            return false;
        }
        else {
            return list.add(entry);
        }
    }

    public static List<Enchantment> removeEnchantmentsFromList(List<Enchantment> list) {
        return list.stream().filter(enchantment -> !EnchantmentUtil.ENCHANTMENT_BLACKLIST.contains(enchantment)).toList();
    }
}
