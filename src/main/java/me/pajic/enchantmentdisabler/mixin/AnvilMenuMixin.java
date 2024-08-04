package me.pajic.enchantmentdisabler.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import me.pajic.enchantmentdisabler.Main;
import me.pajic.enchantmentdisabler.util.ModUtil;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.core.Holder;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.stats.Stats;
import net.minecraft.tags.EnchantmentTags;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AnvilMenu;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.ItemCombinerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.ItemEnchantments;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AnvilMenu.class)
public abstract class AnvilMenuMixin extends ItemCombinerMenu {

    public AnvilMenuMixin(@Nullable MenuType<?> type, int containerId, Inventory playerInventory, ContainerLevelAccess access) {
        super(type, containerId, playerInventory, access);
    }

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

    @ModifyExpressionValue(
            method = "createResult",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/item/enchantment/Enchantment;areCompatible(Lnet/minecraft/core/Holder;Lnet/minecraft/core/Holder;)Z"
            )
    )
    private boolean allowMultipleProtectionEnchantments(boolean original,
                                                        @Local ItemEnchantments ie1,
                                                        @Local ItemEnchantments.Mutable ie2,
                                                        @Local(ordinal = 0) Holder<Enchantment> holder1,
                                                        @Local(ordinal = 1) Holder<Enchantment> holder2
    ) {
        if (
                Main.CONFIG.protection.allowMultipleProtectionEnchantments() &&
                        holder1.is(EnchantmentTags.ARMOR_EXCLUSIVE) &&
                        holder2.is(EnchantmentTags.ARMOR_EXCLUSIVE)
        ) {
            ItemEnchantments.Mutable protectionEnchantments = new ItemEnchantments.Mutable(ItemEnchantments.EMPTY);
            ModUtil.updateProtectionEnchantments(protectionEnchantments, ie1);
            ModUtil.updateProtectionEnchantments(protectionEnchantments, ie2.toImmutable());
            if (protectionEnchantments.keySet().size() <= Main.CONFIG.protection.maxProtectionEnchantments()) {
                return true;
            }
        }
        return original;
    }
}
