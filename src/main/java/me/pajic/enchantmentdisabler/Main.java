package me.pajic.enchantmentdisabler;

import me.pajic.enchantmentdisabler.config.Config;
import net.fabricmc.api.ModInitializer;

public class Main implements ModInitializer {

    public static final Config CONFIG = Config.createAndLoad();

    @Override
    public void onInitialize() {
    }
}