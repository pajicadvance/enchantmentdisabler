package me.pajic.enchantmentdisabler.config;

import io.wispforest.owo.config.Option;
import io.wispforest.owo.config.annotation.Modmenu;
import io.wispforest.owo.config.annotation.Config;
import io.wispforest.owo.config.annotation.Sync;
import io.wispforest.owo.config.annotation.SectionHeader;
import io.wispforest.owo.config.annotation.RestartRequired;
import io.wispforest.owo.config.annotation.Expanded;
import io.wispforest.owo.config.annotation.Nest;
import io.wispforest.owo.config.annotation.PredicateConstraint;

import java.util.List;

@Modmenu(modId = "enchantmentdisabler")
@Config(name = "enchantmentdisabler-config", wrapperName = "Config")
@Sync(Option.SyncMode.OVERRIDE_CLIENT)
@SuppressWarnings("unused")
public class ConfigModel {

    @SectionHeader("disabler")
    @RestartRequired public boolean disablerEnabled = false;
    @RestartRequired @Expanded public List<String> disabledEnchantments = List.of("minecraft:mending");

    @SectionHeader("tweaks")
    @Nest public MaxLevel maxLevel = new MaxLevel();
    @Nest public Trades trades = new Trades();
    @Nest public EnchantingTable enchantingTable = new EnchantingTable();
    @Nest public Protection protection = new Protection();

    public static class MaxLevel {
        @RestartRequired public boolean modifyMaxLevels = false;
        @RestartRequired @Expanded public List<String> maxLevels = List.of("minecraft:sharpness/5");
    }

    public static class Trades {
        public boolean modifyEnchantedBookTradeUses = false;
        @PredicateConstraint("greaterThanZero") public int maxEnchantedBookTradeUses = 12;
        public boolean modifyEnchantedItemTradeUses = false;
        @PredicateConstraint("greaterThanZero") public int maxEnchantedItemTradeUses = 3;
        public boolean enchantedBookTradeRestockEnabled = true;
        public boolean enchantedItemTradeRestockEnabled = true;

        public static boolean greaterThanZero(int value) {
            return Predicates.greaterThanZero(value);
        }
    }

    public static class EnchantingTable {
        public boolean enchantingTableEnabled = true;
        public boolean modifyLapisCost = false;
        public String lapisCostFormula = "id+1";
    }

    public static class Protection {
        @RestartRequired public boolean meleeProtection = false;
        @RestartRequired public boolean featherFallingExclusive = false;
        public boolean allowMultipleProtectionEnchantments = false;
        @PredicateConstraint("greaterThanZero") public int maxProtectionEnchantments = 2;

        public static boolean greaterThanZero(int value) {
            return Predicates.greaterThanZero(value);
        }
    }

    public static class Predicates {
        public static boolean greaterThanZero(int value) {
            return value > 0;
        }
    }
}