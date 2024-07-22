package me.pajic.enchantmentdisabler;

import me.pajic.enchantmentdisabler.config.Config;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.fabricmc.fabric.api.resource.ResourcePackActivationType;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;

public class Main implements ModInitializer {

    public static final Config CONFIG = Config.createAndLoad();
    public static MinecraftServer SERVER;

    @Override
    public void onInitialize() {

        ServerLifecycleEvents.SERVER_STARTED.register(server -> SERVER = server);

        FabricLoader.getInstance().getModContainer("enchantmentdisabler").ifPresent(modContainer -> {

            ResourceManagerHelper.registerBuiltinResourcePack(
                    ResourceLocation.parse("enchantmentdisabler:patcherlib"),
                    modContainer,
                    ResourcePackActivationType.ALWAYS_ENABLED
            );

            if (CONFIG.disablerEnabled()) {
                ResourceManagerHelper.registerBuiltinResourcePack(
                        ResourceLocation.parse("enchantmentdisabler:disabler"),
                        modContainer,
                        ResourcePackActivationType.ALWAYS_ENABLED
                );
            }

            if (CONFIG.maxLevel.modifyMaxLevels()) {
                ResourceManagerHelper.registerBuiltinResourcePack(
                        ResourceLocation.parse("enchantmentdisabler:maxlevels"),
                        modContainer,
                        ResourcePackActivationType.ALWAYS_ENABLED
                );
            }

            if (CONFIG.protection.meleeProtection()) {
                ResourceManagerHelper.registerBuiltinResourcePack(
                        ResourceLocation.parse("enchantmentdisabler:meleeprotection"),
                        modContainer,
                        ResourcePackActivationType.ALWAYS_ENABLED
                );
            }

            if (CONFIG.protection.featherFallingExclusive()) {
                ResourceManagerHelper.registerBuiltinResourcePack(
                        ResourceLocation.parse("enchantmentdisabler:featherfallexclusive"),
                        modContainer,
                        ResourcePackActivationType.ALWAYS_ENABLED
                );
            }
        });
    }
}