package me.pajic.enchantmentdisabler.mixin;

import me.pajic.enchantmentdisabler.util.EnchantmentUtil;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.context.LootContext;
import net.minecraft.loot.function.EnchantRandomlyLootFunction;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

@Mixin(EnchantRandomlyLootFunction.class)
public class EnchantRandomlyLootFunctionMixin {

    @Unique
    private Enchantment e;

    @ModifyArgs(method = "process", at = @At(value = "INVOKE", target = "Lnet/minecraft/loot/function/EnchantRandomlyLootFunction;addEnchantmentToStack(Lnet/minecraft/item/ItemStack;Lnet/minecraft/enchantment/Enchantment;Lnet/minecraft/util/math/random/Random;)Lnet/minecraft/item/ItemStack;"))
    private void rerollEnchantment(Args args) {
        e = EnchantmentUtil.rerollEnchantmentIfBlacklisted(args.get(0), args.get(1), args.get(2));
        if (e != null) {
            args.set(1, e);
        }
    }

    @Inject(method = "process", at = @At(value = "RETURN", ordinal = 1), cancellable = true)
    private void skipEnchanting(ItemStack stack, LootContext context, CallbackInfoReturnable<ItemStack> cir) {
        if (e == null) {
            cir.setReturnValue(stack);
        }
    }
}
