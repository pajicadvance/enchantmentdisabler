package me.pajic.enchantmentdisabler.mixin.loot;

import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import me.pajic.enchantmentdisabler.util.ModUtil;
import net.minecraft.core.Holder;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.ItemEnchantments;
import net.minecraft.world.item.enchantment.providers.EnchantmentsByCostWithDifficulty;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(EnchantmentsByCostWithDifficulty.class)
public class EnchantmentsByCostWithDifficultyMixin {

	@WrapWithCondition(
			method = "enchant",
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/world/item/enchantment/ItemEnchantments$Mutable;upgrade(Lnet/minecraft/core/Holder;I)V"
			)
	)
	private boolean filterEnchantments(ItemEnchantments.Mutable instance, Holder<Enchantment> enchantment, int level) {
		return ModUtil.filter(enchantment, sources -> !sources.loot.get());
	}
}
