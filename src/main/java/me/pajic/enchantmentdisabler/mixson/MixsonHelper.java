package me.pajic.enchantmentdisabler.mixson;

import com.google.gson.JsonElement;
import me.pajic.enchantmentdisabler.ED;
import net.ramixin.mixson.Mixson;
import net.ramixin.mixson.MixsonCodecs;
import net.ramixin.mixson.enums.ErrorPolicy;
import net.ramixin.mixson.enums.Lifetime;
import net.ramixin.mixson.util.Index;
import net.ramixin.mixson.util.functions.Event;

import java.util.UUID;
import java.util.function.Predicate;

public class MixsonHelper {

	private static final ErrorPolicy ERROR_POLICY = ED.xplat().isDebug() ? ErrorPolicy.THROW : ErrorPolicy.LOG;

	public static UUID registerSingleJsonPersistent(String eventName, Index target, Event<JsonElement> event) {
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

	public static UUID registerMultiJsonPersistent(String eventName, Predicate<Index> resourcePredicate, Event<JsonElement> event) {
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
