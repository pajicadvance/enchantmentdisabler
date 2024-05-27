package me.pajic.enchantmentdisabler.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.ref.LocalIntRef;
import me.pajic.enchantmentdisabler.Main;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.EnchantmentMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.objecthunter.exp4j.Expression;
import net.objecthunter.exp4j.ExpressionBuilder;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Slice;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(EnchantmentMenu.class)
public class EnchantmentMenuMixin {

    @Shadow @Final private RandomSource random;

    // Prevents enchanting table buttons from showing up if the item has no possible enchantments
    @ModifyExpressionValue(
            method = "slotsChanged",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/ItemStack;isEnchantable()Z")
    )
    private boolean dontUpdateIfNoEnchantmentsAvailable(boolean original, @Local ItemStack itemStack) {
        if (Main.CONFIG.disablerEnabled() && Main.CONFIG.enchantingTableNest.enchantingTableEnabled()) {
            return original &&
                    !EnchantmentHelper.getAvailableEnchantmentResults(
                            EnchantmentHelper.getEnchantmentCost(random, 2, 15, itemStack),
                            itemStack,
                            false
                    ).isEmpty();
        }
        else {
            return original;
        }
    }

    // Modifies the lapis cost of enchanting items
    @Inject(
            method = "clickMenuButton",
            slice = @Slice(
                    from = @At(value = "INVOKE", target = "Lnet/minecraft/world/Container;getItem(I)Lnet/minecraft/world/item/ItemStack;", ordinal = 0),
                    to = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/ItemStack;getCount()I", ordinal = 0)
            ),
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/ItemStack;isEmpty()Z")
    )
    private void modifyLapisCost(Player player, int id, CallbackInfoReturnable<Boolean> cir, @Local(ordinal = 1) LocalIntRef i) {
        if (Main.CONFIG.enchantingTableNest.modifyLapisCost()) {
            Expression expression = new ExpressionBuilder(Main.CONFIG.enchantingTableNest.lapisCostFormula())
                    .variable("id")
                    .build().setVariable("id", id);
            if (expression.validate().isValid()) {
                int newValue = (int) expression.evaluate();
                if (newValue > 0) {
                    i.set(newValue);
                }
                else {
                    i.set(1);
                }
            }
        }
    }
}