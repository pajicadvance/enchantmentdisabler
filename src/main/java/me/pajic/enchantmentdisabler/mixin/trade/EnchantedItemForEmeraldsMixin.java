package me.pajic.enchantmentdisabler.mixin.trade;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import me.pajic.enchantmentdisabler.ED;
import net.minecraft.world.entity.npc.villager.VillagerTrades;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(VillagerTrades.EnchantedItemForEmeralds.class)
public class EnchantedItemForEmeraldsMixin {

    @ModifyExpressionValue(
            method = "getOffer",
            at = @At(
					value = "FIELD",
					//? if < 1.21.11
					//target = "Lnet/minecraft/world/entity/npc/VillagerTrades$EnchantedItemForEmeralds;maxUses:I",
					//? if >= 1.21.11
					target = "Lnet/minecraft/world/entity/npc/villager/VillagerTrades$EnchantedItemForEmeralds;maxUses:I",
					opcode = Opcodes.GETFIELD
			)
    )
    private int setTradeUses(int original) {
        if (ED.CONFIG.trades.modifyEnchantedItemTradeUses.get() && original > ED.CONFIG.trades.maxEnchantedItemTradeUses.get()) {
            return ED.CONFIG.trades.maxEnchantedItemTradeUses.get();
        }
		return original;
    }
}
