package me.pajic.enchantmentdisabler.mixin;

import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import me.pajic.enchantmentdisabler.Main;
import net.minecraft.core.Holder;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.ItemEnchantments;
import net.minecraft.world.level.storage.loot.functions.SetEnchantmentsFunction;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(SetEnchantmentsFunction.class)
public class SetEnchantmentFunctionMixin {

    @WrapWithCondition(
            method = {
                    "method_57656",
                    "method_60297"
            },
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/item/enchantment/ItemEnchantments$Mutable;set(Lnet/minecraft/core/Holder;I)V"
            )
    )
    private static boolean preventSetEnchantmentIfDisabled(ItemEnchantments.Mutable instance, Holder<Enchantment> enchantment, int level) {
        return Main.CONFIG.disabler.disabledEnchantments.stream().noneMatch(enchantment::is);
    }
}