package me.pajic.enchantmentdisabler.mixin;

import me.pajic.enchantmentdisabler.config.ModCommonConfig;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.EnchantmentTags;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.level.storage.loot.functions.EnchantRandomlyFunction;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

import java.util.List;
import java.util.Optional;

@Mixin(EnchantRandomlyFunction.class)
public class EnchantRandomlyFunctionMixin {

    @Shadow @Final private Optional<HolderSet<Enchantment>> options;

    @ModifyArg(
            method = "run",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/Util;getRandomSafe(Ljava/util/List;Lnet/minecraft/util/RandomSource;)Ljava/util/Optional;"
            ),
            index = 0
    )
    private List<Holder<Enchantment>> filter(List<Holder<Enchantment>> selections) {
        if (ModCommonConfig.disablerEnabled) {
            if (options.isEmpty()) {
                return selections.stream().filter(holder -> holder.is(EnchantmentTags.ON_RANDOM_LOOT)).toList();
            }
            else {
                return selections.stream().filter(holder -> ModCommonConfig.disabledEnchantments.stream().noneMatch(
                        s -> holder.is(ResourceLocation.parse(s)))
                ).toList();
            }
        }
        return selections;
    }
}