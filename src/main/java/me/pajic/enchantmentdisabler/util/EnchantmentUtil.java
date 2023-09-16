package me.pajic.enchantmentdisabler.util;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentLevelEntry;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.util.math.random.Random;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class EnchantmentUtil {

    public static List<Enchantment> ENCHANTMENT_BLACKLIST;

    private static final List<Enchantment> DESERT_ENCHANTMENTS = Arrays.asList(
            Enchantments.EFFICIENCY,
            Enchantments.FIRE_PROTECTION,
            Enchantments.THORNS,
            Enchantments.INFINITY
    );
    private static final List<Enchantment> JUNGLE_ENCHANTMENTS = Arrays.asList(
            Enchantments.UNBREAKING,
            Enchantments.FEATHER_FALLING,
            Enchantments.PROJECTILE_PROTECTION,
            Enchantments.POWER
    );
    private static final List<Enchantment> PLAINS_ENCHANTMENTS = Arrays.asList(
            Enchantments.PROTECTION,
            Enchantments.PUNCH,
            Enchantments.SMITE,
            Enchantments.BANE_OF_ARTHROPODS
    );
    private static final List<Enchantment> SAVANNA_ENCHANTMENTS = Arrays.asList(
            Enchantments.SHARPNESS,
            Enchantments.KNOCKBACK,
            Enchantments.BINDING_CURSE,
            Enchantments.SWEEPING
    );
    private static final List<Enchantment> SNOW_ENCHANTMENTS = Arrays.asList(
            Enchantments.SILK_TOUCH,
            Enchantments.AQUA_AFFINITY,
            Enchantments.LOOTING,
            Enchantments.FROST_WALKER
    );
    private static final List<Enchantment> SWAMP_ENCHANTMENTS = Arrays.asList(
            Enchantments.MENDING,
            Enchantments.DEPTH_STRIDER,
            Enchantments.RESPIRATION,
            Enchantments.VANISHING_CURSE
    );
    private static final List<Enchantment> TAIGA_ENCHANTMENTS = Arrays.asList(
            Enchantments.FORTUNE,
            Enchantments.BLAST_PROTECTION,
            Enchantments.FIRE_ASPECT,
            Enchantments.FLAME
    );

    public static boolean preventEnchantmentAdditionToListIfBlacklisted(List<EnchantmentLevelEntry> list, EnchantmentLevelEntry entry) {

        if (ENCHANTMENT_BLACKLIST.contains(entry.enchantment)) {
            return false;
        }

        return list.add(entry);
    }

    public static List<Enchantment> removeEnchantmentsFromList(List<Enchantment> list) {
        boolean isSpecialBook = list.size() == 1;
        List<Enchantment> filteredList = new ArrayList<>(list.stream().filter(enchantment -> !ENCHANTMENT_BLACKLIST.contains(enchantment)).toList());

        if (filteredList.isEmpty() && isSpecialBook) {
            org.joml.Random random = new org.joml.Random();

            if (list.contains(Enchantments.EFFICIENCY)) {
                List<Enchantment> possibleEnchantments = DESERT_ENCHANTMENTS.stream().filter(e -> !ENCHANTMENT_BLACKLIST.contains(e)).toList();
                if (!possibleEnchantments.isEmpty()) {
                    filteredList.add(possibleEnchantments.get(random.nextInt(possibleEnchantments.size())));
                }
            } else if (list.contains(Enchantments.UNBREAKING)) {
                List<Enchantment> possibleEnchantments = JUNGLE_ENCHANTMENTS.stream().filter(e -> !ENCHANTMENT_BLACKLIST.contains(e)).toList();
                if (!possibleEnchantments.isEmpty()) {
                    filteredList.add(possibleEnchantments.get(random.nextInt(possibleEnchantments.size())));
                }
            } else if (list.contains(Enchantments.PROTECTION)) {
                List<Enchantment> possibleEnchantments = PLAINS_ENCHANTMENTS.stream().filter(e -> !ENCHANTMENT_BLACKLIST.contains(e)).toList();
                if (!possibleEnchantments.isEmpty()) {
                    filteredList.add(possibleEnchantments.get(random.nextInt(possibleEnchantments.size())));
                }
            } else if (list.contains(Enchantments.SHARPNESS)) {
                List<Enchantment> possibleEnchantments = SAVANNA_ENCHANTMENTS.stream().filter(e -> !ENCHANTMENT_BLACKLIST.contains(e)).toList();
                if (!possibleEnchantments.isEmpty()) {
                    filteredList.add(possibleEnchantments.get(random.nextInt(possibleEnchantments.size())));
                }
            } else if (list.contains(Enchantments.SILK_TOUCH)) {
                List<Enchantment> possibleEnchantments = SNOW_ENCHANTMENTS.stream().filter(e -> !ENCHANTMENT_BLACKLIST.contains(e)).toList();
                if (!possibleEnchantments.isEmpty()) {
                    filteredList.add(possibleEnchantments.get(random.nextInt(possibleEnchantments.size())));
                }
            } else if (list.contains(Enchantments.MENDING)) {
                List<Enchantment> possibleEnchantments = SWAMP_ENCHANTMENTS.stream().filter(e -> !ENCHANTMENT_BLACKLIST.contains(e)).toList();
                if (!possibleEnchantments.isEmpty()) {
                    filteredList.add(possibleEnchantments.get(random.nextInt(possibleEnchantments.size())));
                }
            } else if (list.contains(Enchantments.FORTUNE)) {
                List<Enchantment> possibleEnchantments = TAIGA_ENCHANTMENTS.stream().filter(e -> !ENCHANTMENT_BLACKLIST.contains(e)).toList();
                if (!possibleEnchantments.isEmpty()) {
                    filteredList.add(possibleEnchantments.get(random.nextInt(possibleEnchantments.size())));
                }
            }
        }

        if (filteredList.isEmpty()) {
            org.joml.Random random = new org.joml.Random();
            List<Enchantment> possibleEnchantments = Registries.ENCHANTMENT.stream().filter(e -> !ENCHANTMENT_BLACKLIST.contains(e)).toList();
            filteredList.add(possibleEnchantments.get(random.nextInt(possibleEnchantments.size())));
        }

        return filteredList;
    }

    public static Enchantment rerollEnchantmentIfBlacklisted(ItemStack stack, Enchantment enchantment, Random random) {

        if (ENCHANTMENT_BLACKLIST.contains(enchantment)) {
            List<Enchantment> compatibleEnchantments = Registries.ENCHANTMENT.stream().filter(e -> e.isAcceptableItem(stack) && !ENCHANTMENT_BLACKLIST.contains(e)).toList();
            enchantment = compatibleEnchantments.get(random.nextInt(compatibleEnchantments.size()));
        }

        return enchantment;
    }
}
