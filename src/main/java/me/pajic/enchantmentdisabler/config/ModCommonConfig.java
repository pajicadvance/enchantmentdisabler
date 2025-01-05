package me.pajic.enchantmentdisabler.config;

import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.config.ModConfigEvent;
import net.neoforged.neoforge.common.ModConfigSpec;

import java.util.ArrayList;
import java.util.List;

@EventBusSubscriber(modid = "enchantmentdisabler", bus = EventBusSubscriber.Bus.MOD)
public class ModCommonConfig {

    private static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();

    private static final ModConfigSpec.BooleanValue DISABLER_ENABLED = BUILDER
            .translation("text.config.enchantmentdisabler.option.disablerEnabled")
            .gameRestart()
            .define("disablerEnabled", false);
    private static final ModConfigSpec.ConfigValue<List<? extends String>> DISABLED_ENCHANTMENTS = BUILDER
            .translation("text.config.enchantmentdisabler.option.disabledEnchantments")
            .gameRestart()
            .defineListAllowEmpty("disabledEnchantments", List.of("minecraft:mending"), () -> "", ModCommonConfig::validateEnchantmentEntry);
    private static final ModConfigSpec.BooleanValue MODIFY_MAX_LEVELS = BUILDER
            .translation("text.config.enchantmentdisabler.option.maxLevel.modifyMaxLevels")
            .gameRestart()
            .define("modifyMaxLevels", false);
    private static final ModConfigSpec.ConfigValue<List<? extends String>> MAX_LEVELS = BUILDER
            .translation("text.config.enchantmentdisabler.option.maxLevel.maxLevels")
            .gameRestart()
            .defineListAllowEmpty("maxLevels", List.of("minecraft:sharpness/5"), () -> "", ModCommonConfig::validateMaxLevelEntry);

    public static final ModConfigSpec COMMON_SPEC = BUILDER.build();

    public static boolean disablerEnabled;
    public static List<String> disabledEnchantments;
    public static boolean modifyMaxLevels;
    public static List<String> maxLevels;

    private static boolean validateMaxLevelEntry(Object o) {
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

    private static boolean validateEnchantmentEntry(Object o) {
        return o instanceof String entry && entry.split(":").length == 2;
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
        if (event.getConfig().getSpec() == COMMON_SPEC) {
            disablerEnabled = DISABLER_ENABLED.get();
            disabledEnchantments = new ArrayList<>(DISABLED_ENCHANTMENTS.get());
            modifyMaxLevels = MODIFY_MAX_LEVELS.get();
            maxLevels = new ArrayList<>(MAX_LEVELS.get());
        }
    }
}
