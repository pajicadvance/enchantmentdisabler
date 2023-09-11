package me.pajic.enchantmentdisabler.mixin;

import me.pajic.enchantmentdisabler.config.Config;
import net.minecraft.server.MinecraftServer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MinecraftServer.class)
public class MinecraftServerMixin {

    @Inject(method = "loadWorld", at = @At("HEAD"))
    private void preLoad(CallbackInfo ci) {
        new Config();
    }
}
