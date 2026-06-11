package me.pajic.enchantmentdisabler.mixin;

import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import me.pajic.enchantmentdisabler.ED;
import me.pajic.enchantmentdisabler.util.ModUtil;
import net.minecraft.advancements.triggers.CriteriaTriggers;
import net.minecraft.core.Holder;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.stats.Stats;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AnvilMenu;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.ItemCombinerMenu;
import net.minecraft.world.inventory.ItemCombinerMenuSlotDefinition;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.ItemEnchantments;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Map;

@Mixin(AnvilMenu.class)
public abstract class AnvilMenuMixin extends ItemCombinerMenu {

    public AnvilMenuMixin(@Nullable MenuType<?> menuType, int containerId, Inventory inventory, ContainerLevelAccess access, ItemCombinerMenuSlotDefinition slotDefinition) {
        super(menuType, containerId, inventory, access, slotDefinition);
    }

    @Inject(method = "onTake", at = @At("HEAD"))
    private void triggerEnchanterAdvancement(Player player, ItemStack stack, CallbackInfo ci) {
        if (
                !ED.CONFIG.enchantingTable.enchantingTableEnabled.get() &&
                !resultSlots.getItem(0).is(Items.ENCHANTED_BOOK) &&
                !inputSlots.getItem(0).isEnchanted() &&
                inputSlots.getItem(1).is(Items.ENCHANTED_BOOK)
        ) {
            player.awardStat(Stats.ENCHANT_ITEM);
            if (player instanceof ServerPlayer) {
                CriteriaTriggers.ENCHANTED_ITEM.trigger((ServerPlayer) player, resultSlots.getItem(0), 1);
            }
        }
    }

    @Inject(
			//? if fabric
            method = "createResult",
			//? if neoforge
			//method = "createResultInternal",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/inventory/AnvilMenu;broadcastChanges()V"
            )
    )
    private void preventCombineIfLimitExceeded(CallbackInfo ci) {
        if (ED.CONFIG.maxLevel.limitObtainableEnchantmentLevel.get()) {
            ItemStack input = inputSlots.getItem(0);
            ItemStack material = inputSlots.getItem(1);
            ItemStack output = resultSlots.getItem(0);
            if (EnchantmentHelper.hasAnyEnchantments(output)) {
                ItemEnchantments outputEnchantments = ModUtil.getItemEnchantments(output);
                boolean flag = false;
                for (Object2IntOpenHashMap.Entry<Holder<Enchantment>> entry : outputEnchantments.entrySet()) {
                    for (Map.Entry<Identifier, Integer> entry1 : ED.CONFIG.maxLevel.obtainableEnchantmentLevels.entrySet()) {
                        if (entry.getKey().is(entry1.getKey())) {
                            Enchantment e = entry.getKey().value();
                            int levelLimit = entry1.getValue();
                            int inputLevel = EnchantmentHelper.hasAnyEnchantments(input) ? ModUtil.getItemEnchantments(input).getLevel(entry.getKey()) : 0;
                            int materialLevel = EnchantmentHelper.hasAnyEnchantments(material) ? ModUtil.getItemEnchantments(material).getLevel(entry.getKey()) : 0;
                            int maxInputLevel = inputLevel == 0 ? 0 : e.getMaxLevel();
                            int maxMaterialLevel = materialLevel == 0 ? 0 : e.getMaxLevel();
                            if (inputLevel >= levelLimit && inputLevel < maxInputLevel && materialLevel >= levelLimit && materialLevel < maxMaterialLevel) {
                                resultSlots.setItem(0, ItemStack.EMPTY);
                                flag = true;
                                break;
                            }
                        }
                    }
                    if (flag) break;
                }
            }
        }
    }
}
