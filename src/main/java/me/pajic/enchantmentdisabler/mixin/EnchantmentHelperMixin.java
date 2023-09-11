package me.pajic.enchantmentdisabler.mixin;

import me.pajic.enchantmentdisabler.util.EnchantmentUtil;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.EnchantmentLevelEntry;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.List;

@Mixin(EnchantmentHelper.class)
public class EnchantmentHelperMixin {

    @Redirect(method = "getPossibleEntries", at = @At(value = "INVOKE", target = "Ljava/util/List;add(Ljava/lang/Object;)Z"))
    private static <E> boolean redirectListAdd(List<EnchantmentLevelEntry> instance, E e) {
        EnchantmentLevelEntry entry = (EnchantmentLevelEntry) e;
        return EnchantmentUtil.preventEnchantmentAdditionToList(instance, entry);
    }
}
