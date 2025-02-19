package me.pajic.enchantmentdisabler.config;

import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.config.ModConfigEvent;
import net.neoforged.neoforge.common.ModConfigSpec;
import net.objecthunter.exp4j.Expression;
import net.objecthunter.exp4j.ExpressionBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

@EventBusSubscriber(modid = "enchantmentdisabler", bus = EventBusSubscriber.Bus.MOD)
public class ModServerConfig {

    private static final Logger LOGGER = LoggerFactory.getLogger("EnchantmentDisabler-ServerConfig");

    private static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();

    private static final ModConfigSpec.BooleanValue LIMIT_OBTAINABLE_ENCHANTMENT_LEVEL = BUILDER
            .translation("text.config.enchantmentdisabler.option.maxLevel.limitObtainableEnchantmentLevel")
            .define("limitObtainableEnchantmentLevel", false);
    private static final ModConfigSpec.ConfigValue<List<? extends String>> OBTAINABLE_ENCHANTMENT_LEVELS = BUILDER
            .translation("text.config.enchantmentdisabler.option.maxLevel.obtainableEnchantmentLevels")
            .defineListAllowEmpty("obtainableEnchantmentLevels", List.of("minecraft:sharpness/4"), () -> "", ModServerConfig::validateObtainableLevelEntry);

    private static final ModConfigSpec.BooleanValue LIMIT_BOOK_TRADE_LEVEL = BUILDER
            .translation("text.config.enchantmentdisabler.option.trades.limitBookTradeLevel")
            .define("limitBookTradeLevel", false);
    private static final ModConfigSpec.IntValue BOOK_TRADE_LEVEL_LIMIT = BUILDER
            .translation("text.config.enchantmentdisabler.option.trades.bookTradeLevelLimit")
            .defineInRange("bookTradeLevelLimit", 5, 1, Integer.MAX_VALUE);
    private static final ModConfigSpec.BooleanValue MODIFY_ENCHANTED_BOOK_TRADE_USES = BUILDER
            .translation("text.config.enchantmentdisabler.option.trades.modifyEnchantedBookTradeUses")
            .define("modifyEnchantedBookTradeUses", false);
    private static final ModConfigSpec.IntValue MAX_ENCHANTED_BOOK_TRADE_USES = BUILDER
            .translation("text.config.enchantmentdisabler.option.trades.maxEnchantedBookTradeUses")
            .defineInRange("maxEnchantedBookTradeUses", 12, 1, Integer.MAX_VALUE);
    private static final ModConfigSpec.BooleanValue MODIFY_ENCHANTED_ITEM_TRADE_USES = BUILDER
            .translation("text.config.enchantmentdisabler.option.trades.modifyEnchantedItemTradeUses")
            .define("modifyEnchantedItemTradeUses", false);
    private static final ModConfigSpec.IntValue MAX_ENCHANTED_ITEM_TRADE_USES = BUILDER
            .translation("text.config.enchantmentdisabler.option.trades.maxEnchantedItemTradeUses")
            .defineInRange("maxEnchantedItemTradeUses", 3, 1, Integer.MAX_VALUE);
    private static final ModConfigSpec.BooleanValue ENCHANTED_BOOK_TRADE_RESTOCK_ENABLED = BUILDER
            .translation("text.config.enchantmentdisabler.option.trades.enchantedBookTradeRestockEnabled")
            .define("enchantedBookTradeRestockEnabled", true);
    private static final ModConfigSpec.BooleanValue ENCHANTED_ITEM_TRADE_RESTOCK_ENABLED = BUILDER
            .translation("text.config.enchantmentdisabler.option.trades.enchantedItemTradeRestockEnabled")
            .define("enchantedItemTradeRestockEnabled", true);

    private static final ModConfigSpec.BooleanValue ENCHANTING_TABLE_ENABLED = BUILDER
            .translation("text.config.enchantmentdisabler.option.enchantingTable.enchantingTableEnabled")
            .define("enchantingTableEnabled", true);
    private static final ModConfigSpec.BooleanValue MODIFY_MAX_TABLE_POWER = BUILDER
            .translation("text.config.enchantmentdisabler.option.enchantingTable.modifyMaxTablePower")
            .define("modifyMaxTablePower", false);
    private static final ModConfigSpec.IntValue MAX_TABLE_POWER = BUILDER
            .translation("text.config.enchantmentdisabler.option.enchantingTable.maxTablePower")
            .defineInRange("maxTablePower", 15, 1, Integer.MAX_VALUE);
    private static final ModConfigSpec.BooleanValue MODIFY_LAPIS_COST = BUILDER
            .translation("text.config.enchantmentdisabler.option.enchantingTable.modifyLapisCost")
            .define("modifyLapisCost", false);
    private static final ModConfigSpec.ConfigValue<String> LAPIS_COST_FORMULA = BUILDER
            .translation("text.config.enchantmentdisabler.option.enchantingTable.lapisCostFormula")
            .define("lapisCostFormula", "id+1", ModServerConfig::validateStringFormula);
    private static final ModConfigSpec.BooleanValue MODIFY_XP_COST = BUILDER
            .translation("text.config.enchantmentdisabler.option.enchantingTable.modifyXpCost")
            .define("modifyXpCost", false);
    private static final ModConfigSpec.ConfigValue<String> XP_COST_FORMULA = BUILDER
            .translation("text.config.enchantmentdisabler.option.enchantingTable.xpCostFormula")
            .define("xpCostFormula", "id+1", ModServerConfig::validateStringFormula);

    private static final ModConfigSpec.BooleanValue MODIFY_ENCHANT_WITH_LEVELS_MAX_POWER = BUILDER
            .translation("text.config.enchantmentdisabler.option.loot.modifyEnchantWithLevelsMaxPower")
            .define("modifyEnchantWithLevelsMaxPower", false);
    private static final ModConfigSpec.IntValue ENCHANT_WITH_LEVELS_MAX_POWER = BUILDER
            .translation("text.config.enchantmentdisabler.option.loot.enchantWithLevelsMaxPower")
            .defineInRange("enchantWithLevelsMaxPower", 50, 1, Integer.MAX_VALUE);

    public static final ModConfigSpec SERVER_SPEC = BUILDER.build();

    public static boolean limitObtainableEnchantmentLevel;
    public static List<String> obtainableEnchantmentLevels;
    public static boolean limitBookTradeLevel;
    public static int bookTradeLevelLimit;
    public static boolean modifyEnchantedBookTradeUses;
    public static int maxEnchantedBookTradeUses;
    public static boolean modifyEnchantedItemTradeUses;
    public static int maxEnchantedItemTradeUses;
    public static boolean enchantedBookTradeRestockEnabled;
    public static boolean enchantedItemTradeRestockEnabled;
    public static boolean enchantingTableEnabled;
    public static boolean modifyMaxTablePower;
    public static int maxTablePower;
    public static boolean modifyLapisCost;
    public static String lapisCostFormula;
    public static boolean modifyXpCost;
    public static String xpCostFormula;
    public static boolean modifyEnchantWithLevelsMaxPower;
    public static int enchantWithLevelsMaxPower;

    private static boolean validateStringFormula(Object o) {
        if (o instanceof String entry) {
            try {
                Expression expression = new ExpressionBuilder(entry)
                        .variable("id")
                        .build().setVariable("id", 1);
                return expression.validate().isValid();
            } catch (Exception e) {
                LOGGER.warn("Formula in config is not valid: {}", e.getMessage());
            }
        }
        return false;
    }

    private static boolean validateObtainableLevelEntry(Object o) {
        if (o instanceof String entry) {
            String[] split1 = entry.split("/");
            if (split1.length == 2) {
                try {
                    Integer.parseInt(split1[1]);
                } catch (NumberFormatException e) {
                    return false;
                }
                String[] split2 = split1[0].split(":");
                return split2.length == 2;
            }
        }
        return false;
    }

    @SubscribeEvent
    static void onLoad(final ModConfigEvent.Loading event) {
        updateConfig(event);
    }

    @SubscribeEvent
    static void onReload(final ModConfigEvent.Reloading event) {
        updateConfig(event);
    }

    private static void updateConfig(ModConfigEvent event) {
        if (event.getConfig().getSpec() == SERVER_SPEC) {
            limitObtainableEnchantmentLevel = LIMIT_OBTAINABLE_ENCHANTMENT_LEVEL.get();
            obtainableEnchantmentLevels = new ArrayList<>(OBTAINABLE_ENCHANTMENT_LEVELS.get());

            limitBookTradeLevel = LIMIT_BOOK_TRADE_LEVEL.get();
            bookTradeLevelLimit = BOOK_TRADE_LEVEL_LIMIT.get();
            modifyEnchantedBookTradeUses = MODIFY_ENCHANTED_BOOK_TRADE_USES.get();
            maxEnchantedBookTradeUses = MAX_ENCHANTED_BOOK_TRADE_USES.get();
            modifyEnchantedItemTradeUses = MODIFY_ENCHANTED_ITEM_TRADE_USES.get();
            maxEnchantedItemTradeUses = MAX_ENCHANTED_ITEM_TRADE_USES.get();
            enchantedBookTradeRestockEnabled = ENCHANTED_BOOK_TRADE_RESTOCK_ENABLED.get();
            enchantedItemTradeRestockEnabled = ENCHANTED_ITEM_TRADE_RESTOCK_ENABLED.get();

            enchantingTableEnabled = ENCHANTING_TABLE_ENABLED.get();
            modifyMaxTablePower = MODIFY_MAX_TABLE_POWER.get();
            maxTablePower = MAX_TABLE_POWER.get();
            modifyLapisCost = MODIFY_LAPIS_COST.get();
            lapisCostFormula = LAPIS_COST_FORMULA.get();
            modifyXpCost = MODIFY_XP_COST.get();
            xpCostFormula = XP_COST_FORMULA.get();

            modifyEnchantWithLevelsMaxPower = MODIFY_ENCHANT_WITH_LEVELS_MAX_POWER.get();
            enchantWithLevelsMaxPower = ENCHANT_WITH_LEVELS_MAX_POWER.get();
        }
    }
}
