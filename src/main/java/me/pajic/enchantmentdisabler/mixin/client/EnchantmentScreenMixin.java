package me.pajic.enchantmentdisabler.mixin.client;

import com.llamalad7.mixinextras.expression.Definition;
import com.llamalad7.mixinextras.expression.Expression;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
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
            method = {"extractBackground", "renderBg"},
            at = @At("MIXINEXTRAS:EXPRESSION")
    )
    private boolean modifyLapisCostRenderBgCondition(boolean original, @Local(ordinal = 4) int goldCount, @Local(ordinal = 5) int i) {
        int val = ModUtil.lapisCost(i);
        return val > 0 ? goldCount < val : original;
    }

    @Definition(id = "experienceLevel", field = "Lnet/minecraft/client/player/LocalPlayer;experienceLevel:I")
    @Expression("?.experienceLevel < ?")
    @ModifyExpressionValue(
            method = {"extractBackground", "renderBg"},
            at = @At("MIXINEXTRAS:EXPRESSION")
    )
    private boolean modifyXpCostRenderBgCondition(boolean original, @Local(ordinal = 5) int i) {
        int val = ModUtil.xpCost(i);
        return original || val > 0 && minecraft.player.experienceLevel < val;
    }

    @Definition(id = "experienceLevel", field = "Lnet/minecraft/client/player/LocalPlayer;experienceLevel:I")
    @Expression("?.experienceLevel < ?")
    @ModifyExpressionValue(
            method = {"extractRenderState", "render"},
            at = @At("MIXINEXTRAS:EXPRESSION")
    )
    private boolean modifyXpCostTooltipCondition(boolean original, @Local(ordinal = 3) int i) {
        int val = ModUtil.xpCost(i);
        return original || val > 0 && minecraft.player.experienceLevel < val;
    }

    @ModifyArg(
            method = {"extractRenderState", "render"},
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/network/chat/MutableComponent;withStyle(Lnet/minecraft/ChatFormatting;)Lnet/minecraft/network/chat/MutableComponent;",
                    ordinal = /*? fabric {*/2/*?} else {*//*3*//*?}*/
            )
    )
    private ChatFormatting modifyLapisCostTooltipColorCondition(ChatFormatting original, @Local(ordinal = 2) int gold, @Local(ordinal = 3) int i) {
        int val = ModUtil.lapisCost(i);
        return val > 0 ? gold >= val ? ChatFormatting.GRAY : ChatFormatting.RED : original;
    }

    @WrapOperation(
            method = {"extractRenderState", "render"},
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/network/chat/Component;translatable(Ljava/lang/String;)Lnet/minecraft/network/chat/MutableComponent;",
                    ordinal = /*? fabric {*/0/*?} else {*//*1*//*?}*/
            )
    )
    private MutableComponent modifyLapisCostTooltipRequired(String key, Operation<MutableComponent> original, @Local(ordinal = 3) int i) {
        int val = ModUtil.lapisCost(i);
        return val > 0 ? Component.translatable("container.enchant.lapis.many", val) : original.call(key);
    }

    @WrapOperation(
            method = {"extractRenderState", "render"},
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/network/chat/Component;translatable(Ljava/lang/String;)Lnet/minecraft/network/chat/MutableComponent;",
                    ordinal = /*? fabric {*/1/*?} else {*//*2*//*?}*/
            )
    )
    private MutableComponent modifyXpCostTooltipRequired(String key, Operation<MutableComponent> original, @Local(ordinal = 3) int i) {
        int val = ModUtil.xpCost(i);
        return val > 0 ? Component.translatable("container.enchant.level.many", val) : original.call(key);
    }

    @WrapOperation(
            method = {"extractRenderState", "render"},
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/network/chat/Component;translatable(Ljava/lang/String;[Ljava/lang/Object;)Lnet/minecraft/network/chat/MutableComponent;",
                    ordinal = 1
            )
    )
    private MutableComponent modifyXpCostTooltipLevelRequirement(String key, Object[] args, Operation<MutableComponent> original, @Local(ordinal = 3) int i) {
        int val = ModUtil.xpCost(i);
        return val > menu.costs[i] ? original.call(key, new Object[]{val}) : original.call(key, args);
    }

    @WrapOperation(
            method = {"extractRenderState", "render"},
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/network/chat/Component;translatable(Ljava/lang/String;[Ljava/lang/Object;)Lnet/minecraft/network/chat/MutableComponent;",
                    ordinal = 2
            )
    )
    private MutableComponent modifyLapisCostTooltipRequired(String key, Object[] args, Operation<MutableComponent> original, @Local(ordinal = 3) int i) {
        int val = ModUtil.lapisCost(i);
        return val > 0 ? Component.translatable("container.enchant.lapis.many", val) : original.call(key, args);
    }

    @WrapOperation(
            method = {"extractRenderState", "render"},
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/network/chat/Component;translatable(Ljava/lang/String;[Ljava/lang/Object;)Lnet/minecraft/network/chat/MutableComponent;",
                    ordinal = 3
            )
    )
    private MutableComponent modifyXpCostTooltipRequired(String key, Object[] args, Operation<MutableComponent> original, @Local(ordinal = 3) int i) {
        int val = ModUtil.xpCost(i);
        return val > 0 ? Component.translatable("container.enchant.level.many", val) : original.call(key, args);
    }
}
