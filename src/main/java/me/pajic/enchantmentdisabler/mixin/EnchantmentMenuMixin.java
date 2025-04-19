package me.pajic.enchantmentdisabler.mixin;

import com.llamalad7.mixinextras.expression.Definition;
import com.llamalad7.mixinextras.expression.Expression;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import me.pajic.enchantmentdisabler.config.ModCommonConfig;
import me.pajic.enchantmentdisabler.config.ModServerConfig;
import me.pajic.enchantmentdisabler.util.ModUtil;
import net.minecraft.core.HolderSet;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.EnchantmentTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.EnchantmentMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.EnchantmentInstance;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

import java.util.List;
import java.util.Optional;

@Mixin(EnchantmentMenu.class)
public abstract class EnchantmentMenuMixin {

    @Shadow @Final private ContainerLevelAccess access;
    @Shadow @Final private RandomSource random;

    @SuppressWarnings("unchecked")
    @ModifyExpressionValue(
            method = "slotsChanged",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/item/ItemStack;isEnchantable()Z"
            )
    )
    private boolean dontUpdateIfNoEnchantmentsAvailable(boolean original, @Local ItemStack itemStack) {
        if (ModCommonConfig.disablerEnabled && ModServerConfig.enchantingTableEnabled) {
            final boolean[] con = {true};
            access.execute((level, pos) -> {
                Optional<HolderSet.Named<Enchantment>> possibleEnchantments =
                        //? if 1.21.1
                        level.registryAccess().registryOrThrow(Registries.ENCHANTMENT).getTag(EnchantmentTags.IN_ENCHANTING_TABLE);
                        //? if >= 1.21.4
                        /*level.registryAccess().lookupOrThrow(Registries.ENCHANTMENT).get(EnchantmentTags.IN_ENCHANTING_TABLE);*/
                List<EnchantmentInstance> list = EnchantmentHelper.getAvailableEnchantmentResults(
                        EnchantmentHelper.getEnchantmentCost(random, 2, 15, itemStack),
                        itemStack,
                        ((HolderSet.Named)possibleEnchantments.get()).stream()
                );
                if (list.isEmpty()) con[0] = false;
            });
            return original && con[0];
        }
        else {
            return original;
        }
    }

    @ModifyExpressionValue(
            method = "lambda$slotsChanged$0",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/level/block/EnchantingTableBlock;isValidBookShelf(Lnet/minecraft/world/level/Level;Lnet/minecraft/core/BlockPos;Lnet/minecraft/core/BlockPos;)Z"
            )
    )
    private boolean modifyMaxTablePower(boolean original, @Local float j) {
        if (ModServerConfig.modifyMaxTablePower && j >= ModServerConfig.maxTablePower) {
            return false;
        }
        return original;
    }

    @Definition(id = "getCount", method = "Lnet/minecraft/world/item/ItemStack;getCount()I")
    @Expression("?.getCount() < ?")
    @ModifyExpressionValue(
            method = "clickMenuButton",
            at = @At("MIXINEXTRAS:EXPRESSION")
    )
    private boolean modifyLapisCostButtonClickCondition(boolean original, @Local(argsOnly = true) int id, @Local(ordinal = 1) ItemStack itemStack2) {
        if (ModServerConfig.modifyLapisCost) {
            int newValue = ModUtil.evaluateFormulaAndReturnValue(ModServerConfig.lapisCostFormula, id);
            if (newValue > 0) {
                return itemStack2.getCount() < newValue;
            }
        }
        return original;
    }

    @ModifyArg(
            method = "lambda$clickMenuButton$1",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/item/ItemStack;consume(ILnet/minecraft/world/entity/LivingEntity;)V"
            ),
            index = 0
    )
    private int modifyLapisCostConsumeItemStack(int i) {
        if (ModServerConfig.modifyLapisCost) {
            int newValue = ModUtil.evaluateFormulaAndReturnValue(ModServerConfig.lapisCostFormula, i - 1);
            if (newValue > 0) {
                return newValue;
            }
        }
        return i;
    }

    @ModifyArg(
            method = "lambda$clickMenuButton$1",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/entity/player/Player;onEnchantmentPerformed(Lnet/minecraft/world/item/ItemStack;I)V"
            ),
            index = 1
    )
    private int modifyXpCostOnEnchantmentPerformed(int i) {
        if (ModServerConfig.modifyXpCost) {
            int newValue = ModUtil.evaluateFormulaAndReturnValue(ModServerConfig.xpCostFormula, i - 1);
            if (newValue > 0) {
                return newValue;
            }
        }
        return i;
    }
}