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
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.ItemEnchantments;

import java.util.Optional;

public class REIPlugin implements REIClientPlugin {

    @SuppressWarnings({"UnstableApiUsage", "DataFlowIssue"})
    @Override
    public void registerBasicEntryFiltering(BasicFilteringRule<?> rule) {
        Main.CONFIG.disabler.disabledEnchantments.forEach(rl -> {
            Minecraft mc = Minecraft.getInstance();
            ItemStack enchantedBook = Items.ENCHANTED_BOOK.getDefaultInstance();
            ItemEnchantments.Mutable enchantments = new ItemEnchantments.Mutable(ItemEnchantments.EMPTY);
            Optional<Holder.Reference<Enchantment>> enchantment = mc.level.registryAccess()
                            .lookupOrThrow(Registries.ENCHANTMENT)
                            .get(ResourceKey.create(Registries.ENCHANTMENT, rl));
            if (enchantment.isPresent()) {
                enchantments.set(enchantment.get(), enchantment.get().value().getMaxLevel());
                enchantedBook.set(DataComponents.STORED_ENCHANTMENTS, enchantments.toImmutable());
                rule.hide(EntryStacks.of(enchantedBook));
            }
        });
    }
}
