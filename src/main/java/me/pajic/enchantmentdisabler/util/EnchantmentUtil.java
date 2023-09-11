package me.pajic.enchantmentdisabler.util;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.EnchantmentLevelEntry;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;
import java.util.Map;

public class EnchantmentUtil {

    public static List<Enchantment> ENCHANTMENT_BLACKLIST;

    public static void removeEnchantmentsFromItem(ItemStack stack, CallbackInfoReturnable<ItemStack> cir) {
        Map<Enchantment, Integer> enchantments = EnchantmentHelper.get(stack);

        if (!enchantments.isEmpty()) {
            if (ENCHANTMENT_BLACKLIST.stream().anyMatch(enchantments::containsKey)) {
                ENCHANTMENT_BLACKLIST.forEach(enchantments.keySet()::remove);
                EnchantmentHelper.set(enchantments, stack);
            }
        }

        cir.setReturnValue(stack);
    }

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
