package me.pajic.enchantmentdisabler.plugin;

//? >=26.1 {

import cc.cassian.rrv.api.ReliableRecipeViewerClientPlugin;
import cc.cassian.rrv.api.recipe.ItemView;
import dev.kikugie.fletching_table.annotation.fabric.Entrypoint;
import me.pajic.enchantmentdisabler.ED;
import me.pajic.enchantmentdisabler.util.ModUtil;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;

@Entrypoint("rrv_client")
public class RRVPlugin implements ReliableRecipeViewerClientPlugin {

	@Override
	public void onIntegrationInitialize() {
		ED.CONFIG.disabler.disabledEnchantmentsV2.forEach((identifier, sources) -> {
			if (ModUtil.allSourcesDisabled(sources)) ItemView.excludeEnchantment(ResourceKey.create(Registries.ENCHANTMENT, identifier));
		});
	}
}
//?}
