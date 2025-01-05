package me.pajic.enchantmentdisabler.mixson;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import me.pajic.enchantmentdisabler.config.ModCommonConfig;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.fml.loading.FMLLoader;
import net.ramixin.mixson.DebugMode;
import net.ramixin.mixson.Mixson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class ResourceModifications {

    private static final Logger LOGGER = LoggerFactory.getLogger("EnchantmentDisabler-ResourceModifications");

    private static final List<String> DISABLER_TARGETS = List.of(
            "in_enchanting_table",
            "non_treasure",
            "on_mob_spawn_equipment",
            "on_random_loot",
            "on_traded_equipment",
            "tradeable",
            "treasure"
    );

    public static void init() {

        if (!FMLLoader.isProduction()) Mixson.setDebugMode(DebugMode.EXPORT);

        if (ModCommonConfig.disablerEnabled) {
            DISABLER_TARGETS.forEach(name -> Mixson.registerModificationEvent(
                    ResourceLocation.withDefaultNamespace("tags/enchantment/" + name),
                    ResourceLocation.fromNamespaceAndPath("enchantmentdisabler", "modify_" + name),
                    jsonElement -> {
                        List<JsonElement> values = jsonElement.getAsJsonObject().getAsJsonArray("values").asList();
                        values.removeIf(value -> ModCommonConfig.disabledEnchantments.contains(value.getAsString()));
                        JsonArray newValues = new JsonArray();
                        values.forEach(newValues::add);
                        jsonElement.getAsJsonObject().add("values", newValues);
                        return jsonElement;
                    }
            ));
        }

        if (ModCommonConfig.modifyMaxLevels) {
            ModCommonConfig.maxLevels.forEach(entry -> {
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
