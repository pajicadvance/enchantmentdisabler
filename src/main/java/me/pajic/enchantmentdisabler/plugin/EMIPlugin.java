package me.pajic.enchantmentdisabler.plugin;

//? <26.1 {

/*import dev.emi.emi.api.EmiEntrypoint;
import dev.emi.emi.api.EmiInitRegistry;
import dev.emi.emi.api.EmiPlugin;
import dev.emi.emi.api.EmiRegistry;
import dev.kikugie.fletching_table.annotation.fabric.Entrypoint;
import me.pajic.enchantmentdisabler.util.ModUtil;

@EmiEntrypoint
@Entrypoint("emi")
public class EMIPlugin implements EmiPlugin {

    @Override
    public void initialize(EmiInitRegistry registry) {
        registry.disableStacks(emiStack -> ModUtil.filterStacks(emiStack.getItemStack()));
    }

    @Override
    public void register(EmiRegistry registry) {}
}
*///?}
