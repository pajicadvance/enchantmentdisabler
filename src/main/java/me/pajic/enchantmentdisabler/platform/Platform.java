package me.pajic.enchantmentdisabler.platform;

public interface Platform {

	boolean isDevelopmentEnvironment();

	default boolean isDebug() {
		return isDevelopmentEnvironment();
	}
}
