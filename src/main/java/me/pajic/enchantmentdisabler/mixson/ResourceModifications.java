package me.pajic.enchantmentdisabler.mixson;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import me.pajic.enchantmentdisabler.ED;
import me.pajic.enchantmentdisabler.util.ModUtil;
import net.ramixin.mixson.debug.DebugMode;
import net.ramixin.mixson.inline.EventContext;
import net.ramixin.mixson.inline.Mixson;

import java.util.ArrayList;
import java.util.List;

public class ResourceModifications {
	private static final List<String> TRADE_TAGS = List.of("minecraft:on_traded_equipment", "minecraft:tradeable");
	private static final List<String> LOOT_TAGS = List.of("minecraft:on_random_loot", "minecraft:on_mob_spawn_equipment");
	private static final List<String> REMOVE_NON_TREASURE = new ArrayList<>();
	private static final List<String> ADD_LOOT = new ArrayList<>();
	private static final List<String> ADD_TABLE = new ArrayList<>();
	private static final List<String> ADD_TRADE = new ArrayList<>();
	private static final List<String> REMOVE_LOOT = new ArrayList<>();
	private static final List<String> REMOVE_TABLE = new ArrayList<>();
	private static final List<String> REMOVE_TRADE = new ArrayList<>();
	private static boolean initialized = false;

    public static void init() {
		if (initialized) return;
        if (ED.xplat().isDebug()) Mixson.setDebugMode(DebugMode.EXPORT);

		ED.CONFIG.disabler.disabledEnchantmentsV2.forEach((id, sources) -> {
			String s = id.toString();
			if (ModUtil.anySourceDisabled(sources)) {
				REMOVE_NON_TREASURE.add(s);
				if (!sources.loot.get()) REMOVE_LOOT.add(s);
				else ADD_LOOT.add(s);
				if (!sources.table.get()) REMOVE_TABLE.add(s);
				else ADD_TABLE.add(s);
				if (!sources.trade.get()) REMOVE_TRADE.add(s);
				else ADD_TRADE.add(s);
			}
		});

		Mixson.registerEvent(
				Mixson.DEFAULT_PRIORITY,
				rl -> rl.toString().equals("minecraft:tags/enchantment/non_treasure"),
				"Modify non-treasure tag",
				context -> runEventOnTag(context, "minecraft:non_treasure", List.of(), REMOVE_NON_TREASURE),
				false
		);
		Mixson.registerEvent(
				Mixson.DEFAULT_PRIORITY,
				rl -> rl.toString().equals("minecraft:tags/enchantment/in_enchanting_table"),
				"Modify enchanting table tags",
				context -> runEventOnTag(context, "minecraft:in_enchanting_table", ADD_TABLE, REMOVE_TABLE),
				false
		);
		LOOT_TAGS.forEach(s -> Mixson.registerEvent(
				Mixson.DEFAULT_PRIORITY,
				rl -> rl.toString().equals(s.replace(":", ":tags/enchantment/")),
				"Modify random loot tags",
				context -> runEventOnTag(context, s, ADD_LOOT, REMOVE_LOOT),
				false
		));
		TRADE_TAGS.forEach(s -> Mixson.registerEvent(
				Mixson.DEFAULT_PRIORITY,
				rl -> rl.toString().equals(s.replace(":", ":tags/enchantment/")),
				"Modify trade tags",
				context -> runEventOnTag(context, s, ADD_TRADE, REMOVE_TRADE),
				false
		));

		int globalMaxLevel = ED.CONFIG.maxLevel.globalMaxLevel.get();
		if (globalMaxLevel > 0) {
			Mixson.registerEvent(
					Mixson.DEFAULT_PRIORITY,
					rl -> rl.getPath().startsWith("enchantment/"),
					"Set global max level " + globalMaxLevel,
					context -> context.getFile().getAsJsonObject().addProperty("max_level", globalMaxLevel),
					false
			);
		}

        ED.CONFIG.maxLevel.maxLevels.forEach((key, value) -> {
            String namespace = key.getNamespace();
            String path = key.getPath();
            Mixson.registerEvent(
                    Mixson.DEFAULT_PRIORITY,
                    rl -> rl.toString().equals(namespace + ":enchantment/" + path),
                    "Modify max level for enchantment " + key,
                    context -> context.getFile().getAsJsonObject().addProperty("max_level", value),
					false
            );
        });
		initialized = true;
    }

    private static void runEventOnTag(EventContext<JsonElement> context, String tag, List<String> addList, List<String> removeList) {
        context.registerRuntimeEvent(
                Mixson.DEFAULT_PRIORITY,
                rl -> rl.toString().equals(tag.replace(":", ":tags/enchantment/")),
                "Modify tag " + tag,
                context1 -> {
                    List<JsonElement> values = context1.getFile().getAsJsonObject().getAsJsonArray("values").asList();
					addList.forEach(s -> values.add(new JsonPrimitive(s)));
                    values.removeIf(value -> {
                        String entry;
                        if (value.isJsonPrimitive()) entry = value.getAsString();
                        else entry = value.getAsJsonObject().get("id").getAsString();
                        if (entry.startsWith("#")) {
                            runEventOnTag(context1, entry.replace("#", ""), List.of(), removeList);
                            return false;
                        } else return removeList.contains(entry);
                    });
                    JsonArray newValues = new JsonArray();
                    values.forEach(newValues::add);
                    context1.getFile().getAsJsonObject().add("values", newValues);
                },
                false
        );
    }
}
