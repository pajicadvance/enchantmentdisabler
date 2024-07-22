package me.pajic.enchantmentdisabler.util;

import me.pajic.enchantmentdisabler.Main;
import net.minecraft.core.component.DataComponents;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
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
}