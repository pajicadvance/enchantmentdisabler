package me.pajic.enchantmentdisabler.mixin;

import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import me.pajic.enchantmentdisabler.config.ModCommonConfig;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.ItemEnchantments;
import net.minecraft.world.level.storage.loot.functions.SetEnchantmentsFunction;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(SetEnchantmentsFunction.class)
public class SetEnchantmentFunctionMixin {

    @WrapWithCondition(
            method = {
                    "lambda$run$4",
                    "lambda$run$5"
            },
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/item/enchantment/ItemEnchantments$Mutable;set(Lnet/minecraft/core/Holder;I)V"
            )
    )
    private static boolean preventSetEnchantmentIfDisabled(ItemEnchantments.Mutable instance, Holder<Enchantment> enchantment, int level) {
        return ModCommonConfig.disabledEnchantments.stream().noneMatch(s -> enchantment.is(ResourceLocation.parse(s)));
    }
}