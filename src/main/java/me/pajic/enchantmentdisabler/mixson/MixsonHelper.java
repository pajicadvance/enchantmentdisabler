package me.pajic.enchantmentdisabler.mixson;

import com.google.gson.JsonElement;
import me.pajic.enchantmentdisabler.platform.MultiLoaderUtil;
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

	private static final ErrorPolicy ERROR_POLICY = MultiLoaderUtil.INSTANCE.isDevEnv() ? ErrorPolicy.THROW : ErrorPolicy.LOG;

	public static void setDebugFlags() {
		if (MultiLoaderUtil.INSTANCE.isDevEnv()) {
			Mixson.enableDebugOption(DebugOption.BASIC_LOGGING);
			Mixson.enableDebugOption(DebugOption.EXTRA_LOGGING);
			Mixson.enableDebugOption(DebugOption.EXPORT_PATCHED_FILE);
		}
	}

	public static void registerSingleJson(String eventName, Index target, Event<JsonElement> event) {
		Mixson.registerEvent(
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

	public static void registerMultiJson(String eventName, Predicate<Index> resourcePredicate, Event<JsonElement> event) {
		Mixson.registerEvent(
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
