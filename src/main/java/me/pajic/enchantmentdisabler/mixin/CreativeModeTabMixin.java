package me.pajic.enchantmentdisabler.mixin;

import me.pajic.enchantmentdisabler.config.ModCommonConfig;
import me.pajic.enchantmentdisabler.util.ModUtil;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Collection;
import java.util.Set;

@Mixin(CreativeModeTab.class)
public class CreativeModeTabMixin {

    @Shadow private Collection<ItemStack> displayItems;
    @Shadow private Set<ItemStack> displayItemsSearchTab;

    @Inject(method = "buildContents", at = @At("TAIL"))
    private void removeDisabledEnchantmentBooks(CallbackInfo ci) {
        if (ModCommonConfig.disablerEnabled) {
            filter(displayItems);
            filter(displayItemsSearchTab);
        }
    }

    @Unique
    private void filter(Collection<ItemStack> displayItems) {
        displayItems.removeIf(ModUtil::filterStacks);
    }
}