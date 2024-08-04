package me.pajic.enchantmentdisabler.util;

import me.pajic.enchantmentdisabler.Main;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.EnchantmentTags;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.ItemEnchantments;

public class ModUtil {

    public static boolean filterStacks(ItemStack stack) {
        for (String s : Main.CONFIG.disabledEnchantments()) {
            ItemEnchantments storedEnchantments = stack.get(DataComponents.STORED_ENCHANTMENTS);
            if (storedEnchantments != null && storedEnchantments.keySet().stream().anyMatch(holder -> holder.is(ResourceLocation.parse(s)))) {
                return true;
            }
        }
        return false;
    }

    public static void updateProtectionEnchantments(ItemEnchantments.Mutable existing, ItemEnchantments addition) {
        for (Holder<Enchantment> e : addition.keySet()) {
            if (e.is(EnchantmentTags.ARMOR_EXCLUSIVE)) {
                if (existing.getLevel(e) == 0) {
                    existing.set(e, addition.getLevel(e));
                }
                else {
                    existing.upgrade(e, addition.getLevel(e));
                }
            }
        }
    }
}