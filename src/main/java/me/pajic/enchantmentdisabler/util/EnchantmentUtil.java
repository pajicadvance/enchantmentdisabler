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
import java.util.Map;

public class EnchantmentUtil {
    public static List<Enchantment> ENCHANTMENT_BLACKLIST;

    private static final org.joml.Random RANDOM = new org.joml.Random();

    private static final Map<Enchantment, List<Enchantment>> POSSIBLE_ENCHANTMENTS_MAP = Map.of(
            Enchantments.EFFICIENCY, Arrays.asList(
                    Enchantments.EFFICIENCY,
                    Enchantments.FIRE_PROTECTION,
                    Enchantments.THORNS,
                    Enchantments.INFINITY
            ),
            Enchantments.UNBREAKING, Arrays.asList(
                    Enchantments.UNBREAKING,
                    Enchantments.FEATHER_FALLING,
                    Enchantments.PROJECTILE_PROTECTION,
                    Enchantments.POWER
            ),
            Enchantments.PROTECTION, Arrays.asList(
                    Enchantments.PROTECTION,
                    Enchantments.PUNCH,
                    Enchantments.SMITE,
                    Enchantments.BANE_OF_ARTHROPODS
            ),
            Enchantments.SHARPNESS, Arrays.asList(
                    Enchantments.SHARPNESS,
                    Enchantments.KNOCKBACK,
                    Enchantments.BINDING_CURSE,
                    Enchantments.SWEEPING
            ),
            Enchantments.SILK_TOUCH, Arrays.asList(
                    Enchantments.SILK_TOUCH,
                    Enchantments.AQUA_AFFINITY,
                    Enchantments.LOOTING,
                    Enchantments.FROST_WALKER
            ),
            Enchantments.MENDING, Arrays.asList(
                    Enchantments.MENDING,
                    Enchantments.DEPTH_STRIDER,
                    Enchantments.RESPIRATION,
                    Enchantments.VANISHING_CURSE
            ),
            Enchantments.FORTUNE, Arrays.asList(
                    Enchantments.FORTUNE,
                    Enchantments.BLAST_PROTECTION,
                    Enchantments.FIRE_ASPECT,
                    Enchantments.FLAME
            )
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
            list.forEach(enchantment -> {
                if (POSSIBLE_ENCHANTMENTS_MAP.containsKey(enchantment)) {
                    List<Enchantment> possibleEnchantments = POSSIBLE_ENCHANTMENTS_MAP.get(enchantment).stream().filter(e -> !ENCHANTMENT_BLACKLIST.contains(e)).toList();
                    if (!possibleEnchantments.isEmpty()) {
                        filteredList.add(possibleEnchantments.get(RANDOM.nextInt(possibleEnchantments.size())));
                    }
                }
            });
        }

        if (filteredList.isEmpty()) {
            List<Enchantment> possibleEnchantments = Registries.ENCHANTMENT.stream().filter(e -> !ENCHANTMENT_BLACKLIST.contains(e)).toList();
            filteredList.add(possibleEnchantments.get(RANDOM.nextInt(possibleEnchantments.size())));
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
