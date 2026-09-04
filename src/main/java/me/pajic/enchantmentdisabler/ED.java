package me.pajic.enchantmentdisabler;

import me.fzzyhmstrs.fzzy_config.api.ConfigApiJava;
import me.pajic.enchantmentdisabler.config.ModConfig;
import me.pajic.enchantmentdisabler.mixson.DataPatches;
import me.pajic.enchantmentdisabler.mixson.MixsonHelper;
import me.pajic.enchantmentdisabler.platform.MultiLoaderUtil;
import net.minecraft.resources.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ED {

    public static final String MOD_ID = /*$ mod_id*/ "enchantmentdisabler";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
    public static ModConfig CONFIG = ConfigApiJava.registerAndLoadConfig(ModConfig::new);

    public static void onInitialize() {
        MixsonHelper.setDebugFlags();
        DataPatches.init();
    }

    public static Identifier id(String path) {
        return Identifier.fromNamespaceAndPath(MOD_ID, path);
    }

    public static void debugLog(String message, Object ... args) {
        if (MultiLoaderUtil.INSTANCE.isDevEnv()) LOGGER.info(message, args);
    }
}
