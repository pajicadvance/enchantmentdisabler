package me.pajic.enchantmentdisabler.mixin.loot;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import me.pajic.enchantmentdisabler.util.ModUtil;
import net.minecraft.core.Holder;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.ItemEnchantments;
import net.minecraft.world.level.storage.loot.functions.SetEnchantmentsFunction;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(SetEnchantmentsFunction.class)
public class SetEnchantmentsFunctionMixin {

	@WrapWithCondition(
			method = {"lambda$run$1", "lambda$run$2", "lambda$run$4", "lambda$run$5", "method_57656", "method_60297"},
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/world/item/enchantment/ItemEnchantments$Mutable;set(Lnet/minecraft/core/Holder;I)V"
			)
	)
	private static boolean preventSetEnchantmentIfDisabled(ItemEnchantments.Mutable instance, Holder<Enchantment> enchantment, int level) {
		return ModUtil.filter(enchantment, sources -> !sources.loot.get());
	}

	@ModifyReturnValue(
			method = "run",
			at = @At("RETURN")
	)
	private ItemStack returnBookIfNoEnchantments(ItemStack original) {
		return original.is(Items.ENCHANTED_BOOK) && !EnchantmentHelper.hasAnyEnchantments(original) ? new ItemStack(Items.BOOK) : original;
	}
}
