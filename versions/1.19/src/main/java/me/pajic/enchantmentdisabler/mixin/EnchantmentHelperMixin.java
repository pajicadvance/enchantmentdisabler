package me.pajic.enchantmentdisabler.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import com.llamalad7.mixinextras.sugar.Local;
import me.pajic.enchantmentdisabler.Main;
import me.pajic.enchantmentdisabler.util.ModUtil;
import net.minecraft.core.Registry;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.EnchantmentInstance;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import java.util.List;

@Mixin(EnchantmentHelper.class)
public class EnchantmentHelperMixin {

    // Prevents addition of blacklisted enchantments to the list of possible enchantments when enchanting an item
    @WrapWithCondition(
            method = "getAvailableEnchantmentResults",
            at = @At(value = "INVOKE", target = "Ljava/util/List;add(Ljava/lang/Object;)Z")
    )
    private static boolean redirectListAdd(List<EnchantmentInstance> instance, Object o) {
        if (Main.CONFIG.disablerEnabled()) {
            EnchantmentInstance entry = (EnchantmentInstance) o;
            return !Main.CONFIG.disabledEnchantments().contains(
                    Registry.ENCHANTMENT.getKey(entry.enchantment).toString()
            );
        }
        else {
            return true;
        }
    }

    // Modifies the maximum enchantment level
    @ModifyExpressionValue(
            method = "getAvailableEnchantmentResults",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/enchantment/Enchantment;getMaxLevel()I")
    )
    private static int setMaxEnchantmentLevel(int original, @Local Enchantment enchantment) {
        if (Main.CONFIG.maxLevelNest.modifyMaxLevels()) {
            return Math.min(original, ModUtil.getMaxLevel(enchantment));
        }
        else {
            return original;
        }
    }
}