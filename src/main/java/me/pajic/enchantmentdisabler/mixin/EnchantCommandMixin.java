package me.pajic.enchantmentdisabler.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import me.pajic.enchantmentdisabler.Main;
import me.pajic.enchantmentdisabler.util.ModUtil;
import net.minecraft.core.Holder;
import net.minecraft.server.commands.EnchantCommand;
import net.minecraft.tags.EnchantmentTags;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.ItemEnchantments;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(EnchantCommand.class)
public class EnchantCommandMixin {

    @ModifyExpressionValue(
            method = "enchant",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/item/enchantment/EnchantmentHelper;isEnchantmentCompatible(Ljava/util/Collection;Lnet/minecraft/core/Holder;)Z"
            )
    )
    private static boolean allowMultipleProtectionEnchantments(boolean original,
                                                               @Local ItemStack itemStack,
                                                               @Local(argsOnly = true) Holder<Enchantment> enchantment,
                                                               @Local(argsOnly = true) int level
    ) {
        if (Main.CONFIG.protection.allowMultipleProtectionEnchantments() && enchantment.is(EnchantmentTags.ARMOR_EXCLUSIVE)) {
            ItemEnchantments.Mutable protectionEnchantments = new ItemEnchantments.Mutable(ItemEnchantments.EMPTY);
            ModUtil.updateProtectionEnchantments(protectionEnchantments, EnchantmentHelper.getEnchantmentsForCrafting(itemStack));
            if (protectionEnchantments.getLevel(enchantment) == 0) {
                protectionEnchantments.set(enchantment, level);
            }
            else {
                protectionEnchantments.upgrade(enchantment, level);
            }
            if (protectionEnchantments.keySet().size() <= Main.CONFIG.protection.maxProtectionEnchantments()) {
                return true;
            }
        }
        return original;
    }
}
