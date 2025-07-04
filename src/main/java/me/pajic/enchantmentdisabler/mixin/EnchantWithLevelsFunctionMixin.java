package me.pajic.enchantmentdisabler.mixin;

import me.pajic.enchantmentdisabler.Main;
import net.minecraft.world.level.storage.loot.functions.EnchantWithLevelsFunction;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(EnchantWithLevelsFunction.class)
public class EnchantWithLevelsFunctionMixin {

    @ModifyArg(
            method = "run",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/item/enchantment/EnchantmentHelper;enchantItem(Lnet/minecraft/util/RandomSource;Lnet/minecraft/world/item/ItemStack;ILnet/minecraft/core/RegistryAccess;Ljava/util/Optional;)Lnet/minecraft/world/item/ItemStack;"
            ),
            index = 2
    )
    private int modifyMaxEnchantmentLevel(int level) {
        if (Main.CONFIG.loot.modifyEnchantWithLevelsMaxPower.get() && level > Main.CONFIG.loot.enchantWithLevelsMaxPower.get()) {
            return Main.CONFIG.loot.enchantWithLevelsMaxPower.get();
        }
        return level;
    }
}