package me.pajic.enchantmentdisabler.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import me.pajic.enchantmentdisabler.ED;
import me.pajic.enchantmentdisabler.util.ModUtil;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.EnchantmentInstance;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import java.util.List;
import java.util.Map;

@Mixin(EnchantmentHelper.class)
public class EnchantmentHelperMixin {

	@WrapOperation(
			method = {"lambda$getAvailableEnchantmentResults$1", "lambda$getAvailableEnchantmentResults$0", "method_60106"},
			at = @At(
					value = "INVOKE",
					target = "Ljava/util/List;add(Ljava/lang/Object;)Z"
			)
	)
	private static <E> boolean preventAdditionIfAllDisabled(List<EnchantmentInstance> instance, E e, Operation<Boolean> original) {
		return ModUtil.filter(((EnchantmentInstance) e).enchantment/*? if >=26.1 {*/()/*?}*/, ModUtil::allSourcesDisabled) && original.call(instance, e);
	}

	@ModifyExpressionValue(
			method = "enchantItem(Lnet/minecraft/util/RandomSource;Lnet/minecraft/world/item/ItemStack;ILjava/util/stream/Stream;)Lnet/minecraft/world/item/ItemStack;",
			at = @At(
                    //? if <26.1 {
                    /*value = "FIELD",
                    target = "Lnet/minecraft/world/item/enchantment/EnchantmentInstance;level:I",
                    opcode = Opcodes.GETFIELD
                    *///?} else {
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/item/enchantment/EnchantmentInstance;level()I"
                    //?}
            )
	)
	private static int limitEnchantmentLevel(int original, @Local EnchantmentInstance ei) {
		if (ED.CONFIG.maxLevel.limitObtainableEnchantmentLevel.get()) {
			for (Map.Entry<Identifier, Integer> entry : ED.CONFIG.maxLevel.obtainableEnchantmentLevels.entrySet()) {
				if (ei.enchantment/*? if >=26.1 {*/()/*?}*/.is(entry.getKey())) {
					if (original > entry.getValue()) return entry.getValue();
				}
			}
		}
		return original;
	}
}
