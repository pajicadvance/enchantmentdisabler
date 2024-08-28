package me.pajic.enchantmentdisabler.mixin;

import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import net.minecraft.client.gui.screens.packs.PackSelectionScreen;
import net.minecraft.client.gui.screens.packs.TransferableSelectionList;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import java.util.List;

@SuppressWarnings("rawtypes")
@Mixin(PackSelectionScreen.class)
public class PackSelectionScreenMixin {

    @WrapWithCondition(
            method = "method_29672",
            at = @At(
                    value = "INVOKE",
                    target = "Ljava/util/List;add(Ljava/lang/Object;)Z"
            )
    )
    private <E> boolean hideModResourcePacks(List instance, E e) {
        TransferableSelectionList.PackEntry packEntry = (TransferableSelectionList.PackEntry) e;
        return !packEntry.getPackId().contains("enchantmentdisabler");
    }
}
