package me.pajic.enchantmentdisabler.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import me.pajic.enchantmentdisabler.util.EnchantmentUtil;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.util.*;

public class Config {

    private static final Logger LOGGER = LoggerFactory.getLogger("EnchantmentDisabler-Config");
    private static final Path configFile = FabricLoader.getInstance().getConfigDir().resolve("enchantmentdisabler.json");
    private static final List<Enchantment> ENCHANTMENTS = Registries.ENCHANTMENT.stream().toList();
    private static final List<Enchantment> DISABLED_BY_DEFAULT = Arrays.asList(
            Enchantments.MENDING,
            Enchantments.VANISHING_CURSE,
            Enchantments.BINDING_CURSE,
            Enchantments.THORNS
    );
    private record ConfigOption(String enchantmentId, boolean disabled){}

    public Config() {

        try (FileReader reader = new FileReader(configFile.toFile())) {
            Gson gson = new Gson();
            List<ConfigOption> options = new ArrayList<>(List.of(gson.fromJson(reader, ConfigOption[].class)));
            List<ConfigOption> removed = new ArrayList<>();
            List<Enchantment> all = new ArrayList<>();
            List<Enchantment> disabled = new ArrayList<>();

            for (ConfigOption configOption : options) {
                Identifier id = new Identifier(configOption.enchantmentId);
                Enchantment enchantment = Registries.ENCHANTMENT.get(id);
                if (enchantment != null) {
                    LOGGER.info("All list addition: " + id);
                    all.add(enchantment);

                    if (configOption.disabled) {
                        LOGGER.info("Disabled list addition: " + id);
                        disabled.add(enchantment);
                    }
                } else {
                    LOGGER.info("Removed list addition: " + id);
                    removed.add(configOption);
                }
            }

            if (!removed.isEmpty()) {
                options.removeAll(removed);
                LOGGER.info("Removed config options: " + removed);
            }

            EnchantmentUtil.ENCHANTMENT_BLACKLIST = new ArrayList<>(disabled);
            LOGGER.info("Updated blacklist: " + EnchantmentUtil.ENCHANTMENT_BLACKLIST);

            List<Enchantment> newEnchantments = new ArrayList<>(ENCHANTMENTS);
            newEnchantments.removeAll(all);

            Gson gson2 = new GsonBuilder().setPrettyPrinting().create();
            try (FileWriter writer = new FileWriter(configFile.toFile())) {
                if (!newEnchantments.isEmpty()) {
                    newEnchantments.forEach(enchantment -> options.add(new ConfigOption(Objects.requireNonNull(EnchantmentHelper.getEnchantmentId(enchantment)).toString(), false)));
                    LOGGER.info("New enchantments: " + newEnchantments);
                }
                gson2.toJson(options, writer);
                LOGGER.info("Updated mod config");
            } catch (IOException e) {
                LOGGER.error("Failed to save mod config", e);
            }
        } catch (FileNotFoundException e) {
            LOGGER.info("Config not found, initializing");
            initializeConfig();
        } catch (IOException e) {
            LOGGER.error("Failed to read mod config", e);
        }
    }

    private void initializeConfig() {

        try (FileWriter writer = new FileWriter(configFile.toFile())) {
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            List<ConfigOption> options = new ArrayList<>();

            for (Enchantment enchantment : ENCHANTMENTS) {
                String id = Objects.requireNonNull(EnchantmentHelper.getEnchantmentId(enchantment)).toString();
                LOGGER.info("Initial add: " + id);

                if (DISABLED_BY_DEFAULT.contains(enchantment)) {
                    options.add(new ConfigOption(id, true));
                }
                else {
                    options.add(new ConfigOption(id, false));
                }
            }

            EnchantmentUtil.ENCHANTMENT_BLACKLIST = new ArrayList<>(DISABLED_BY_DEFAULT);
            gson.toJson(options, writer);
            LOGGER.info("Config initialized");
        } catch (IOException e) {
            LOGGER.error("Failed to save mod config", e);
        }
    }
}
