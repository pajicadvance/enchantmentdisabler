package me.pajic.enchantmentdisabler.mixin;

import com.llamalad7.mixinextras.injector.ModifyReceiver;
import me.pajic.enchantmentdisabler.Main;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.level.storage.loot.functions.EnchantRandomlyFunction;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

import java.util.Optional;
import java.util.stream.Stream;

@Mixin(EnchantRandomlyFunction.class)
public class EnchantRandomlyFunctionMixin {

    @Shadow @Final private Optional<HolderSet<Enchantment>> options;

    @ModifyReceiver(
            method = "run",
            at = @At(
                    value = "INVOKE",
                    target = "Ljava/util/stream/Stream;toList()Ljava/util/List;"
            )
    )
    private Stream<Holder<Enchantment>> filterOptionsIfPresent(Stream<Holder<Enchantment>> instance) {
        if (Main.CONFIG.disablerEnabled() && options.map(HolderSet::stream).isPresent()) {
            return instance.filter(enchantmentHolder ->
                    Main.CONFIG.disabledEnchantments().stream().noneMatch(s ->
                            enchantmentHolder.is(ResourceLocation.parse(s))));
        }
        return instance;
    }
}
