package me.pajic.enchantmentdisabler;

import me.pajic.enchantmentdisabler.config.ModConfig;
import me.pajic.enchantmentdisabler.mixson.ResourceModifications;
import net.fabricmc.api.ModInitializer;

public class Main implements ModInitializer {

    public static final ModConfig CONFIG = ModConfig.createAndLoad();

    @Override
    public void onInitialize() {
        ResourceModifications.init();
    }
}
