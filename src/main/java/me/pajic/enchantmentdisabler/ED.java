package me.pajic.enchantmentdisabler;

import me.fzzyhmstrs.fzzy_config.api.ConfigApiJava;
import me.pajic.enchantmentdisabler.config.ModConfig;
import me.pajic.enchantmentdisabler.platform.Platform;
import net.minecraft.resources.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

//? fabric {
import me.pajic.enchantmentdisabler.platform.fabric.FabricPlatform;
//?} neoforge {
/*import me.pajic.enchantmentdisabler.platform.neoforge.NeoforgePlatform;
*///?}

@SuppressWarnings("LoggingSimilarMessage")
public class ED {

	public static final String MOD_ID = /*$ mod_id*/ "enchantmentdisabler";
	public static final String MOD_VERSION = /*$ mod_version*/ "3.4.0";
	public static final String MOD_FRIENDLY_NAME = /*$ mod_name*/ "Enchantment Disabler";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
	public static final Identifier CONFIG_RL = id("config");
	public static ModConfig CONFIG = ConfigApiJava.registerAndLoadConfig(ModConfig::new);
	private static final Platform PLATFORM = createPlatformInstance();

	public static Platform xplat() {
		return PLATFORM;
	}

	private static Platform createPlatformInstance() {
		//? fabric {
		return new FabricPlatform();
		//?} neoforge {
		/*return new NeoforgePlatform();
		*///?}
	}

	public static Identifier id(String path) {
		return Identifier.fromNamespaceAndPath(MOD_ID, path);
	}

	public static void debugLog(String message, Object ... args) {
		if (PLATFORM.isDebug()) LOGGER.info(message, args);
	}
}
