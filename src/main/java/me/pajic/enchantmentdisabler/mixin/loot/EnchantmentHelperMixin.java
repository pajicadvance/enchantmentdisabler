package me.pajic.enchantmentdisabler.mixin.loot;

import com.llamalad7.mixinextras.sugar.Local;
import me.pajic.enchantmentdisabler.ED;
import me.pajic.enchantmentdisabler.util.ModUtil;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

import java.util.Optional;
import java.util.stream.Stream;

@Mixin(EnchantmentHelper.class)
public class EnchantmentHelperMixin {

	@SuppressWarnings("OptionalUsedAsFieldOrParameterType")
	@ModifyArg(
			method = "enchantItem(Lnet/minecraft/util/RandomSource;Lnet/minecraft/world/item/ItemStack;ILnet/minecraft/core/RegistryAccess;Ljava/util/Optional;)Lnet/minecraft/world/item/ItemStack;",
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/world/item/enchantment/EnchantmentHelper;enchantItem(Lnet/minecraft/util/RandomSource;Lnet/minecraft/world/item/ItemStack;ILjava/util/stream/Stream;)Lnet/minecraft/world/item/ItemStack;"
			),
			index = 3
	)
	private static Stream<Holder<Enchantment>> filterEnchantmentsIfNoOptionsProvided(
			Stream<Holder<Enchantment>> enchantments,
			@Local(argsOnly = true) Optional<? extends HolderSet<Enchantment>> possibleEnchantments
	) {
		if (possibleEnchantments.isEmpty() || possibleEnchantments.get().size() == 0) {
			ED.debugLog("[EnchantmentHelper] No options found, filtering fallback");
			return enchantments.filter(enchantment ->
					ModUtil.filter(enchantment, sources -> !sources.loot.get())
			);
		}
		return enchantments;
	}
}
