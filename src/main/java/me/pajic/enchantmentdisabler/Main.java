package me.pajic.enchantmentdisabler;

import me.pajic.enchantmentdisabler.config.Config;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.fabricmc.fabric.api.resource.ResourcePackActivationType;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.resources.ResourceLocation;

public class Main implements ModInitializer {

    public static final Config CONFIG = Config.createAndLoad();

    @Override
    public void onInitialize() {

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

            if (CONFIG.protection.elementalProtection()) {
                ResourceManagerHelper.registerBuiltinResourcePack(
                        ResourceLocation.parse("enchantmentdisabler:elementalprotection"),
                        modContainer,
                        ResourcePackActivationType.ALWAYS_ENABLED
                );
            }

            if (CONFIG.protection.magicProtection()) {
                ResourceManagerHelper.registerBuiltinResourcePack(
                        ResourceLocation.parse("enchantmentdisabler:magicprotection"),
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