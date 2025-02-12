package me.pajic.enchantmentdisabler.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import me.pajic.enchantmentdisabler.config.ModServerConfig;
import me.pajic.enchantmentdisabler.util.ModUtil;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.npc.VillagerTrades;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.trading.ItemCost;
import net.minecraft.world.item.trading.MerchantOffer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import java.util.Optional;

@Mixin(VillagerTrades.EnchantBookForEmeralds.class)
public class EnchantBookForEmeraldsMixin {

    @WrapOperation(
            method = "getOffer",
            at = @At(
                    value = "NEW",
                    target = "(Lnet/minecraft/world/item/trading/ItemCost;Ljava/util/Optional;Lnet/minecraft/world/item/ItemStack;IIF)Lnet/minecraft/world/item/trading/MerchantOffer;"
            )
    )
    private MerchantOffer setTradeUses(ItemCost baseCostA, Optional costB, ItemStack result, int maxUses, int xp, float priceMultiplier, Operation<MerchantOffer> original) {
        if (ModServerConfig.modifyEnchantedBookTradeUses) {
            return original.call(baseCostA, costB, result, ModServerConfig.maxEnchantedBookTradeUses, xp, priceMultiplier);
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
        if (ModServerConfig.limitBookTradeLevel && original > ModServerConfig.bookTradeLevelLimit) {
            value = ModServerConfig.bookTradeLevelLimit;
        }
        if (ModServerConfig.limitObtainableEnchantmentLevel) {
            for (Object2IntMap.Entry<String> entry : ModUtil.parseObtainableEnchantmentLevelLimits().object2IntEntrySet()) {
                if (enchantment.is(ResourceLocation.parse(entry.getKey())) && value >= entry.getIntValue()) {
                    value = entry.getIntValue();
                    break;
                }
            }
        }
        return value;
    }
}