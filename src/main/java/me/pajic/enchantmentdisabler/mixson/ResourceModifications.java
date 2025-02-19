package me.pajic.enchantmentdisabler.mixson;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import me.pajic.enchantmentdisabler.Main;
import net.fabricmc.loader.api.FabricLoader;
import net.ramixin.mixson.debug.DebugMode;
import net.ramixin.mixson.inline.EventContext;
import net.ramixin.mixson.inline.Mixson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class ResourceModifications {

    private static final Logger LOGGER = LoggerFactory.getLogger("EnchantmentDisabler-ResourceModifications");

    private static final List<String> DISABLER_TARGETS = List.of(
            "minecraft:curse",
            "minecraft:in_enchanting_table",
            "minecraft:non_treasure",
            "minecraft:on_mob_spawn_equipment",
            "minecraft:on_random_loot",
            "minecraft:on_traded_equipment",
            "minecraft:tradeable",
            "minecraft:treasure"
    );

    public static void init() {
        if (FabricLoader.getInstance().isDevelopmentEnvironment()) Mixson.setDebugMode(DebugMode.EXPORT);

        if (Main.CONFIG.disablerEnabled()) {
            DISABLER_TARGETS.forEach(tag -> Mixson.registerEvent(
                    Mixson.DEFAULT_PRIORITY,
                    tag.replace(":", ":tags/enchantment/"),
                    "enchantmentdisabler:modify_" + tag.replace(':', '_'),
                    context -> runEventOnTag(context, tag)
            ));
        }

        if (Main.CONFIG.maxLevel.modifyMaxLevels()) {
            Main.CONFIG.maxLevel.maxLevels().forEach(entry -> {
                int i = entry.lastIndexOf('/');
                if (i == -1) {
                    LOGGER.error("Invalid max level entry: {}", entry);
                } else {
                    String entryString = entry.substring(0, i);
                    int maxLevel;
                    try {
                        maxLevel = Integer.parseInt(entry.substring(i + 1));
                        String[] split2 = entryString.split(":", 2);
                        if (split2.length != 2) {
                            LOGGER.error("Enchantment in max level entry must be in format namespace:enchantment: {}", entry);
                        } else {
                            String namespace = split2[0];
                            String enchantment = split2[1];
                            Mixson.registerEvent(
                                    Mixson.DEFAULT_PRIORITY,
                                    namespace + ":enchantment/" + enchantment,
                                    "enchantmentdisabler:modify_" + enchantment + "_max_level",
                                    context -> context.getFile().getAsJsonObject().addProperty("max_level", maxLevel)
                            );
                        }
                    } catch (NumberFormatException e) {
                        LOGGER.error("Max level is not a number in max level entry: {}", entry);
                    }
                }
            });
        }
    }

    private static void runEventOnTag(EventContext<JsonElement> context, String tag) {
        context.registerRuntimeEvent(
                Mixson.DEFAULT_PRIORITY,
                tag.replace(":", ":tags/enchantment/"),
                "enchantmentdisabler:modify_" + tag.replace(':', '_'),
                context1 -> {
                    List<JsonElement> values = context1.getFile().getAsJsonObject().getAsJsonArray("values").asList();
                    values.removeIf(value -> {
                        String entry;
                        if (value.isJsonPrimitive()) entry = value.getAsString();
                        else entry = value.getAsJsonObject().get("id").getAsString();
                        if (entry.startsWith("#")) {
                            runEventOnTag(context1, entry.replace("#", ""));
                            return false;
                        } else return Main.CONFIG.disabledEnchantments().contains(entry);
                    });
                    JsonArray newValues = new JsonArray();
                    values.forEach(newValues::add);
                    context1.getFile().getAsJsonObject().add("values", newValues);
                },
                false
        );
    }
}
