package me.pajic.enchantmentdisabler.util;

import me.pajic.enchantmentdisabler.ED;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.ItemEnchantments;

import java.util.Map;
import java.util.function.Predicate;

public class ModUtil {

	private static final ThreadLocal<Boolean> TRADING = ThreadLocal.withInitial(() -> false);

	public static boolean filter(Holder<Enchantment> enchantment, Predicate<EnchantmentSources> predicate) {
		for (Map.Entry<Identifier, EnchantmentSources> entry : ED.CONFIG.disabler.disabledEnchantmentsV2.entrySet()) {
			if (enchantment.is(entry.getKey()) && predicate.test(entry.getValue())) {
				ED.debugLog("Filtered out {}", entry.getKey().toString());
				return false;
			}
		}
		return true;
	}

    public static boolean filterStacks(ItemStack stack) {
		ItemEnchantments storedEnchantments = stack.get(DataComponents.STORED_ENCHANTMENTS);
        for (Map.Entry<Identifier, EnchantmentSources> entry : ED.CONFIG.disabler.disabledEnchantmentsV2.entrySet()) {
            if (storedEnchantments != null) {
                if (storedEnchantments.keySet().stream().anyMatch(holder -> holder.is(entry.getKey())) && allSourcesDisabled(entry.getValue())) return true;
            }
        }
        return false;
    }

    public static ItemEnchantments getItemEnchantments(ItemStack stack) {
        if (stack.has(DataComponents.STORED_ENCHANTMENTS)) return stack.get(DataComponents.STORED_ENCHANTMENTS);
        else return stack.get(DataComponents.ENCHANTMENTS);
    }

	public static boolean allSourcesDisabled(EnchantmentSources sources) {
		return !sources.loot.get() && !sources.table.get() && !sources.trade.get();
	}

	public static boolean anySourceDisabled(EnchantmentSources sources) {
		return !sources.loot.get() || !sources.table.get() || !sources.trade.get();
	}

	public static void setTrading(boolean trading) {
		TRADING.set(trading);
	}

	public static boolean isTrading() {
		return TRADING.get();
	}

	public static boolean lootOrTradeDisabled(EnchantmentSources sources) {
		return isTrading() ? !sources.trade.get() : !sources.loot.get();
	}

	public static int capObtainableLevel(Holder<Enchantment> enchantment, int level) {
		if (ED.CONFIG.maxLevel.limitObtainableEnchantmentLevel.get()) {
			for (Map.Entry<Identifier, Integer> entry : ED.CONFIG.maxLevel.obtainableEnchantmentLevels.entrySet()) {
				if (enchantment.is(entry.getKey()) && level > entry.getValue()) return entry.getValue();
			}
		}
		return level;
	}

	public static int lapisCost(int index) {
		if (!ED.CONFIG.enchantingTable.modifyLapisCost.get()) return 0;
		return (int) ED.CONFIG.enchantingTable.lapisCostFormula.evalSafe(Map.of('i', (double) index), 0);
	}

	public static int xpCost(int index) {
		if (!ED.CONFIG.enchantingTable.modifyXpCost.get()) return 0;
		return (int) ED.CONFIG.enchantingTable.xpCostFormula.evalSafe(Map.of('i', (double) index), 0);
	}
}
