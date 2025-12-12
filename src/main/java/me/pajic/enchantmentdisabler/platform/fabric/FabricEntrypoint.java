package me.pajic.enchantmentdisabler.platform.fabric;

//? fabric {

import me.pajic.enchantmentdisabler.mixson.ResourceModifications;
import net.fabricmc.api.ModInitializer;

@SuppressWarnings("unused")
public class FabricEntrypoint implements ModInitializer {

	@Override
	public void onInitialize() {
		ResourceModifications.init();
	}
}
//?}
