package me.pajic.enchantmentdisabler.mixson;

import com.google.gson.JsonElement;
import me.pajic.enchantmentdisabler.ED;
import net.ramixin.mixson.Mixson;
import net.ramixin.mixson.MixsonCodecs;
import net.ramixin.mixson.enums.DebugOption;
import net.ramixin.mixson.enums.ErrorPolicy;
import net.ramixin.mixson.enums.Lifetime;
import net.ramixin.mixson.util.Index;
import net.ramixin.mixson.util.functions.Event;

import java.util.UUID;
import java.util.function.Predicate;

public class MixsonHelper {

	private static final ErrorPolicy ERROR_POLICY = ED.xplat().isDebug() ? ErrorPolicy.THROW : ErrorPolicy.LOG;

	public static void setDebugFlags() {
		if (ED.xplat().isDebug()) {
			Mixson.enableDebugOption(DebugOption.BASIC_LOGGING);
			Mixson.enableDebugOption(DebugOption.EXTRA_LOGGING);
			Mixson.enableDebugOption(DebugOption.EXPORT_PATCHED_FILE);
		}
	}

	public static UUID registerSingleJson(String eventName, Index target, Event<JsonElement> event) {
		return Mixson.registerEvent(
				MixsonCodecs.JSON_ELEMENT,
				Mixson.DEFAULT_PRIORITY,
				Lifetime.PERSISTENT,
				ERROR_POLICY,
				eventName,
				index -> index.idEquals(target),
				event
		);
	}

	public static UUID registerSingleJsonDeferred(String eventName, Index target, Event<JsonElement> event) {
		return Mixson.registerEvent(
				MixsonCodecs.JSON_ELEMENT,
				Mixson.DEFAULT_PRIORITY,
				Lifetime.DEFERRED,
				ERROR_POLICY,
				eventName,
				index -> index.idEquals(target),
				event
		);
	}

	public static UUID registerMultiJson(String eventName, Predicate<Index> resourcePredicate, Event<JsonElement> event) {
		return Mixson.registerEvent(
				MixsonCodecs.JSON_ELEMENT,
				Mixson.DEFAULT_PRIORITY,
				Lifetime.PERSISTENT,
				ERROR_POLICY,
				eventName,
				resourcePredicate,
				event
		);
	}
}
