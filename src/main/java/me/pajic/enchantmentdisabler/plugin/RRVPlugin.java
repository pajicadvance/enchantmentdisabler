package me.pajic.enchantmentdisabler.plugin;

import cc.cassian.rrv.api.ReliableRecipeViewerClientPlugin;
import cc.cassian.rrv.api.recipe.ItemView;
import me.pajic.enchantmentdisabler.ED;
import me.pajic.enchantmentdisabler.util.ModUtil;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;

public class RRVPlugin implements ReliableRecipeViewerClientPlugin {

	@Override
	public void onIntegrationInitialize() {
		ED.CONFIG.disabler.disabledEnchantmentsV2.forEach((identifier, sources) -> {
			if (ModUtil.allSourcesDisabled(sources)) ItemView.excludeEnchantment(ResourceKey.create(Registries.ENCHANTMENT, identifier));
		});
	}
}
