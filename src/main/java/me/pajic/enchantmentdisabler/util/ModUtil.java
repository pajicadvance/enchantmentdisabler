package me.pajic.enchantmentdisabler.util;

import me.pajic.enchantmentdisabler.config.ModCommonConfig;
import net.minecraft.ResourceLocationException;
import net.minecraft.core.component.DataComponents;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.ItemEnchantments;
import net.objecthunter.exp4j.Expression;
import net.objecthunter.exp4j.ExpressionBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public class ModUtil {

    private static final Logger LOGGER = LoggerFactory.getLogger("EnchantmentDisabler-ResourceParsing");

    private static final List<String> INVALID_ENTRIES = new ArrayList<>();

    public static boolean filterStacks(ItemStack stack) {
        for (String s : ModCommonConfig.disabledEnchantments) {
            ItemEnchantments storedEnchantments = stack.get(DataComponents.STORED_ENCHANTMENTS);
            if (storedEnchantments != null) {
                try {
                    if (storedEnchantments.keySet().stream().anyMatch(holder -> holder.is(ResourceLocation.parse(s))))
                        return true;
                } catch (ResourceLocationException e) {
                    handleResourceLocationException(s, e);
                }
            }
        }
        return false;
    }

    public static void handleResourceLocationException(String s, ResourceLocationException e) {
        if (!INVALID_ENTRIES.contains(s)) {
            LOGGER.error("[Enchantment Disabler] Failed to parse enchantment {}:", s);
            LOGGER.error(e.getMessage());
            LOGGER.error("Verify that enchantments added in the disabled enchantments list inside the mod config are valid.");
            INVALID_ENTRIES.add(s);
        }
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
