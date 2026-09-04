package me.pajic.enchantmentdisabler.mixin.trade;

//? >=26.1 {

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import me.pajic.enchantmentdisabler.ED;
import net.minecraft.core.component.DataComponents;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.ItemEnchantments;
import net.minecraft.world.item.trading.MerchantOffer;
import net.minecraft.world.item.trading.VillagerTrade;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.functions.EnchantRandomlyFunction;
import net.minecraft.world.level.storage.loot.functions.EnchantWithLevelsFunction;
import net.minecraft.world.level.storage.loot.functions.LootItemFunction;
import net.minecraft.world.level.storage.loot.functions.SetEnchantmentsFunction;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;
import java.util.Map;

@Mixin(VillagerTrade.class)
public class VillagerTradeMixin {

	@Shadow @Final private List<LootItemFunction> givenItemModifiers;
	@Shadow @Final private ItemStackTemplate gives;

	@ModifyExpressionValue(
			method = "getOffer",
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/world/level/storage/loot/providers/number/NumberProvider;getInt(Lnet/minecraft/world/level/storage/loot/LootContext;)I",
					ordinal = 0
			)
	)
	private int modifyMaxUses(int original) {
		if (givenItemModifiers.stream().anyMatch(f -> f instanceof EnchantRandomlyFunction || f instanceof EnchantWithLevelsFunction || f instanceof SetEnchantmentsFunction)) {
			if (gives.is(Items.ENCHANTED_BOOK) && ED.CONFIG.trades.modifyEnchantedBookTradeUses.get() && original > ED.CONFIG.trades.maxEnchantedBookTradeUses.get()) {
				return ED.CONFIG.trades.maxEnchantedBookTradeUses.get();
			}
			if (ED.CONFIG.trades.modifyEnchantedItemTradeUses.get() && original > ED.CONFIG.trades.maxEnchantedItemTradeUses.get()) {
				return ED.CONFIG.trades.maxEnchantedItemTradeUses.get();
			}
		}
		return original;
	}

	@Inject(
			method = "getOffer",
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/world/item/ItemStack;remove(Lnet/minecraft/core/component/DataComponentType;)Ljava/lang/Object;"
			)
	)
	private void limitBookEnchantmentLevel(
			LootContext lootContext,
			CallbackInfoReturnable<MerchantOffer> cir,
			@Local(name = "result") ItemStack result
	) {
		if (result.is(Items.ENCHANTED_BOOK)) {
			ItemEnchantments ie = result.get(DataComponents.STORED_ENCHANTMENTS);
			if (ie != null) {
				ItemEnchantments.Mutable enchantments = new ItemEnchantments.Mutable(ie);
				enchantments.keySet().forEach(e -> {
					if (ED.CONFIG.trades.limitBookTradeLevel.get() && enchantments.getLevel(e) > ED.CONFIG.trades.bookTradeLevelLimit.get()) {
						enchantments.set(e, ED.CONFIG.trades.bookTradeLevelLimit.get());
					}
					if (ED.CONFIG.maxLevel.limitObtainableEnchantmentLevel.get()) {
						for (Map.Entry<Identifier, Integer> entry : ED.CONFIG.maxLevel.obtainableEnchantmentLevels.entrySet()) {
							if (e.is(entry.getKey()) && enchantments.getLevel(e) > entry.getValue()) {
								enchantments.set(e, entry.getValue());
								break;
							}
						}
					}
				});
				result.set(DataComponents.STORED_ENCHANTMENTS, enchantments.toImmutable());
			}
		}
	}
}
//?}
