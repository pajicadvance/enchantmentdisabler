package me.pajic.enchantmentdisabler.mixin;

import me.pajic.enchantmentdisabler.util.EnchantmentUtil;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.village.TradeOffers;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(value = TradeOffers.EnchantBookFactory.class , priority = 1500) // Just in case some other mod tries to modify this class
public abstract class EnchantBookFactoryMixin {

    @Shadow
    @Mutable
    @Final
    private List<Enchantment> possibleEnchantments;

    @Inject(method = "<init>(III[Lnet/minecraft/enchantment/Enchantment;)V", at = @At(value = "TAIL"))
    private void modifyArg(int experience, int minLevel, int maxLevel, Enchantment[] possibleEnchantments, CallbackInfo ci) {
        this.possibleEnchantments = EnchantmentUtil.removeEnchantmentsFromList(List.of(possibleEnchantments));
    }
}
