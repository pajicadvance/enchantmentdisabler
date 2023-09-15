package me.pajic.enchantmentdisabler.mixin;

import me.pajic.enchantmentdisabler.util.EnchantmentUtil;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.loot.function.EnchantRandomlyLootFunction;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

import java.util.List;

@Mixin(EnchantRandomlyLootFunction.class)
public class EnchantRandomlyLootFunctionMixin {

    @ModifyVariable(method = "process", at = @At("STORE"))
    public List<Enchantment> modifyEnchantmentList(List<Enchantment> list) {
        return EnchantmentUtil.removeEnchantmentsFromList(list);
    }
}
