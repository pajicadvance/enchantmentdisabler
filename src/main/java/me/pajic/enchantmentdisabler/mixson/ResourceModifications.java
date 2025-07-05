package me.pajic.enchantmentdisabler.mixson;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import me.pajic.enchantmentdisabler.Main;
import me.pajic.enchantmentdisabler.util.ModUtil;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.fml.loading.FMLLoader;
import net.ramixin.mixson.debug.DebugMode;
import net.ramixin.mixson.inline.EventContext;
import net.ramixin.mixson.inline.Mixson;

import java.util.List;

@SuppressWarnings({"removal", "deprecation"})
public class ResourceModifications {

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
        if (!FMLLoader.isProduction()) Mixson.setDebugMode(DebugMode.EXPORT);

        DISABLER_TARGETS.forEach(tag -> Mixson.registerEvent(
                Mixson.DEFAULT_PRIORITY,
                tag.replace(":", ":tags/enchantment/"),
                "enchantmentdisabler:modify_" + tag.replace(':', '_'),
                context -> runEventOnTag(context, tag)
        ));

        if (Main.CONFIG.maxLevel.modifyMaxLevels.get()) {
            Main.CONFIG.maxLevel.maxLevels.forEach((key, value) -> {
                String namespace = key.getNamespace();
                String path = key.getPath();
                Mixson.registerEvent(
                        Mixson.DEFAULT_PRIORITY,
                        namespace + ":enchantment/" + path,
                        "enchantmentdisabler:modify_" + path + "_max_level",
                        context -> context.getFile().getAsJsonObject().addProperty("max_level", value)
                );
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
                        } else {
                            if (!ModUtil.registeredEnchantments.contains(ResourceLocation.parse(entry))) {
                                ModUtil.registeredEnchantments.add(ResourceLocation.parse(entry));
                            }
                            if (!Main.CONFIG.disabler.disablerEnabled.get()) return false;
                            return Main.CONFIG.disabler.disabledEnchantments.contains(ResourceLocation.parse(entry));
                        }
                    });
                    JsonArray newValues = new JsonArray();
                    values.forEach(newValues::add);
                    context1.getFile().getAsJsonObject().add("values", newValues);
                },
                false
        );
    }
}
