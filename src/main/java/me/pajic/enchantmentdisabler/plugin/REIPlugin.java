package me.pajic.enchantmentdisabler.plugin;

import me.pajic.enchantmentdisabler.Main;
import me.shedaniel.rei.api.client.entry.filtering.base.BasicFilteringRule;
import me.shedaniel.rei.api.client.plugins.REIClientPlugin;
import me.shedaniel.rei.api.common.util.EntryStacks;
import net.minecraft.client.Minecraft;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.ItemEnchantments;

public class REIPlugin implements REIClientPlugin {

    @Override
    public void registerBasicEntryFiltering(BasicFilteringRule<?> rule) {
        Main.CONFIG.disabledEnchantments().forEach(enchantmentEntry -> {
            Minecraft mc = Minecraft.getInstance();
            String[] split = enchantmentEntry.split(":");
            ItemStack enchantedBook = Items.ENCHANTED_BOOK.getDefaultInstance();
            ItemEnchantments.Mutable enchantments = new ItemEnchantments.Mutable(ItemEnchantments.EMPTY);
            Holder<Enchantment> enchantment = mc.level.registryAccess()
                            .lookupOrThrow(Registries.ENCHANTMENT)
                            .getOrThrow(ResourceKey.create(
                                    Registries.ENCHANTMENT,
                                    ResourceLocation.fromNamespaceAndPath(split[0], split[1])
                            ));
            enchantments.set(enchantment, enchantment.value().getMaxLevel());
            enchantedBook.set(DataComponents.STORED_ENCHANTMENTS, enchantments.toImmutable());
            rule.hide(EntryStacks.of(enchantedBook));
        });
    }
}
