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
}
