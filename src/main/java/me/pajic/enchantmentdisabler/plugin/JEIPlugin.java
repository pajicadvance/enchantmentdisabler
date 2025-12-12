package me.pajic.enchantmentdisabler.plugin;

import me.pajic.enchantmentdisabler.ED;
import me.pajic.enchantmentdisabler.util.ModUtil;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.registration.IRecipeRegistration;
import net.minecraft.client.Minecraft;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.ItemEnchantments;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

@JeiPlugin
public class JEIPlugin implements IModPlugin {

	@Override
	public @NotNull Identifier getPluginUid() {
		return ED.id("jei_plugin");
	}

	@Override
	public void registerRecipes(IRecipeRegistration registration) {
		Set<ItemStack> hiddenItems = new HashSet<>();
		ED.CONFIG.disabler.disabledEnchantmentsV2.forEach((id, sources) -> {
			if (ModUtil.allSourcesDisabled(sources)) {
				Minecraft mc = Minecraft.getInstance();
				ItemStack enchantedBook = Items.ENCHANTED_BOOK.getDefaultInstance();
				ItemEnchantments.Mutable enchantments = new ItemEnchantments.Mutable(ItemEnchantments.EMPTY);
				Optional<Holder.Reference<Enchantment>> enchantment = mc.level.registryAccess()
						.lookupOrThrow(Registries.ENCHANTMENT)
						.get(ResourceKey.create(Registries.ENCHANTMENT, id));
				if (enchantment.isPresent()) {
					enchantments.set(enchantment.get(), enchantment.get().value().getMaxLevel());
					enchantedBook.set(DataComponents.STORED_ENCHANTMENTS, enchantments.toImmutable());
					hiddenItems.add(enchantedBook);
				}
			}
		});
		registration.getIngredientManager().removeIngredientsAtRuntime(VanillaTypes.ITEM_STACK, hiddenItems);
	}
}
