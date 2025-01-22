package me.pajic.enchantmentdisabler.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import me.pajic.enchantmentdisabler.Main;
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
            method = "method_60106",
            at = @At(
                    value = "INVOKE",
                    target = "Ljava/util/List;add(Ljava/lang/Object;)Z"
            )
    )
    private static <E> boolean preventAdditionIfDisabled(List<EnchantmentInstance> instance, E e, Operation<Boolean> original) {
        EnchantmentInstance ei = (EnchantmentInstance) e;
        if (
                Main.CONFIG.disablerEnabled() &&
                Main.CONFIG.disabledEnchantments().stream().anyMatch(s -> {
                    try {
                        return ei.enchantment.is(ResourceLocation.parse(s));
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
}
