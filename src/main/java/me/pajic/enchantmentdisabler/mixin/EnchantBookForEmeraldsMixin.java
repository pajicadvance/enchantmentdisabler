package me.pajic.enchantmentdisabler.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import me.pajic.enchantmentdisabler.Main;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.npc.VillagerTrades;
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
        if (Main.CONFIG.trades.modifyEnchantedBookTradeUses.get() && maxUses > Main.CONFIG.trades.maxEnchantedBookTradeUses.get()) {
            return original.call(baseCostA, costB, result, Main.CONFIG.trades.maxEnchantedBookTradeUses.get(), xp, priceMultiplier);
        }
        else {
            return original.call(baseCostA, costB, result, maxUses, xp, priceMultiplier);
        }
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
        if (Main.CONFIG.trades.limitBookTradeLevel.get() && original > Main.CONFIG.trades.bookTradeLevelLimit.get()) {
            value = Main.CONFIG.trades.bookTradeLevelLimit.get();
        }
        if (Main.CONFIG.maxLevel.limitObtainableEnchantmentLevel.get()) {
            for (Map.Entry<ResourceLocation, Integer> entry : Main.CONFIG.maxLevel.obtainableEnchantmentLevels.entrySet()) {
                if (enchantment.is(entry.getKey()) && value >= entry.getValue()) {
                    value = entry.getValue();
                    break;
                }
            }
        }
        return value;
    }
}