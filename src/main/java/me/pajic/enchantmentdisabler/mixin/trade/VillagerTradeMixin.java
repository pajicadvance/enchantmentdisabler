package me.pajic.enchantmentdisabler.mixin.trade;

//? >=26.1 {

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import me.pajic.enchantmentdisabler.ED;
import me.pajic.enchantmentdisabler.util.ModUtil;
import net.minecraft.core.component.DataComponents;
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
import net.minecraft.world.level.storage.loot.functions.SequenceFunction;
import net.minecraft.world.level.storage.loot.functions.SetEnchantmentsFunction;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

//? >26.2 {
import net.minecraft.core.Holder;
import java.util.Optional;
//?} else {
/*import java.util.List;
*///?}

@Mixin(VillagerTrade.class)
public class VillagerTradeMixin {

    //~ if >26.2 'List<LootItemFunction> givenItemModifiers' -> 'Optional<Holder<LootItemFunction>> givenItemModifier'
	@Shadow @Final private Optional<Holder<LootItemFunction>> givenItemModifier;
	@Shadow @Final private ItemStackTemplate gives;

    @ModifyExpressionValue(
			method = "getOffer",
			at = @At(
					value = "INVOKE",
                    //~ if >26.2 'NumberProvider' -> 'ints/ContextIntProvider'
                    target = "Lnet/minecraft/world/level/storage/loot/providers/number/ints/ContextIntProvider;getInt(Lnet/minecraft/world/level/storage/loot/LootContext;)I",
					ordinal = 0
			)
	)
	private int modifyMaxUses(int original) {
        //~ if >26.2 'givenItemModifiers' -> 'givenItemModifier'
		if (givenItemModifier.stream()/*? >26.2 {*/.map(Holder::value)/*?}*/.anyMatch(VillagerTradeMixin::ed$isEnchantFunction)) {
			if (gives.is(Items.ENCHANTED_BOOK)) {
				if (ED.CONFIG.trades.modifyEnchantedBookTradeUses.get() && original > ED.CONFIG.trades.maxEnchantedBookTradeUses.get()) {
					return ED.CONFIG.trades.maxEnchantedBookTradeUses.get();
				}
			} else if (ED.CONFIG.trades.modifyEnchantedItemTradeUses.get() && original > ED.CONFIG.trades.maxEnchantedItemTradeUses.get()) {
				return ED.CONFIG.trades.maxEnchantedItemTradeUses.get();
			}
		}
		return original;
	}

	@WrapOperation(
			method = "getOffer",
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/world/level/storage/loot/functions/LootItemFunction;apply(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;"
			)
	)
	private Object markTrading(LootItemFunction instance, Object stack, Object context, Operation<Object> original) {
		ModUtil.setTrading(true);
		try {
			return original.call(instance, stack, context);
		} finally {
			ModUtil.setTrading(false);
		}
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
					int level = ModUtil.capObtainableLevel(e, enchantments.getLevel(e));
					if (ED.CONFIG.trades.limitBookTradeLevel.get()) level = Math.min(level, ED.CONFIG.trades.bookTradeLevelLimit.get());
					enchantments.set(e, level);
				});
				result.set(DataComponents.STORED_ENCHANTMENTS, enchantments.toImmutable());
			}
		}
	}

	@Unique
	private static boolean ed$isEnchantFunction(LootItemFunction function) {
		if (function instanceof SequenceFunction sequence) {
			return ((SequenceFunctionAccessor) sequence).getFunctions().stream()/*? >26.2 {*/.map(Holder::value)/*?}*/.anyMatch(VillagerTradeMixin::ed$isEnchantFunction);
		}
		return function instanceof EnchantRandomlyFunction || function instanceof EnchantWithLevelsFunction || function instanceof SetEnchantmentsFunction;
	}
}
//?}
