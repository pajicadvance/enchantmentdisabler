package me.pajic.enchantmentdisabler.mixin.loot;

import com.llamalad7.mixinextras.sugar.Local;
import me.pajic.enchantmentdisabler.ED;
import me.pajic.enchantmentdisabler.util.ModUtil;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.EnchantmentTags;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.level.storage.loot.functions.EnchantRandomlyFunction;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Mixin(EnchantRandomlyFunction.class)
public class EnchantRandomlyFunctionMixin {

    @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
    @Shadow @Final private Optional<HolderSet<Enchantment>> options;

    @ModifyArg(
            method = "run",
            at = @At(
                    value = "INVOKE",
					target = "Lnet/minecraft/util/Util;getRandomSafe(Ljava/util/List;Lnet/minecraft/util/RandomSource;)Ljava/util/Optional;"
            ),
            index = 0
    )
    private List<Holder<Enchantment>> filter(List<Holder<Enchantment>> selections) {
        if (options.isEmpty()) return selections.stream().filter(enchantment ->
				enchantment.is(EnchantmentTags.ON_RANDOM_LOOT)
		).toList();
        return selections.stream().filter(enchantment ->
				ModUtil.filter(enchantment, sources -> !sources.loot.get())
        ).toList();
    }

	@ModifyArg(
			method = "enchantItem",
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/util/Mth;nextInt(Lnet/minecraft/util/RandomSource;II)I"
			),
			index = 2
	)
    private static int limitEnchantmentLevel(int original, @Local(argsOnly = true) Holder<Enchantment> enchantment) {
        if (ED.CONFIG.maxLevel.limitObtainableEnchantmentLevel.get()) {
            for (Map.Entry<Identifier, Integer> entry : ED.CONFIG.maxLevel.obtainableEnchantmentLevels.entrySet()) {
                if (enchantment.is(entry.getKey())) {
                    if (original > entry.getValue()) return entry.getValue();
                }
            }
        }
        return original;
    }
}
