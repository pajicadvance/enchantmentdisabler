package me.pajic.enchantmentdisabler.mixin.loot;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import me.pajic.enchantmentdisabler.ED;
import me.pajic.enchantmentdisabler.util.ModUtil;
import net.minecraft.core.HolderSet;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.level.storage.loot.functions.EnchantWithLevelsFunction;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

import java.util.Optional;

@Mixin(EnchantWithLevelsFunction.class)
public class EnchantWithLevelsFunctionMixin {

	@SuppressWarnings("OptionalUsedAsFieldOrParameterType")
	@ModifyExpressionValue(
			method = "run",
			at = @At(
					value = "FIELD",
					target = "Lnet/minecraft/world/level/storage/loot/functions/EnchantWithLevelsFunction;options:Ljava/util/Optional;",
					opcode = Opcodes.GETFIELD
			)
	)
	private Optional<HolderSet<Enchantment>> filterEnchantments(Optional<HolderSet<Enchantment>> original) {
		return original.map(holders ->
				HolderSet.direct(holders.stream().filter(enchantment ->
						ModUtil.filter(enchantment, sources -> !sources.loot.get())).toList()
				)
		);
	}

	@ModifyArg(
			method = "run",
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/world/item/enchantment/EnchantmentHelper;enchantItem(Lnet/minecraft/util/RandomSource;Lnet/minecraft/world/item/ItemStack;ILnet/minecraft/core/RegistryAccess;Ljava/util/Optional;)Lnet/minecraft/world/item/ItemStack;"
			),
			index = 2
	)
	private int modifyMaxEnchantmentLevel(int level) {
		if (ED.CONFIG.loot.modifyEnchantWithLevelsMaxPower.get() && level > ED.CONFIG.loot.enchantWithLevelsMaxPower.get()) {
			return ED.CONFIG.loot.enchantWithLevelsMaxPower.get();
		}
		return level;
	}
}
