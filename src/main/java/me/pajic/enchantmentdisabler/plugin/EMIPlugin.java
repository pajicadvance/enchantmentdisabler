package me.pajic.enchantmentdisabler.plugin;

import dev.emi.emi.api.EmiInitRegistry;
import dev.emi.emi.api.EmiPlugin;
import dev.emi.emi.api.EmiRegistry;
import me.pajic.enchantmentdisabler.Main;
import me.pajic.enchantmentdisabler.util.ModUtil;

public class EMIPlugin implements EmiPlugin {
    @Override
    public void initialize(EmiInitRegistry registry) {
        if (Main.CONFIG.disablerEnabled()) {
            registry.disableStacks(emiStack -> ModUtil.filterStacks(emiStack.getItemStack()));
        }
    }

    @Override
    public void register(EmiRegistry registry) {}
}
