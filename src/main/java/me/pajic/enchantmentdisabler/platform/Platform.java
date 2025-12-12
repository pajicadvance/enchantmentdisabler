package me.pajic.enchantmentdisabler.platform;

public interface Platform {
	boolean isModLoaded(String modId);

	boolean isDebug();

	ModLoader loader();

	String mcVersion();

	enum ModLoader {
		FABRIC, NEOFORGE, FORGE
	}
}
