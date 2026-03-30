package me.pajic.enchantmentdisabler.mixson;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import me.pajic.enchantmentdisabler.ED;
import me.pajic.enchantmentdisabler.util.ModUtil;
import net.ramixin.mixson.EventContext;
import net.ramixin.mixson.Mixson;
import net.ramixin.mixson.enums.DebugOption;
import net.ramixin.mixson.util.Index;

import java.util.ArrayList;
import java.util.List;

public class DataPatches {

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
		if (ED.xplat().isDebug()) {
			Mixson.enableDebugOption(DebugOption.BASIC_LOGGING);
			Mixson.enableDebugOption(DebugOption.EXTRA_LOGGING);
			Mixson.enableDebugOption(DebugOption.EXPORT_PATCHED_FILE);
		}

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

		MixsonHelper.registerSingleJsonPersistent(
				"Modify non-treasure tag",
				new Index("minecraft:tags/enchantment/non_treasure"),
				context -> runEventOnTag(context, "minecraft:non_treasure", List.of(), REMOVE_NON_TREASURE)
		);
		MixsonHelper.registerSingleJsonPersistent(
				"Modify enchanting table tags",
				new Index("minecraft:tags/enchantment/in_enchanting_table"),
				context -> runEventOnTag(context, "minecraft:in_enchanting_table", ADD_TABLE, REMOVE_TABLE)
		);
		LOOT_TAGS.forEach(s -> MixsonHelper.registerSingleJsonPersistent(
				"Modify random loot tags",
				new Index(s.replace(":", ":tags/enchantment/")),
				context -> runEventOnTag(context, s, ADD_LOOT, REMOVE_LOOT)
		));
		TRADE_TAGS.forEach(s -> MixsonHelper.registerSingleJsonPersistent(
				"Modify trade tags",
				new Index(s.replace(":", ":tags/enchantment/")),
				context -> runEventOnTag(context, s, ADD_TRADE, REMOVE_TRADE)
		));

		int globalMaxLevel = ED.CONFIG.maxLevel.globalMaxLevel.get();
		if (globalMaxLevel > 0) {
			MixsonHelper.registerMultiJsonPersistent(
					"Set global max level " + globalMaxLevel,
					index -> index.id().getPath().startsWith("enchantment/"),
					context -> context.getFile().getAsJsonObject().addProperty("max_level", globalMaxLevel)
			);
		}

		ED.CONFIG.maxLevel.maxLevels.forEach((key, value) -> {
			String namespace = key.getNamespace();
			String path = key.getPath();
			MixsonHelper.registerSingleJsonPersistent(
					"Modify max level for enchantment " + key,
					new Index(namespace + ":enchantment/" + path),
					context -> context.getFile().getAsJsonObject().addProperty("max_level", value)
			);
		});
	}

	private static void runEventOnTag(EventContext<JsonElement> context, String tag, List<String> addList, List<String> removeList) {
		context.pullIntoRuntime(MixsonHelper.registerSingleJsonDeferred(
				"Modify tag " + tag,
				new Index(tag.replace(":", ":tags/enchantment/")),
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
				}
		));
	}
}
