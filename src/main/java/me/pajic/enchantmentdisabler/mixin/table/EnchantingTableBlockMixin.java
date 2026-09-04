package me.pajic.enchantmentdisabler.mixin.table;

import me.pajic.enchantmentdisabler.ED;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.level.block.EnchantingTableBlock;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(EnchantingTableBlock.class)
public class EnchantingTableBlockMixin {

    @Inject(
            method = "useWithoutItem",
            at = @At("HEAD"),
            cancellable = true
    )
    private void disableEnchantingTableFunctionality(CallbackInfoReturnable<InteractionResult> cir) {
        if (!ED.CONFIG.enchantingTable.enchantingTableEnabled.get()) {
            cir.setReturnValue(InteractionResult.FAIL);
        }
    }
}
