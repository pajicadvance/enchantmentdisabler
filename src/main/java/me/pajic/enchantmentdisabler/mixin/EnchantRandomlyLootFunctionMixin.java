package me.pajic.enchantmentdisabler.mixin;

import me.pajic.enchantmentdisabler.util.EnchantmentUtil;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.context.LootContext;
import net.minecraft.loot.function.EnchantRandomlyLootFunction;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(EnchantRandomlyLootFunction.class)
public class EnchantRandomlyLootFunctionMixin {

    @Inject(method = "process", at = @At(value = "RETURN"), cancellable = true)
    private void modifyReturnedItem(ItemStack stack, LootContext context, CallbackInfoReturnable<ItemStack> cir) {
        EnchantmentUtil.removeEnchantmentsFromItem(stack, cir);
    }
}
