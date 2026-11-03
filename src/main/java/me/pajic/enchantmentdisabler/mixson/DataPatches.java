package me.pajic.enchantmentdisabler.mixson;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import me.pajic.enchantmentdisabler.ED;
import me.pajic.enchantmentdisabler.util.ModUtil;
import net.ramixin.mixson.EventContext;
import net.ramixin.mixson.util.Index;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class DataPatches {

	private static final String NON_TREASURE = "minecraft:non_treasure";
	private static final List<String> TRADE_TAGS = List.of("minecraft:on_traded_equipment", "minecraft:tradeable");
	private static final List<String> LOOT_TAGS = List.of("minecraft:on_random_loot", "minecraft:on_mob_spawn_equipment");
	private static final List<String> REMOVE_NON_TREASURE = new ArrayList<>();
	private static final List<String> ADD_LOOT = new ArrayList<>();
	private static final List<String> ADD_TABLE = new ArrayList<>();
	private static final List<String> ADD_TRADE = new ArrayList<>();
	private static final List<String> REMOVE_LOOT = new ArrayList<>();
	private static final List<String> REMOVE_TABLE = new ArrayList<>();
	private static final List<String> REMOVE_TRADE = new ArrayList<>();

	public static void init() {
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

		MixsonHelper.registerSingleJson(
				"Modify non-treasure tag",
				new Index("minecraft:tags/enchantment/non_treasure"),
				context -> {
					List<String> entries = context.getFile().getAsJsonObject().getAsJsonArray("values").asList().stream().map(DataPatches::entryId).toList();
					Map.of("loot", ADD_LOOT, "table", ADD_TABLE, "trade", ADD_TRADE).forEach((source, addList) -> {
						JsonArray values = new JsonArray();
						addList.stream().filter(entries::contains).forEach(values::add);
						JsonObject tag = new JsonObject();
						tag.add("values", values);
						context.createResource(new Index(enabledTag(source).replace(":", ":tags/enchantment/")), tag);
					});
					runEventOnTag(context, NON_TREASURE, null, REMOVE_NON_TREASURE);
				}
		);
		MixsonHelper.registerSingleJson(
				"Modify enchanting table tags",
				new Index("minecraft:tags/enchantment/in_enchanting_table"),
				context -> runEventOnTag(context, "minecraft:in_enchanting_table", "table", REMOVE_TABLE)
		);
		LOOT_TAGS.forEach(s -> MixsonHelper.registerSingleJson(
				"Modify random loot tags",
				new Index(s.replace(":", ":tags/enchantment/")),
				context -> runEventOnTag(context, s, "loot", REMOVE_LOOT)
		));
		TRADE_TAGS.forEach(s -> MixsonHelper.registerSingleJson(
				"Modify trade tags",
				new Index(s.replace(":", ":tags/enchantment/")),
				context -> runEventOnTag(context, s, "trade", REMOVE_TRADE)
		));

		int globalMaxLevel = ED.CONFIG.maxLevel.globalMaxLevel.get();
		if (globalMaxLevel > 0) {
			MixsonHelper.registerMultiJson(
					"Set global max level " + globalMaxLevel,
					index -> index.id().getPath().startsWith("enchantment/"),
					context -> context.getFile().getAsJsonObject().addProperty("max_level", globalMaxLevel)
			);
		}

		ED.CONFIG.maxLevel.maxLevels.forEach((key, value) -> {
			String namespace = key.getNamespace();
			String path = key.getPath();
			MixsonHelper.registerSingleJson(
					"Modify max level for enchantment " + key,
					new Index(namespace + ":enchantment/" + path),
					context -> context.getFile().getAsJsonObject().addProperty("max_level", value)
			);
		});
	}

	private static void runEventOnTag(EventContext<JsonElement> context, String tag, String source, List<String> removeList) {
		context.pullIntoRuntime(MixsonHelper.registerSingleJsonDeferred(
				"Modify tag " + tag,
				new Index(tag.replace(":", ":tags/enchantment/")),
				context1 -> {
					List<JsonElement> values = context1.getFile().getAsJsonObject().getAsJsonArray("values").asList();
					boolean referencesNonTreasure = false;
					for (JsonElement value : values) {
						if (entryId(value).equals("#" + NON_TREASURE)) referencesNonTreasure = true;
					}
					values.removeIf(value -> {
						String entry = entryId(value);
						if (entry.startsWith("#")) {
							String nestedTag = entry.replace("#", "");
							if (!nestedTag.equals(NON_TREASURE)) runEventOnTag(context1, nestedTag, null, removeList);
							return false;
						} else return removeList.contains(entry);
					});
					JsonArray newValues = new JsonArray();
					values.forEach(newValues::add);
					if (source != null && referencesNonTreasure) {
						JsonObject enabledTag = new JsonObject();
						enabledTag.addProperty("id", "#" + enabledTag(source));
						enabledTag.addProperty("required", false);
						newValues.add(enabledTag);
					}
					context1.getFile().getAsJsonObject().add("values", newValues);
				}
		));
	}

	private static String entryId(JsonElement value) {
		if (value.isJsonPrimitive()) return value.getAsString();
		return value.getAsJsonObject().get("id").getAsString();
	}

	private static String enabledTag(String source) {
		return ED.id("non_treasure_enabled_in_" + source).toString();
	}
}
