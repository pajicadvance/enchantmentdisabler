package me.pajic.enchantmentdisabler.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.ref.LocalIntRef;
import me.pajic.enchantmentdisabler.Main;
import net.minecraft.core.HolderSet;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.EnchantmentTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.EnchantmentMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.EnchantmentInstance;
import net.objecthunter.exp4j.Expression;
import net.objecthunter.exp4j.ExpressionBuilder;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Slice;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;
import java.util.Optional;

@Mixin(EnchantmentMenu.class)
public abstract class EnchantmentMenuMixin {

    @Shadow
    @Final
    private ContainerLevelAccess access;

    @Shadow
    @Final
    private RandomSource random;

    @SuppressWarnings("unchecked")
    @ModifyExpressionValue(
            method = "slotsChanged",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/item/ItemStack;isEnchantable()Z"
            )
    )
    private boolean dontUpdateIfNoEnchantmentsAvailable(boolean original, @Local ItemStack itemStack) {
        if (Main.CONFIG.disablerEnabled() && Main.CONFIG.enchantingTable.enchantingTableEnabled()) {
            final boolean[] con = {true};
            access.execute((level, pos) -> {
                Optional<HolderSet.Named<Enchantment>> possibleEnchantments =
                        level.registryAccess().registryOrThrow(Registries.ENCHANTMENT).getTag(EnchantmentTags.IN_ENCHANTING_TABLE);
                List<EnchantmentInstance> list = EnchantmentHelper.getAvailableEnchantmentResults(
                        EnchantmentHelper.getEnchantmentCost(random, 2, 15, itemStack),
                        itemStack,
                        ((HolderSet.Named)possibleEnchantments.get()).stream()
                );
                if (list.isEmpty()) con[0] = false;
            });
            return original && con[0];
        }
        else {
            return original;
        }
    }

    @Inject(
            method = "clickMenuButton",
            slice = @Slice(
                    from = @At(value = "INVOKE", target = "Lnet/minecraft/world/Container;getItem(I)Lnet/minecraft/world/item/ItemStack;", ordinal = 0),
                    to = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/ItemStack;getCount()I", ordinal = 0)
            ),
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/ItemStack;isEmpty()Z")
    )
    private void modifyLapisCost(Player player, int id, CallbackInfoReturnable<Boolean> cir, @Local(ordinal = 1) LocalIntRef i) {
        if (Main.CONFIG.enchantingTable.modifyLapisCost()) {
            Expression expression = new ExpressionBuilder(Main.CONFIG.enchantingTable.lapisCostFormula())
                    .variable("id")
                    .build().setVariable("id", id);
            if (expression.validate().isValid()) {
                int newValue = (int) expression.evaluate();
                if (newValue > 0) {
                    i.set(newValue);
                }
                else {
                    i.set(1);
                }
            }
        }
    }
}