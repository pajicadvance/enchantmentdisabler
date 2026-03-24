package me.pajic.enchantmentdisabler.platform.fabric;

//? fabric {

import me.pajic.enchantmentdisabler.platform.Platform;
import net.fabricmc.loader.api.FabricLoader;

public class FabricPlatform implements Platform {

	@Override
	public boolean isDevelopmentEnvironment() {
		return FabricLoader.getInstance().isDevelopmentEnvironment();
	}
}
//?}
