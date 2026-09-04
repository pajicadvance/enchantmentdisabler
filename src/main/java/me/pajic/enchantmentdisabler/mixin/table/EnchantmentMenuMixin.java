package me.pajic.enchantmentdisabler.mixin.table;

import com.llamalad7.mixinextras.expression.Definition;
import com.llamalad7.mixinextras.expression.Expression;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import me.pajic.enchantmentdisabler.ED;
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
import java.util.Map;
import java.util.Optional;

@Mixin(EnchantmentMenu.class)
public abstract class EnchantmentMenuMixin {

    @Shadow @Final private ContainerLevelAccess access;
    @Shadow @Final private RandomSource random;

    @SuppressWarnings({"unchecked", "rawtypes", "OptionalGetWithoutIsPresent"})
    @ModifyExpressionValue(
            method = "slotsChanged",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/item/ItemStack;isEnchantable()Z"
            )
    )
    private boolean dontUpdateIfNoEnchantmentsAvailable(boolean original, @Local ItemStack itemStack) {
        if (ED.CONFIG.enchantingTable.enchantingTableEnabled.get()) {
            final boolean[] con = {true};
            access.execute((level, bp) -> {
                Optional<HolderSet.Named<Enchantment>> possibleEnchantments =
                        level.registryAccess().lookupOrThrow(Registries.ENCHANTMENT).get(EnchantmentTags.IN_ENCHANTING_TABLE);
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
			method = {"lambda$slotsChanged$0", "method_17411"},
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/level/block/EnchantingTableBlock;isValidBookShelf(Lnet/minecraft/world/level/Level;Lnet/minecraft/core/BlockPos;Lnet/minecraft/core/BlockPos;)Z"
            )
    )
    private boolean modifyMaxTablePower(boolean original, @Local /*? fabric {*/int/*?} else {*//*float*//*?}*/ bookcases) {
        if (ED.CONFIG.enchantingTable.modifyMaxTablePower.get() && bookcases >= ED.CONFIG.enchantingTable.maxTablePower.get()) {
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
    private boolean modifyLapisCostButtonClickCondition(boolean original, @Local(argsOnly = true) int id, @Local(ordinal = 1) ItemStack currency) {
        if (ED.CONFIG.enchantingTable.modifyLapisCost.get()) {
            int val = (int) ED.CONFIG.enchantingTable.lapisCostFormula.evalSafe(Map.of('i', (double) id), id);
            return val > 0 ? currency.getCount() < val : original;
        }
        return original;
    }

    @ModifyArg(
			method = {"lambda$clickMenuButton$0", "lambda$clickMenuButton$1", "method_17410"},
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/item/ItemStack;consume(ILnet/minecraft/world/entity/LivingEntity;)V"
            ),
            index = 0
    )
    private int modifyLapisCostConsumeItemStack(int i) {
        if (ED.CONFIG.enchantingTable.modifyLapisCost.get()) {
            int val = (int) ED.CONFIG.enchantingTable.lapisCostFormula.evalSafe(Map.of('i', (double) i - 1), i);
            return val > 0 ? val : i;
        }
        return i;
    }

    @ModifyArg(
			method = {"lambda$clickMenuButton$0", "lambda$clickMenuButton$1", "method_17410"},
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/entity/player/Player;onEnchantmentPerformed(Lnet/minecraft/world/item/ItemStack;I)V"
            ),
            index = 1
    )
    private int modifyXpCostOnEnchantmentPerformed(int i) {
        if (ED.CONFIG.enchantingTable.modifyXpCost.get()) {
            int val = (int) ED.CONFIG.enchantingTable.xpCostFormula.evalSafe(Map.of('i', (double) i - 1), i);
            return val > 0 ? val : i;
        }
        return i;
    }
}
