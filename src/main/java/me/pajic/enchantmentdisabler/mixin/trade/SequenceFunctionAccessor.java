package me.pajic.enchantmentdisabler.mixin.trade;

//? >=26.1 {

import net.minecraft.world.level.storage.loot.functions.LootItemFunction;
import net.minecraft.world.level.storage.loot.functions.SequenceFunction;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

//? >26.2 {
import net.minecraft.core.HolderSet;
//?} else {
/*import java.util.List;
*///?}

@Mixin(SequenceFunction.class)
public interface SequenceFunctionAccessor {

	@Accessor
    //~ if >26.2 'List<LootItemFunction>' -> 'HolderSet<LootItemFunction>'
	HolderSet<LootItemFunction> getFunctions();
}
//?}
