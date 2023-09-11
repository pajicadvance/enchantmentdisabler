package me.pajic.enchantmentdisabler.mixin;

import me.pajic.enchantmentdisabler.util.EnchantmentUtil;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.village.TradeOffers;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

import java.util.List;

@Mixin(TradeOffers.EnchantBookFactory.class)
public abstract class EnchantBookFactoryMixin {

    @ModifyVariable(method = "create", at = @At("STORE"))
    public List<Enchantment> modifyEnchantmentList(List<Enchantment> list) {
        return EnchantmentUtil.removeEnchantmentsFromList(list);
    }
}
