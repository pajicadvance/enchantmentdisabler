package me.pajic.enchantmentdisabler.util;

import me.fzzyhmstrs.fzzy_config.annotations.Translation;
import me.fzzyhmstrs.fzzy_config.validation.misc.ValidatedBoolean;

@Translation(prefix = "enchantmentdisabler.config.sources")
public class EnchantmentSources {

    public ValidatedBoolean table;
    public ValidatedBoolean loot;
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
