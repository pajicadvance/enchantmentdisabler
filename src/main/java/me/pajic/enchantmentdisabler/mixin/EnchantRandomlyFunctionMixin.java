package me.pajic.enchantmentdisabler.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import me.pajic.enchantmentdisabler.Main;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.EnchantmentTags;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.level.storage.loot.functions.EnchantRandomlyFunction;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Mixin(EnchantRandomlyFunction.class)
public class EnchantRandomlyFunctionMixin {

    @Shadow @Final private Optional<HolderSet<Enchantment>> options;

    @ModifyArg(
            method = "run",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/Util;getRandomSafe(Ljava/util/List;Lnet/minecraft/util/RandomSource;)Ljava/util/Optional;"
            ),
            index = 0
    )
    private List<Holder<Enchantment>> filter(List<Holder<Enchantment>> selections) {
        if (Main.CONFIG.disabler.disablerEnabled.get()) {
            if (options.isEmpty()) {
                return selections.stream().filter(holder -> holder.is(EnchantmentTags.ON_RANDOM_LOOT)).toList();
            }
            else {
                return selections.stream().filter(holder -> Main.CONFIG.disabler.disabledEnchantments.stream().noneMatch(holder::is)).toList();
            }
        }
        return selections;
    }

    @ModifyExpressionValue(
            method = "enchantItem",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/item/enchantment/Enchantment;getMaxLevel()I"
            )
    )
    private static int limitEnchantmentLevel(int original, @Local(argsOnly = true) Holder<Enchantment> enchantment) {
        if (Main.CONFIG.maxLevel.limitObtainableEnchantmentLevel.get()) {
            for (Map.Entry<ResourceLocation, Integer> entry : Main.CONFIG.maxLevel.obtainableEnchantmentLevels.entrySet()) {
                if (enchantment.is(entry.getKey())) {
                    if (original > entry.getValue()) return entry.getValue();
                }
            }
        }
        return original;
    }
}