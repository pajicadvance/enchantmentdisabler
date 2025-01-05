package me.pajic.enchantmentdisabler.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import me.pajic.enchantmentdisabler.config.ModServerConfig;
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
        if (ModServerConfig.modifyEnchantedItemTradeUses) {
            return ModServerConfig.maxEnchantedItemTradeUses;
        }
        else {
            return original;
        }
    }
}
