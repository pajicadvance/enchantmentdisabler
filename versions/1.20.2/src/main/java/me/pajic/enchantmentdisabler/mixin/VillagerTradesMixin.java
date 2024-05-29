package me.pajic.enchantmentdisabler.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import com.terraformersmc.modmenu.util.mod.Mod;
import io.wispforest.owo.config.Option;
import me.pajic.enchantmentdisabler.Main;
import me.pajic.enchantmentdisabler.util.ModUtil;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.npc.VillagerTrades;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.trading.MerchantOffer;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;
import java.util.function.Consumer;

@Mixin(VillagerTrades.class)
public abstract class VillagerTradesMixin {

    @Mixin(VillagerTrades.EnchantBookForEmeralds.class)
    public abstract static class EnchantBookForEmeraldsMixin {

        @Shadow
        @Final
        @Mutable
        private List<Enchantment> tradeableEnchantments;

        // Removes blacklisted enchantments from the list of possible enchantments for enchanted book trades
        @Inject(
                method = "<init>(III[Lnet/minecraft/world/item/enchantment/Enchantment;)V",
                at = @At("TAIL")
        )
        private void modifyEnchantmentList(int villagerXp, int minLevel, int maxLevel, Enchantment[] tradeableEnchantments, CallbackInfo ci) {
            if (Main.CONFIG.disablerEnabled()) {
                this.tradeableEnchantments = ModUtil.filterList(List.of(tradeableEnchantments));
            }
        }

        // If there are no possible enchantments, the enchanted book trade is replaced with a regular book trade
        @Inject(
                method = "getOffer",
                at = @At("HEAD"),
                cancellable = true
        )
        private void replaceTrade(Entity entity, RandomSource random, CallbackInfoReturnable<MerchantOffer> cir) {
            if (Main.CONFIG.disablerEnabled()) {
                List<Enchantment> list = ModUtil.filterList(BuiltInRegistries.ENCHANTMENT.stream().filter(Enchantment::isTradeable).toList());
                if (list.isEmpty()) {
                    cir.setReturnValue(new MerchantOffer(
                            new ItemStack(Items.EMERALD, 1),
                            new ItemStack(Items.BOOK, 1),
                            16, 10, 0.05F));
                }
            }
        }

        // Sets the max amount of uses of enchanted book trades
        @WrapOperation(
                method = "getOffer",
                at = @At(value = "NEW", target = "(Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/item/ItemStack;IIF)Lnet/minecraft/world/item/trading/MerchantOffer;")
        )
        private MerchantOffer setTradeUses(ItemStack baseCostA, ItemStack costB, ItemStack result, int maxUses, int xp, float priceMultiplier, Operation<MerchantOffer> original) {
            if (Main.CONFIG.tradesNest.modifyEnchantedBookTradeUses()) {
                return original.call(baseCostA, costB, result, Main.CONFIG.tradesNest.maxEnchantedBookTradeUses(), xp, priceMultiplier);
            }
            else {
                return original.call(baseCostA, costB, result, maxUses, xp, priceMultiplier);
            }
        }

        // Modifies the maximum enchantment level
        @ModifyExpressionValue(
                method = "getOffer",
                at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/enchantment/Enchantment;getMaxLevel()I")
        )
        private int setMaxEnchantmentLevel(int original, @Local Enchantment enchantment) {
            if (Main.CONFIG.maxLevelNest.modifyMaxLevels()) {
                return Math.min(original, ModUtil.getMaxLevel(enchantment));
            }
            else {
                return original;
            }
        }
    }

    @Mixin(VillagerTrades.EnchantedItemForEmeralds.class)
    public abstract static class EnchantedItemForEmeraldsMixin {

        // Sets the max amount of uses of enchanted item trades
        @ModifyExpressionValue(
                method = "getOffer",
                at = @At(value = "FIELD", target = "Lnet/minecraft/world/entity/npc/VillagerTrades$EnchantedItemForEmeralds;maxUses:I")
        )
        private int setTradeUses(int original) {
            if (Main.CONFIG.tradesNest.modifyEnchantedItemTradeUses()) {
                return Main.CONFIG.tradesNest.maxEnchantedItemTradeUses();
            }
            else {
                return original;
            }
        }
    }
}