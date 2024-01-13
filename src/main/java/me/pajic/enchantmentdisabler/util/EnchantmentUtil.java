package me.pajic.enchantmentdisabler.util;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentLevelEntry;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.util.math.random.Random;

import java.util.ArrayList;
import java.util.List;

public class EnchantmentUtil {
    public static List<Enchantment> ENCHANTMENT_BLACKLIST = new ArrayList<>();

    public static boolean preventEnchantmentAdditionToListIfBlacklisted(List<EnchantmentLevelEntry> list, EnchantmentLevelEntry entry) {
        if (ENCHANTMENT_BLACKLIST.contains(entry.enchantment)) {
            return false;
        }

        return list.add(entry);
    }

    public static List<Enchantment> removeEnchantmentsFromList(List<Enchantment> list) {
        return new ArrayList<>(list.stream().filter(enchantment -> !ENCHANTMENT_BLACKLIST.contains(enchantment)).toList());
    }

    public static Enchantment rerollEnchantmentIfBlacklisted(ItemStack stack, Enchantment enchantment, Random random) {
        if (ENCHANTMENT_BLACKLIST.contains(enchantment)) {
            List<Enchantment> compatibleEnchantments = Registries.ENCHANTMENT.stream().filter(e -> e.isAcceptableItem(stack) && !ENCHANTMENT_BLACKLIST.contains(e)).toList();

            if (compatibleEnchantments.isEmpty()) {
                return null;
            }

            enchantment = compatibleEnchantments.get(random.nextInt(compatibleEnchantments.size()));
        }

        return enchantment;
    }
}
