package me.pajic.enchantmentdisabler.mixin.trade;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import me.pajic.enchantmentdisabler.ED;
import net.minecraft.core.Holder;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.npc.villager.VillagerTrades;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.trading.ItemCost;
import net.minecraft.world.item.trading.MerchantOffer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import java.util.Map;
import java.util.Optional;

@Mixin(VillagerTrades.EnchantBookForEmeralds.class)
public class EnchantBookForEmeraldsMixin {

    @SuppressWarnings({"rawtypes", "OptionalUsedAsFieldOrParameterType"})
    @WrapOperation(
            method = "getOffer",
            at = @At(
                    value = "NEW",
                    target = "(Lnet/minecraft/world/item/trading/ItemCost;Ljava/util/Optional;Lnet/minecraft/world/item/ItemStack;IIF)Lnet/minecraft/world/item/trading/MerchantOffer;"
            )
    )
    private MerchantOffer setTradeUses(ItemCost baseCostA, Optional costB, ItemStack result, int maxUses, int xp, float priceMultiplier, Operation<MerchantOffer> original) {
        if (ED.CONFIG.trades.modifyEnchantedBookTradeUses.get() && maxUses > ED.CONFIG.trades.maxEnchantedBookTradeUses.get()) {
            return original.call(baseCostA, costB, result, ED.CONFIG.trades.maxEnchantedBookTradeUses.get(), xp, priceMultiplier);
        }
        return original.call(baseCostA, costB, result, maxUses, xp, priceMultiplier);
    }

    @ModifyExpressionValue(
            method = "getOffer",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/util/Mth;nextInt(Lnet/minecraft/util/RandomSource;II)I"
            )
    )
    private int limitMaxEnchantmentLevel(int original, @Local Holder<Enchantment> enchantment) {
        int value = original;
        if (ED.CONFIG.trades.limitBookTradeLevel.get() && original > ED.CONFIG.trades.bookTradeLevelLimit.get()) {
            value = ED.CONFIG.trades.bookTradeLevelLimit.get();
        }
        if (ED.CONFIG.maxLevel.limitObtainableEnchantmentLevel.get()) {
            for (Map.Entry<Identifier, Integer> entry : ED.CONFIG.maxLevel.obtainableEnchantmentLevels.entrySet()) {
                if (enchantment.is(entry.getKey()) && value >= entry.getValue()) {
                    value = entry.getValue();
                    break;
                }
            }
        }
        return value;
    }
}
