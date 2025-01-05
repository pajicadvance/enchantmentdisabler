package me.pajic.enchantmentdisabler.mixin;

import com.llamalad7.mixinextras.expression.Expression;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import me.pajic.enchantmentdisabler.config.ModServerConfig;
import me.pajic.enchantmentdisabler.util.ModUtil;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.EnchantmentScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.EnchantmentMenu;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(EnchantmentScreen.class)
public abstract class EnchantmentScreenMixin extends AbstractContainerScreen<EnchantmentMenu> {

    public EnchantmentScreenMixin(EnchantmentMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);
    }

    @Expression("? < ? + 1")
    @ModifyExpressionValue(
            method = "renderBg",
            at = @At("MIXINEXTRAS:EXPRESSION")
    )
    private boolean modifyLapisCostRenderBgCondition(boolean original, @Local(ordinal = 4) int k, @Local(ordinal = 5) int l) {
        if (ModServerConfig.modifyLapisCost) {
            int newValue = ModUtil.evaluateFormulaAndReturnValue(ModServerConfig.lapisCostFormula, l);
            if (newValue > 0) {
                return k < newValue;
            }
        }
        return original;
    }

    @ModifyArg(
            method = "render",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/network/chat/MutableComponent;withStyle(Lnet/minecraft/ChatFormatting;)Lnet/minecraft/network/chat/MutableComponent;",
                    ordinal = 2
            )
    )
    private ChatFormatting modifyLapisCostTooltipColorCondition(ChatFormatting original,
                                                                @Local(ordinal = 2) int i,
                                                                @Local(ordinal = 3) int j
    ) {
        if (ModServerConfig.modifyLapisCost) {
            int newValue = ModUtil.evaluateFormulaAndReturnValue(ModServerConfig.lapisCostFormula, j);
            if (newValue > 0) {
                return i >= newValue ? ChatFormatting.GRAY : ChatFormatting.RED;
            }
        }
        return original;
    }

    @WrapOperation(
            method = "render",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/network/chat/Component;translatable(Ljava/lang/String;)Lnet/minecraft/network/chat/MutableComponent;",
                    ordinal = 0
            )
    )
    private MutableComponent modifyLapisCostTooltipRequired(String key, Operation<MutableComponent> original,
                                                            @Local(ordinal = 2) int i,
                                                            @Local(ordinal = 3) int j
    ) {
        if (ModServerConfig.modifyLapisCost) {
            int newValue = ModUtil.evaluateFormulaAndReturnValue(ModServerConfig.lapisCostFormula, j);
            if (newValue > 0) {
                return Component.translatable("container.enchant.lapis.many", newValue);
            }
        }
        return original.call(key);
    }

    @WrapOperation(
            method = "render",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/network/chat/Component;translatable(Ljava/lang/String;)Lnet/minecraft/network/chat/MutableComponent;",
                    ordinal = 1
            )
    )
    private MutableComponent modifyXpCostTooltipRequired(String key, Operation<MutableComponent> original,
                                                         @Local(ordinal = 2) int i,
                                                         @Local(ordinal = 3) int j
    ) {
        if (ModServerConfig.modifyXpCost) {
            int newValue = ModUtil.evaluateFormulaAndReturnValue(ModServerConfig.xpCostFormula, j);
            if (newValue > 0) {
                return Component.translatable("container.enchant.level.many", newValue);
            }
        }
        return original.call(key);
    }

    @WrapOperation(
            method = "render",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/network/chat/Component;translatable(Ljava/lang/String;[Ljava/lang/Object;)Lnet/minecraft/network/chat/MutableComponent;",
                    ordinal = 2
            )
    )
    private MutableComponent modifyLapisCostTooltipRequired(String key, Object[] args, Operation<MutableComponent> original,
                                                            @Local(ordinal = 2) int i,
                                                            @Local(ordinal = 3) int j
    ) {
        if (ModServerConfig.modifyLapisCost) {
            int newValue = ModUtil.evaluateFormulaAndReturnValue(ModServerConfig.lapisCostFormula, j);
            if (newValue > 0) {
                return Component.translatable("container.enchant.lapis.many", newValue);
            }
        }
        return original.call(key, args);
    }

    @WrapOperation(
            method = "render",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/network/chat/Component;translatable(Ljava/lang/String;[Ljava/lang/Object;)Lnet/minecraft/network/chat/MutableComponent;",
                    ordinal = 3
            )
    )
    private MutableComponent modifyXpCostTooltipRequired(String key, Object[] args, Operation<MutableComponent> original,
                                                         @Local(ordinal = 2) int i,
                                                         @Local(ordinal = 3) int j
    ) {
        if (ModServerConfig.modifyXpCost) {
            int newValue = ModUtil.evaluateFormulaAndReturnValue(ModServerConfig.xpCostFormula, j);
            if (newValue > 0) {
                return Component.translatable("container.enchant.level.many", newValue);
            }
        }
        return original.call(key, args);
    }
}