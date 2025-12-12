package me.pajic.enchantmentdisabler.mixin.trade;

import me.pajic.enchantmentdisabler.ED;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.trading.MerchantOffer;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MerchantOffer.class)
public class MerchantOfferMixin {

    @Final @Shadow private ItemStack result;

    @Inject(
			method = "resetUses",
			at = @At("HEAD"),
			cancellable = true
	)
    private void skipEnchantedItemTradeRestock(CallbackInfo ci) {
        if (!ED.CONFIG.trades.enchantedBookTradeRestockEnabled.get() && result.is(Items.ENCHANTED_BOOK)) ci.cancel();
        if (
				!ED.CONFIG.trades.enchantedItemTradeRestockEnabled.get() &&
				!result.is(Items.ENCHANTED_BOOK) && !result.getEnchantments().isEmpty()
		) ci.cancel();
    }
}
