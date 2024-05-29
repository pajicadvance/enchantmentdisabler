package me.pajic.enchantmentdisabler.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import me.pajic.enchantmentdisabler.Main;
import me.pajic.enchantmentdisabler.util.ModUtil;
import net.minecraft.core.Holder;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.level.storage.loot.functions.EnchantRandomlyFunction;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

import java.util.List;

@Mixin(EnchantRandomlyFunction.class)
public class EnchantRandomlyFunctionMixin {

    // Reroll enchantment if blacklisted, cancel enchanting if no possible enchantments found
    @WrapOperation(
            method = "run",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/storage/loot/functions/EnchantRandomlyFunction;enchantItem(Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/item/enchantment/Enchantment;Lnet/minecraft/util/RandomSource;)Lnet/minecraft/world/item/ItemStack;")
    )
    public ItemStack modifyEnchantmentList(ItemStack stack, Enchantment enchantment, RandomSource random, Operation<ItemStack> original) {
        if (Main.CONFIG.disablerEnabled()) {
            Enchantment rerolledEnchantment = ModUtil.rerollEnchantmentIfBlacklisted(stack, enchantment, random);
            if (rerolledEnchantment != null) {
                return original.call(stack, rerolledEnchantment, random);
            }
            return stack;
        }
        return original.call(stack, enchantment, random);
    }

    // Modifies the maximum enchantment level
    @WrapOperation(
            method = "enchantItem",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/util/Mth;nextInt(Lnet/minecraft/util/RandomSource;II)I")
    )
    private static int setMaxEnchantmentLevel(RandomSource random, int minimum, int maximum, Operation<Integer> original,
                                              @Local(argsOnly = true) Enchantment enchantment) {
        if (Main.CONFIG.maxLevelNest.modifyMaxLevels()) {
            int maxLevel = ModUtil.getMaxLevel(enchantment);
            if (maxLevel > 0) {
                return original.call(random, minimum, maxLevel);
            }
        }
        return original.call(random, minimum, maximum);
    }
}