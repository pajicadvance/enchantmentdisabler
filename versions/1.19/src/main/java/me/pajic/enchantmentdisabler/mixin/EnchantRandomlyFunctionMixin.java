package me.pajic.enchantmentdisabler.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import me.pajic.enchantmentdisabler.Main;
import me.pajic.enchantmentdisabler.util.ModUtil;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.level.storage.loot.functions.EnchantRandomlyFunction;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

import java.util.List;

@Mixin(EnchantRandomlyFunction.class)
public class EnchantRandomlyFunctionMixin {

    // Removes blacklisted enchantments from the list of possible enchantments
    @ModifyVariable(
            method = "run",
            at = @At("STORE")
    )
    public List<Enchantment> modifyEnchantmentList(List<Enchantment> list) {
        if (Main.CONFIG.disablerEnabled()) {
            return ModUtil.filterList(list);
        }
        else {
            return list;
        }
    }

    // Modifies the maximum enchantment level
    @WrapOperation(
            method = "enchantItem",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/util/Mth;nextInt(Lnet/minecraft/util/RandomSource;II)I")
    )
    private static int setMaxEnchantmentLevel(RandomSource random, int minimum, int maximum, Operation<Integer> original,
                                              @Local(argsOnly = true) Enchantment enchantment) {
        if (Main.CONFIG.maxLevelNest.modifyMaxLevels()) {
            int maxLevel = ModUtil.getMaxLevel(enchantment);
            if (maxLevel > 0) {
                return original.call(random, minimum, maxLevel);
            }
        }
        return original.call(random, minimum, maximum);
    }
}