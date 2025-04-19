package me.pajic.enchantmentdisabler.mixin;

import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import me.pajic.enchantmentdisabler.Main;
import me.pajic.enchantmentdisabler.util.ModUtil;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.stats.Stats;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.*;
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

@Mixin(AnvilMenu.class)
public abstract class AnvilMenuMixin extends ItemCombinerMenu {

    //? if >= 1.21.4 {
    /*public AnvilMenuMixin(@Nullable MenuType<?> menuType, int containerId, Inventory inventory, ContainerLevelAccess access, ItemCombinerMenuSlotDefinition slotDefinition) {
        super(menuType, containerId, inventory, access, slotDefinition);
    }
    *///?}

    //? if 1.21.1 {
    public AnvilMenuMixin(@Nullable MenuType<?> type, int containerId, Inventory playerInventory, ContainerLevelAccess access) {
        super(type, containerId, playerInventory, access);
    }
    //?}

    @Inject(method = "onTake", at = @At("HEAD"))
    private void triggerEnchanterAdvancement(Player player, ItemStack stack, CallbackInfo ci) {
        if (
                !Main.CONFIG.enchantingTable.enchantingTableEnabled() &&
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
            method = "createResult",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/inventory/AnvilMenu;broadcastChanges()V"
            )
    )
    private void preventCombineIfLimitExceeded(CallbackInfo ci) {
        if (Main.CONFIG.maxLevel.limitObtainableEnchantmentLevel()) {
            ItemStack input = inputSlots.getItem(0);
            ItemStack material = inputSlots.getItem(1);
            ItemStack output = resultSlots.getItem(0);
            if (EnchantmentHelper.hasAnyEnchantments(output)) {
                ItemEnchantments outputEnchantments = ModUtil.getItemEnchantments(output);
                boolean flag = false;
                for (Object2IntOpenHashMap.Entry<Holder<Enchantment>> entry : outputEnchantments.entrySet()) {
                    for (Object2IntMap.Entry<String> entry1 : ModUtil.parseObtainableEnchantmentLevelLimits().object2IntEntrySet()) {
                        if (entry.getKey().is(ResourceLocation.parse(entry1.getKey()))) {
                            Enchantment e = entry.getKey().value();
                            int levelLimit = entry1.getIntValue();
                            int inputLevel = ModUtil.getItemEnchantments(input).getLevel(entry.getKey());
                            int materialLevel = ModUtil.getItemEnchantments(material).getLevel(entry.getKey());
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