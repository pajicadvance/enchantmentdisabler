package me.pajic.enchantmentdisabler;

import me.pajic.enchantmentdisabler.config.ModCommonConfig;
import me.pajic.enchantmentdisabler.config.ModServerConfig;
import me.pajic.enchantmentdisabler.mixson.ResourceModifications;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;

@Mod("enchantmentdisabler")
public class Main {

    public Main(IEventBus modEventBus, ModContainer modContainer) {
        modContainer.registerConfig(ModConfig.Type.COMMON, ModCommonConfig.COMMON_SPEC);
        modContainer.registerConfig(ModConfig.Type.SERVER, ModServerConfig.SERVER_SPEC);
        modEventBus.addListener(this::onInitialize);
    }

    public void onInitialize(FMLCommonSetupEvent event) {
        ResourceModifications.init();
    }
}
