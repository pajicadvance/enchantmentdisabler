package me.pajic.enchantmentdisabler.util;

import me.pajic.enchantmentdisabler.config.ModCommonConfig;
import net.minecraft.core.component.DataComponents;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.ItemEnchantments;
import net.objecthunter.exp4j.Expression;
import net.objecthunter.exp4j.ExpressionBuilder;

public class ModUtil {

    public static boolean filterStacks(ItemStack stack) {
        for (String s : ModCommonConfig.disabledEnchantments) {
            ItemEnchantments storedEnchantments = stack.get(DataComponents.STORED_ENCHANTMENTS);
            if (storedEnchantments != null && storedEnchantments.keySet().stream().anyMatch(holder -> holder.is(ResourceLocation.parse(s)))) {
                return true;
            }
        }
        return false;
    }

    public static int evaluateFormulaAndReturnValue(String formula, int id) {
        Expression expression = new ExpressionBuilder(formula)
                .variable("id")
                .build().setVariable("id", id);
        if (expression.validate().isValid()) {
            return (int) expression.evaluate();
        }
        return -1;
    }
}
