package me.pajic.enchantmentdisabler.mixin.table;

import com.llamalad7.mixinextras.expression.Expression;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import dev.kikugie.fletching_table.annotation.MixinEnvironment;
import me.pajic.enchantmentdisabler.ED;
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

import java.util.Map;

@MixinEnvironment(type = MixinEnvironment.Env.CLIENT)
@Mixin(EnchantmentScreen.class)
public abstract class EnchantmentScreenMixin extends AbstractContainerScreen<EnchantmentMenu> {

    public EnchantmentScreenMixin(EnchantmentMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);
    }

    @Expression("? < ? + 1")
    @ModifyExpressionValue(
            method = "extractBackground",
            at = @At("MIXINEXTRAS:EXPRESSION")
    )
    private boolean modifyLapisCostRenderBgCondition(boolean original, @Local(name = "goldCount") int goldCount, @Local(name = "i") int i) {
        if (ED.CONFIG.enchantingTable.modifyLapisCost.get()) {
            int val = (int) ED.CONFIG.enchantingTable.lapisCostFormula.evalSafe(Map.of('i', (double) i), i);
            return val > 0 ? goldCount < val : original;
        }
        return original;
    }

    @ModifyArg(
            method = "extractRenderState",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/network/chat/MutableComponent;withStyle(Lnet/minecraft/ChatFormatting;)Lnet/minecraft/network/chat/MutableComponent;",
                    ordinal = 2
            )
    )
    private ChatFormatting modifyLapisCostTooltipColorCondition(ChatFormatting original, @Local(name = "gold") int gold, @Local(name = "i") int i) {
        if (ED.CONFIG.enchantingTable.modifyLapisCost.get()) {
            int val = (int) ED.CONFIG.enchantingTable.lapisCostFormula.evalSafe(Map.of('i', (double) i), i);
            return val > 0 ? gold >= val ? ChatFormatting.GRAY : ChatFormatting.RED : original;
        }
        return original;
    }

    @WrapOperation(
            method = "extractRenderState",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/network/chat/Component;translatable(Ljava/lang/String;)Lnet/minecraft/network/chat/MutableComponent;",
                    ordinal = 0
            )
    )
    private MutableComponent modifyLapisCostTooltipRequired(String key, Operation<MutableComponent> original, @Local(name = "i") int i) {
        if (ED.CONFIG.enchantingTable.modifyLapisCost.get()) {
            int val = (int) ED.CONFIG.enchantingTable.lapisCostFormula.evalSafe(Map.of('i', (double) i), i);
            return val > 0 ? Component.translatable("container.enchant.lapis.many", val) : original.call(key);
        }
        return original.call(key);
    }

    @WrapOperation(
            method = "extractRenderState",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/network/chat/Component;translatable(Ljava/lang/String;)Lnet/minecraft/network/chat/MutableComponent;",
                    ordinal = 1
            )
    )
    private MutableComponent modifyXpCostTooltipRequired(String key, Operation<MutableComponent> original, @Local(name = "i") int i) {
        if (ED.CONFIG.enchantingTable.modifyXpCost.get()) {
            int val = (int) ED.CONFIG.enchantingTable.xpCostFormula.evalSafe(Map.of('i', (double) i), i);
            return val > 0 ? Component.translatable("container.enchant.level.many", val) : original.call(key);
        }
        return original.call(key);
    }

    @WrapOperation(
            method = "extractRenderState",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/network/chat/Component;translatable(Ljava/lang/String;[Ljava/lang/Object;)Lnet/minecraft/network/chat/MutableComponent;",
                    ordinal = 2
            )
    )
    private MutableComponent modifyLapisCostTooltipRequired(String key, Object[] args, Operation<MutableComponent> original, @Local(name = "i") int i) {
        if (ED.CONFIG.enchantingTable.modifyLapisCost.get()) {
            int val = (int) ED.CONFIG.enchantingTable.lapisCostFormula.evalSafe(Map.of('i', (double) i), i);
            return val > 0 ? Component.translatable("container.enchant.lapis.many", val) : original.call(key, args);
        }
        return original.call(key, args);
    }

    @WrapOperation(
            method = "extractRenderState",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/network/chat/Component;translatable(Ljava/lang/String;[Ljava/lang/Object;)Lnet/minecraft/network/chat/MutableComponent;",
                    ordinal = 3
            )
    )
    private MutableComponent modifyXpCostTooltipRequired(String key, Object[] args, Operation<MutableComponent> original, @Local(name = "i") int i) {
        if (ED.CONFIG.enchantingTable.modifyXpCost.get()) {
            int val = (int) ED.CONFIG.enchantingTable.xpCostFormula.evalSafe(Map.of('i', (double) i), i);
            return val > 0 ? Component.translatable("container.enchant.level.many", val) : original.call(key, args);
        }
        return original.call(key, args);
    }
}
