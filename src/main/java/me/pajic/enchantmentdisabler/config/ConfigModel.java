package me.pajic.enchantmentdisabler.config;

import io.wispforest.owo.config.annotation.Config;
import io.wispforest.owo.config.annotation.Expanded;
import io.wispforest.owo.config.annotation.Modmenu;
import io.wispforest.owo.config.annotation.Nest;
import io.wispforest.owo.config.annotation.SectionHeader;

import java.util.List;

@Modmenu(modId = "enchantmentdisabler")
@Config(name = "enchantmentdisabler-config", wrapperName = "Config")
public class ConfigModel {

    @SectionHeader("disabler")
    public boolean disablerEnabled = true;
    @Expanded public List<String> disabledEnchantments = List.of("minecraft:mending");

    @SectionHeader("tweaks")
    @Nest public MaxLevelNest maxLevelNest = new MaxLevelNest();
    @Nest public TradesNest tradesNest = new TradesNest();
    @Nest public EnchantingTableNest enchantingTableNest = new EnchantingTableNest();

    public static class MaxLevelNest {
        public boolean modifyMaxLevels = false;
        @Expanded public List<String> maxLevels = List.of("minecraft:sharpness/5");
    }

    public static class TradesNest {
        public boolean modifyEnchantedBookTradeUses = false;
        public int maxEnchantedBookTradeUses = 12;
        public boolean modifyEnchantedItemTradeUses = false;
        public int maxEnchantedItemTradeUses = 3;
    }

    public static class EnchantingTableNest {
        public boolean enchantingTableEnabled = true;
    }
}