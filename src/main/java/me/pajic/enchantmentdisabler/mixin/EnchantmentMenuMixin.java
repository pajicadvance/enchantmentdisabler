package me.pajic.enchantmentdisabler.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import me.pajic.enchantmentdisabler.Main;
import net.minecraft.util.RandomSource;
import net.minecraft.world.inventory.EnchantmentMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

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
}