package me.pajic.enchantmentdisabler.mixson;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import me.pajic.enchantmentdisabler.Main;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.resources.ResourceLocation;
import net.ramixin.mixson.DebugMode;
import net.ramixin.mixson.Mixson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public class ResourceModifications {

    private static final Logger LOGGER = LoggerFactory.getLogger("EnchantmentDisabler-ResourceModifications");

    private static final List<String> DISABLER_TARGETS = new ArrayList<>(List.of(
            "minecraft:curse",
            "minecraft:in_enchanting_table",
            "minecraft:non_treasure",
            "minecraft:on_mob_spawn_equipment",
            "minecraft:on_random_loot",
            "minecraft:on_traded_equipment",
            "minecraft:tradeable",
            "minecraft:treasure"
    ));

    public static void init() {

        if (FabricLoader.getInstance().isDevelopmentEnvironment()) Mixson.setDebugMode(DebugMode.EXPORT);

        if (FabricLoader.getInstance().isModLoaded("mr_enchantments_encore")) {
            DISABLER_TARGETS.addAll(List.of(
                    "enchantencore:alloy",
                    "enchantencore:arrow_trail",
                    "enchantencore:aspect",
                    "enchantencore:curse",
                    "enchantencore:protection",
                    "enchantencore:splash_arrow",
                    "enchantencore:trail"
            ));
        }

        if (Main.CONFIG.disablerEnabled()) {
            DISABLER_TARGETS.forEach(name -> Mixson.registerModificationEvent(
                    ResourceLocation.parse(name.replace(":", ":tags/enchantment/")),
                    ResourceLocation.fromNamespaceAndPath("enchantmentdisabler", "modify_" + name.replace(':', '_')),
                    jsonElement -> {
                        List<JsonElement> values = jsonElement.getAsJsonObject().getAsJsonArray("values").asList();
                        values.removeIf(value -> {
                            if (value.isJsonPrimitive()) return Main.CONFIG.disabledEnchantments().contains(value.getAsString());
                            else return Main.CONFIG.disabledEnchantments().contains(value.getAsJsonObject().get("id").getAsString());
                        });
                        JsonArray newValues = new JsonArray();
                        values.forEach(newValues::add);
                        jsonElement.getAsJsonObject().add("values", newValues);
                        return jsonElement;
                    }
            ));
        }

        if (Main.CONFIG.maxLevel.modifyMaxLevels()) {
            Main.CONFIG.maxLevel.maxLevels().forEach(entry -> {
                String[] split1 = entry.split("/", 2);
                if (split1.length != 2) {
                    LOGGER.error("Invalid max level entry: {}", entry);
                } else {
                    String entryString = split1[0];
                    int maxLevel;
                    try {
                        maxLevel = Integer.parseInt(split1[1]);
                        String[] split2 = entryString.split(":", 2);
                        if (split2.length != 2) {
                            LOGGER.error("Enchantment in max level entry must be in format namespace:enchantment: {}", entry);
                        } else {
                            String namespace = split2[0];
                            String enchantment = split2[1];
                            Mixson.registerModificationEvent(
                                    ResourceLocation.fromNamespaceAndPath(namespace, "enchantment/" + enchantment),
                                    ResourceLocation.fromNamespaceAndPath("enchantmentdisabler", "modify_" + enchantment + "_max_level"),
                                    jsonElement -> {
                                        jsonElement.getAsJsonObject().addProperty("max_level", maxLevel);
                                        return jsonElement;
                                    }
                            );
                        }
                    } catch (NumberFormatException e) {
                        LOGGER.error("Max level is not a number in max level entry: {}", entry);
                    }
                }
            });
        }
    }
}
