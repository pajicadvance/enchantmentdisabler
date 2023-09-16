package me.pajic.enchantmentdisabler.mixin;

import me.pajic.enchantmentdisabler.util.EnchantmentUtil;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.village.TradeOffers;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

import java.util.Arrays;
import java.util.List;

@Mixin(TradeOffers.EnchantBookFactory.class)
public abstract class EnchantBookFactoryMixin {

    @ModifyArg(method = "<init>(III[Lnet/minecraft/enchantment/Enchantment;)V", at = @At(value = "INVOKE", target = "Ljava/util/Arrays;asList([Ljava/lang/Object;)Ljava/util/List;"))
    private <T> T[] modifyArg(T[] list) {
        List<Enchantment> possibleEnchantments = (List<Enchantment>) Arrays.asList(list);
        return (T[]) EnchantmentUtil.removeEnchantmentsFromList(possibleEnchantments).toArray();
    }
}
