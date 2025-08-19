package me.pajic.enchantmentdisabler;

import me.fzzyhmstrs.fzzy_config.api.ConfigApiJava;
import me.pajic.enchantmentdisabler.config.ModConfig;
import me.pajic.enchantmentdisabler.mixson.ResourceModifications;
import net.fabricmc.api.ModInitializer;
import net.minecraft.resources.ResourceLocation;

public class Main implements ModInitializer {
    public static final String MOD_ID = "enchantmentdisabler";
    public static final ResourceLocation CONFIG_RL = ResourceLocation.fromNamespaceAndPath(MOD_ID, "config");
    public static ModConfig CONFIG = ConfigApiJava.registerAndLoadConfig(ModConfig::new);

    @Override
    public void onInitialize() {
        ResourceModifications.init();
    }
}
