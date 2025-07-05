package me.pajic.enchantmentdisabler.util;

import me.pajic.enchantmentdisabler.Main;
import net.minecraft.core.component.DataComponents;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.ItemEnchantments;

import java.util.ArrayList;
import java.util.List;

public class ModUtil {
    public static List<ResourceLocation> registeredEnchantments = new ArrayList<>();

    public static boolean filterStacks(ItemStack stack) {
        for (ResourceLocation rl : Main.CONFIG.disabler.disabledEnchantments) {
            ItemEnchantments storedEnchantments = stack.get(DataComponents.STORED_ENCHANTMENTS);
            if (storedEnchantments != null) {
                if (storedEnchantments.keySet().stream().anyMatch(holder -> holder.is(rl))) return true;
            }
        }
        return false;
    }

    public static ItemEnchantments getItemEnchantments(ItemStack stack) {
        if (stack.has(DataComponents.STORED_ENCHANTMENTS)) return stack.get(DataComponents.STORED_ENCHANTMENTS);
        else return stack.get(DataComponents.ENCHANTMENTS);
    }
}
