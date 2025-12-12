package me.pajic.enchantmentdisabler.util;

import me.fzzyhmstrs.fzzy_config.annotations.Translation;
import me.fzzyhmstrs.fzzy_config.validation.misc.ValidatedBoolean;

public class EnchantmentSources {
	@Translation(prefix = "enchantmentdisabler.config.sources")
    public ValidatedBoolean table;
	@Translation(prefix = "enchantmentdisabler.config.sources")
    public ValidatedBoolean loot;
	@Translation(prefix = "enchantmentdisabler.config.sources")
    public ValidatedBoolean trade;

    public EnchantmentSources(boolean table, boolean loot, boolean trade) {
        this.table = new ValidatedBoolean(table);
        this.loot = new ValidatedBoolean(loot);
        this.trade = new ValidatedBoolean(trade);
    }

    public EnchantmentSources() {
        this.table = new ValidatedBoolean(false);
        this.loot = new ValidatedBoolean(false);
        this.trade = new ValidatedBoolean(false);
    }
}
