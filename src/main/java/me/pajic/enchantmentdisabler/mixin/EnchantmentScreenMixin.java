package me.pajic.enchantmentdisabler.mixin;

import com.llamalad7.mixinextras.expression.Expression;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import me.pajic.enchantmentdisabler.Main;
import net.minecraft.client.gui.screens.inventory.EnchantmentScreen;
import net.objecthunter.exp4j.ExpressionBuilder;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(EnchantmentScreen.class)
public class EnchantmentScreenMixin {

    @Expression("? < ? + 1")
    @ModifyExpressionValue(
            method = "renderBg",
            at = @At("MIXINEXTRAS:EXPRESSION")
    )
    private boolean modifyLapisCost(boolean original, @Local(ordinal = 4) int k, @Local(ordinal = 5) int l) {
        if (Main.CONFIG.enchantingTable.modifyLapisCost()) {
            net.objecthunter.exp4j.Expression expression = new ExpressionBuilder(Main.CONFIG.enchantingTable.lapisCostFormula())
                    .variable("id")
                    .build().setVariable("id", l);
            if (expression.validate().isValid()) {
                int newValue = (int) expression.evaluate();
                if (newValue > 0) {
                    return k < newValue;
                }
                else {
                    return original;
                }
            }
        }
        return original;
    }
}
