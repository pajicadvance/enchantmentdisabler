package me.pajic.enchantmentdisabler.mixin;

import me.pajic.enchantmentdisabler.util.EnchantmentUtil;
import net.minecraft.loot.function.EnchantRandomlyLootFunction;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

@Mixin(EnchantRandomlyLootFunction.class)
public class EnchantRandomlyLootFunctionMixin {

    @ModifyArgs(method = "process", at = @At(value = "INVOKE", target = "Lnet/minecraft/loot/function/EnchantRandomlyLootFunction;addEnchantmentToStack(Lnet/minecraft/item/ItemStack;Lnet/minecraft/enchantment/Enchantment;Lnet/minecraft/util/math/random/Random;)Lnet/minecraft/item/ItemStack;"))
    private void modifyArgs(Args args) {
        args.set(1, EnchantmentUtil.rerollEnchantmentIfBlacklisted(args.get(0), args.get(1), args.get(2)));
    }
}
