package me.pajic.enchantmentdisabler.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import me.pajic.enchantmentdisabler.Main;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.EnchantmentInstance;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import java.util.List;
import java.util.Map;

@Mixin(EnchantmentHelper.class)
public class EnchantmentHelperMixin {

    @WrapOperation(
            method = "method_60106",
            at = @At(
                    value = "INVOKE",
                    target = "Ljava/util/List;add(Ljava/lang/Object;)Z"
            )
    )
    private static <E> boolean preventAdditionIfDisabled(List<EnchantmentInstance> instance, E e, Operation<Boolean> original) {
        EnchantmentInstance ei = (EnchantmentInstance) e;
        if (
                Main.CONFIG.disabler.disablerEnabled.get() &&
                Main.CONFIG.disabler.disabledEnchantments.stream().anyMatch(rl -> {
                    return ei.enchantment/*? if > 1.21.4 {*/()/*?}*/.is(rl);
                })
        ) {
            return false;
        }
        return original.call(instance, e);
    }

    @ModifyExpressionValue(
            method = "enchantItem(Lnet/minecraft/util/RandomSource;Lnet/minecraft/world/item/ItemStack;ILjava/util/stream/Stream;)Lnet/minecraft/world/item/ItemStack;",
            at = @At(
                    //? if <= 1.21.4 {
                    /*value = "FIELD",
                    target = "Lnet/minecraft/world/item/enchantment/EnchantmentInstance;level:I"
                    *///?} else {
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/item/enchantment/EnchantmentInstance;level()I"
                    //?}
            )
    )
    private static int limitEnchantmentLevel(int original, @Local EnchantmentInstance ei) {
        if (Main.CONFIG.maxLevel.limitObtainableEnchantmentLevel.get()) {
            for (Map.Entry<ResourceLocation, Integer> entry : Main.CONFIG.maxLevel.obtainableEnchantmentLevels.entrySet()) {
                if (ei.enchantment/*? if > 1.21.4 {*/()/*?}*/.is(entry.getKey())) {
                    if (original > entry.getValue()) return entry.getValue();
                }
            }
        }
        return original;
    }
}
