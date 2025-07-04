package me.pajic.enchantmentdisabler.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import me.pajic.enchantmentdisabler.Main;
import net.minecraft.world.entity.npc.VillagerTrades;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(VillagerTrades.EnchantedItemForEmeralds.class)
public class EnchantedItemForEmeraldsMixin {

    @ModifyExpressionValue(
            method = "getOffer",
            at = @At(value = "FIELD", target = "Lnet/minecraft/world/entity/npc/VillagerTrades$EnchantedItemForEmeralds;maxUses:I")
    )
    private int setTradeUses(int original) {
        if (Main.CONFIG.trades.modifyEnchantedItemTradeUses.get() && original > Main.CONFIG.trades.maxEnchantedItemTradeUses.get()) {
            return Main.CONFIG.trades.maxEnchantedItemTradeUses.get();
        }
        else {
            return original;
        }
    }
}