package me.pajic.enchantmentdisabler.mixin;

import me.pajic.enchantmentdisabler.Main;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.EnchantmentTableBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(EnchantmentTableBlock.class)
public class EnchantmentTableBlockMixin {

    // Disables the functionality of the enchanting table
    @Inject(
            method = "use",
            at = @At("HEAD"),
            cancellable = true
    )
    private void disableEnchantingTableFunctionality(BlockState state, Level world, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit, CallbackInfoReturnable<InteractionResult> cir) {
        if (!Main.CONFIG.enchantingTableNest.enchantingTableEnabled()) {
            cir.setReturnValue(InteractionResult.FAIL);
        }
    }
}