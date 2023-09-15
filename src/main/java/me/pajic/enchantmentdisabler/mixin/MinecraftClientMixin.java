package me.pajic.enchantmentdisabler.mixin;

import me.pajic.enchantmentdisabler.config.Config;
import net.minecraft.client.MinecraftClient;
import net.minecraft.server.SaveLoader;
import net.minecraft.world.level.storage.LevelStorage;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.function.Function;

@Mixin(MinecraftClient.class)
public class MinecraftClientMixin {

    @Inject(method = "startIntegratedServer(Ljava/lang/String;Ljava/util/function/Function;Ljava/util/function/Function;ZLnet/minecraft/client/MinecraftClient$WorldLoadAction;)V", at = @At("HEAD"))
    private void preStart(String worldName, Function<LevelStorage.Session, SaveLoader.DataPackSettingsSupplier> dataPackSettingsSupplierGetter, Function<LevelStorage.Session, SaveLoader.SavePropertiesSupplier> savePropertiesSupplierGetter, boolean safeMode, MinecraftClient.WorldLoadAction worldLoadAction, CallbackInfo ci) {
        new Config();
    }
}
