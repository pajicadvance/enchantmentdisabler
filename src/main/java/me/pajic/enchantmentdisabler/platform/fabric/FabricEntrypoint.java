package me.pajic.enchantmentdisabler.platform.fabric;

//? fabric {

import dev.kikugie.fletching_table.annotation.fabric.Entrypoint;
import me.pajic.enchantmentdisabler.mixson.DataPatches;
import net.fabricmc.api.ModInitializer;

@Entrypoint("main")
public class FabricEntrypoint implements ModInitializer {

	@Override
	public void onInitialize() {
		DataPatches.init();
	}
}
//?}
