package me.pajic.enchantmentdisabler.mixin;

import me.pajic.enchantmentdisabler.util.EnchantmentUtil;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.math.random.Random;
import net.minecraft.village.TradeOffer;
import net.minecraft.village.TradeOffers;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

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

    @Inject(method = "create", at = @At("HEAD"), cancellable = true)
    private void replaceTrade(Entity entity, Random random, CallbackInfoReturnable<TradeOffer> cir) {
        if (this.possibleEnchantments.isEmpty()) {
            cir.setReturnValue(new TradeOffer(new ItemStack(Items.EMERALD, 1), new ItemStack(Items.BOOK, 1), 16, 10, 0.05F));
        }
    }
}
