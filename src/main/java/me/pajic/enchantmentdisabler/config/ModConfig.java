package me.pajic.enchantmentdisabler.config;

import me.fzzyhmstrs.fzzy_config.annotations.Action;
import me.fzzyhmstrs.fzzy_config.annotations.RequiresAction;
import me.fzzyhmstrs.fzzy_config.annotations.Version;
import me.fzzyhmstrs.fzzy_config.config.Config;
import me.fzzyhmstrs.fzzy_config.config.ConfigSection;
import me.fzzyhmstrs.fzzy_config.validation.collection.ValidatedList;
import me.fzzyhmstrs.fzzy_config.validation.collection.ValidatedMap;
import me.fzzyhmstrs.fzzy_config.validation.minecraft.ValidatedIdentifier;
import me.fzzyhmstrs.fzzy_config.validation.misc.ValidatedBoolean;
import me.fzzyhmstrs.fzzy_config.validation.misc.ValidatedExpression;
import me.fzzyhmstrs.fzzy_config.validation.number.ValidatedInt;
import me.pajic.enchantmentdisabler.Main;
import me.pajic.enchantmentdisabler.util.ModUtil;
import net.minecraft.resources.ResourceLocation;

import java.util.Set;

@Version(version = 1)
public class ModConfig extends Config {
    public ModConfig() {
        super(Main.CONFIG_RL);
    }

    public Disabler disabler = new Disabler();
    public MaxLevel maxLevel = new MaxLevel();
    public Trades trades = new Trades();
    public EnchantingTable enchantingTable = new EnchantingTable();
    public Loot loot = new Loot();

    public static class Disabler extends ConfigSection {
        @RequiresAction(action = Action.RESTART)
        public ValidatedBoolean disablerEnabled = new ValidatedBoolean(false);
        @RequiresAction(action = Action.RESTART)
        public ValidatedList<ResourceLocation> disabledEnchantments = new ValidatedIdentifier(ResourceLocation.withDefaultNamespace("mending")).toList();
    }

    @SuppressWarnings("unchecked")
    public static class MaxLevel extends ConfigSection {
        @RequiresAction(action = Action.RESTART)
        public ValidatedBoolean modifyMaxLevels = new ValidatedBoolean(false);
        @RequiresAction(action = Action.RESTART)
        public ValidatedMap<ResourceLocation, Integer> maxLevels = (new ValidatedMap.Builder())
                .keyHandler(new ValidatedIdentifier(ResourceLocation.withDefaultNamespace("mending")))
                .valueHandler(new ValidatedInt(1, Integer.MAX_VALUE, 1))
                .build();
        public ValidatedBoolean limitObtainableEnchantmentLevel = new ValidatedBoolean(false);
        public ValidatedMap<ResourceLocation, Integer> obtainableEnchantmentLevels = (new ValidatedMap.Builder())
                .keyHandler(new ValidatedIdentifier(ResourceLocation.withDefaultNamespace("mending")))
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
}
