package me.pajic.enchantmentdisabler.platform.neoforge;

//? neoforge {

/*import me.pajic.enchantmentdisabler.ED;
import me.pajic.enchantmentdisabler.mixson.DataPatches;import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.registries.RegisterEvent;

@Mod(ED.MOD_ID)
@EventBusSubscriber(modid = ED.MOD_ID)
public class NeoforgeEntrypoint {

	@SubscribeEvent
	private static void onCommonSetup(FMLCommonSetupEvent event) {
		ED.onInitialize();
	}

	@SubscribeEvent
	private static void initDataPatches(RegisterEvent event) {
		DataPatches.init();
	}
}
*///?}
