package me.pajic.enchantmentdisabler.config;

import me.fzzyhmstrs.fzzy_config.annotations.Action;
import me.fzzyhmstrs.fzzy_config.annotations.RequiresAction;
import me.fzzyhmstrs.fzzy_config.annotations.Version;
import me.fzzyhmstrs.fzzy_config.config.Config;
import me.fzzyhmstrs.fzzy_config.config.ConfigSection;
import me.fzzyhmstrs.fzzy_config.validation.collection.ValidatedList;
import me.fzzyhmstrs.fzzy_config.validation.collection.ValidatedMap;
import me.fzzyhmstrs.fzzy_config.validation.minecraft.ValidatedIdentifier;
import me.fzzyhmstrs.fzzy_config.validation.misc.ValidatedAny;
import me.fzzyhmstrs.fzzy_config.validation.misc.ValidatedBoolean;
import me.fzzyhmstrs.fzzy_config.validation.misc.ValidatedExpression;
import me.fzzyhmstrs.fzzy_config.validation.number.ValidatedInt;
import me.pajic.enchantmentdisabler.ED;
import me.pajic.enchantmentdisabler.util.EnchantmentSources;
import net.minecraft.resources.Identifier;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@SuppressWarnings({"unchecked", "rawtypes"})
@Version(version = 2)
public class ModConfig extends Config {
	public ModConfig() {
		super(ED.id("config"));
	}

	public Disabler disabler = new Disabler();
	public MaxLevel maxLevel = new MaxLevel();
	public Trades trades = new Trades();
	public EnchantingTable enchantingTable = new EnchantingTable();
	public Loot loot = new Loot();

	public static class Disabler extends ConfigSection {
		@RequiresAction(action = Action.RESTART)
		public ValidatedMap<Identifier, EnchantmentSources> disabledEnchantmentsV2 = (new ValidatedMap.Builder())
				.keyHandler(new ValidatedIdentifier())
				.valueHandler(new ValidatedAny<>(new EnchantmentSources()))
				.build();
		public ValidatedList<Identifier> disabledEnchantments = new ValidatedIdentifier().toList();
	}

	public static class MaxLevel extends ConfigSection {
		@RequiresAction(action = Action.RESTART)
		public ValidatedMap<Identifier, Integer> maxLevels = (new ValidatedMap.Builder())
				.keyHandler(new ValidatedIdentifier())
				.valueHandler(new ValidatedInt(1, Integer.MAX_VALUE, 1))
				.build();
		public ValidatedInt globalMaxLevel = new ValidatedInt(0, Integer.MAX_VALUE, 0);
		public ValidatedBoolean limitObtainableEnchantmentLevel = new ValidatedBoolean(false);
		public ValidatedMap<Identifier, Integer> obtainableEnchantmentLevels = (new ValidatedMap.Builder())
				.keyHandler(new ValidatedIdentifier())
				.valueHandler(new ValidatedInt(1, Integer.MAX_VALUE, 1))
				.build();
	}

	public static class Trades extends ConfigSection {
		public ValidatedBoolean limitBookTradeLevel = new ValidatedBoolean(false);
		public ValidatedInt bookTradeLevelLimit = new ValidatedInt(5, 5, 1);
		public ValidatedBoolean modifyEnchantedBookTradeUses = new ValidatedBoolean(false);
		public ValidatedInt maxEnchantedBookTradeUses = new ValidatedInt(12, 12, 1);
		public ValidatedBoolean modifyEnchantedItemTradeUses = new ValidatedBoolean(false);
		public ValidatedInt maxEnchantedItemTradeUses = new ValidatedInt(3, 3, 1);
		public ValidatedBoolean enchantedBookTradeRestockEnabled = new ValidatedBoolean(true);
		public ValidatedBoolean enchantedItemTradeRestockEnabled = new ValidatedBoolean(true);
	}

	public static class EnchantingTable extends ConfigSection {
		public ValidatedBoolean enchantingTableEnabled = new ValidatedBoolean(true);
		public ValidatedBoolean modifyMaxTablePower = new ValidatedBoolean(false);
		public ValidatedInt maxTablePower = new ValidatedInt(15, 15, 1);
		public ValidatedBoolean modifyLapisCost = new ValidatedBoolean(false);
		public ValidatedExpression lapisCostFormula = new ValidatedExpression("i+1", Set.of('i'));
		public ValidatedBoolean modifyXpCost = new ValidatedBoolean(false);
		public ValidatedExpression xpCostFormula = new ValidatedExpression("i+1", Set.of('i'));
	}

	public static class Loot extends ConfigSection {
		public ValidatedBoolean modifyEnchantWithLevelsMaxPower = new ValidatedBoolean(false);
		public ValidatedInt enchantWithLevelsMaxPower = new ValidatedInt(50, 50, 1);
	}

	@Override
	public void update(int deserializedVersion) {
		if (deserializedVersion == 1) {
			Map<Identifier, EnchantmentSources> map = new HashMap<>();
			disabler.disabledEnchantments.forEach(disabledEnchantment ->
					map.put(disabledEnchantment, new EnchantmentSources())
			);
			disabler.disabledEnchantmentsV2 = (new ValidatedMap.Builder())
					.keyHandler(new ValidatedIdentifier())
					.valueHandler(new ValidatedAny<>(new EnchantmentSources()))
					.defaults(map)
					.build();
		}
	}
}
