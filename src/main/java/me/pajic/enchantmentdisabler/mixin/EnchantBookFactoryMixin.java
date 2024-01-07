package me.pajic.enchantmentdisabler.mixin;

import me.pajic.enchantmentdisabler.util.EnchantmentUtil;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.math.random.Random;
import net.minecraft.village.TradeOffer;
import net.minecraft.village.TradeOffers;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Mixin(TradeOffers.EnchantBookFactory.class)
public abstract class EnchantBookFactoryMixin {

    @Unique
    private List<Enchantment> modifiedList;

    @ModifyVariable(method = "create", at = @At("STORE"))
    public List<Enchantment> modifyEnchantmentList(List<Enchantment> list) {
        modifiedList = EnchantmentUtil.removeEnchantmentsFromList(list);
        return modifiedList;
    }

    @Inject(method = "create", at = @At(value = "INVOKE", target = "Ljava/util/List;get(I)Ljava/lang/Object;", shift = At.Shift.BEFORE), cancellable = true)
    public void replaceTrade(Entity entity, Random random, CallbackInfoReturnable<TradeOffer> cir) {
        if (modifiedList.isEmpty()) {
            cir.setReturnValue(new TradeOffer(new ItemStack(Items.EMERALD, 1), new ItemStack(Items.BOOK, 1), 16, 10, 0.05F));
        }
    }
}
