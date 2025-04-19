package me.pajic.enchantmentdisabler.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import me.pajic.enchantmentdisabler.config.ModCommonConfig;
import me.pajic.enchantmentdisabler.config.ModServerConfig;
import me.pajic.enchantmentdisabler.util.ModUtil;
import net.minecraft.ResourceLocationException;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.EnchantmentInstance;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import java.util.List;

@Mixin(EnchantmentHelper.class)
public class EnchantmentHelperMixin {

    @WrapOperation(
            //? if >= 1.21.5
            /*method = "lambda$getAvailableEnchantmentResults$42",*/
            //? if < 1.21.5
            method = "lambda$getAvailableEnchantmentResults$41",
            at = @At(
                    value = "INVOKE",
                    target = "Ljava/util/List;add(Ljava/lang/Object;)Z"
            )
    )
    private static <E> boolean preventAdditionIfDisabled(List<EnchantmentInstance> instance, E e, Operation<Boolean> original) {
        EnchantmentInstance ei = (EnchantmentInstance) e;
        if (
                ModCommonConfig.disablerEnabled &&
                ModCommonConfig.disabledEnchantments.stream().anyMatch(s -> {
                    try {
                        return ei.enchantment/*? if 1.21.5 {*//*()*//*?}*/.is(ResourceLocation.parse(s));
                    } catch (ResourceLocationException ex) {
                        ModUtil.handleResourceLocationException(s ,ex);
                        return false;
                    }
                })
        ) {
            return false;
        }
        return original.call(instance, e);
    }

    @ModifyExpressionValue(
            method = "enchantItem(Lnet/minecraft/util/RandomSource;Lnet/minecraft/world/item/ItemStack;ILjava/util/stream/Stream;)Lnet/minecraft/world/item/ItemStack;",
            at = @At(
                    //? if < 1.21.5 {
                    value = "FIELD",
                    target = "Lnet/minecraft/world/item/enchantment/EnchantmentInstance;level:I"
                    //?}
                    //? if >= 1.21.5 {
                    /*value = "INVOKE",
                    target = "Lnet/minecraft/world/item/enchantment/EnchantmentInstance;level()I"
                    *///?}
            )
    )
    private static int limitEnchantmentLevel(int original, @Local EnchantmentInstance ei) {
        if (ModServerConfig.limitObtainableEnchantmentLevel) {
            for (Object2IntMap.Entry<String> entry : ModUtil.parseObtainableEnchantmentLevelLimits().object2IntEntrySet()) {
                if (ei.enchantment/*? if 1.21.5 {*//*()*//*?}*/.is(ResourceLocation.parse(entry.getKey()))) {
                    if (original > entry.getIntValue()) return entry.getIntValue();
                }
            }
        }
        return original;
    }
}
