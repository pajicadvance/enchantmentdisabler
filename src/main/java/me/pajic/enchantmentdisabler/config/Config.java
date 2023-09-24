package me.pajic.enchantmentdisabler.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import it.unimi.dsi.fastutil.objects.Object2BooleanArrayMap;
import me.pajic.enchantmentdisabler.util.EnchantmentUtil;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
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
    private static final Gson GSON = new GsonBuilder()
            .registerTypeAdapter(Identifier.class, new Identifier.Serializer())
            .setPrettyPrinting()
            .create();
    private static final Path configFile = FabricLoader.getInstance().getConfigDir().resolve("enchantmentdisabler.json");
    private static final List<Enchantment> ENCHANTMENTS = Registry.ENCHANTMENT.stream().toList();
    private static final List<Enchantment> DISABLED_BY_DEFAULT = Arrays.asList(
            Enchantments.MENDING,
            Enchantments.VANISHING_CURSE,
            Enchantments.BINDING_CURSE,
            Enchantments.THORNS
    );

    public Config() {
        try (FileReader reader = new FileReader(configFile.toFile())) {
            Map<Identifier, Boolean> options = GSON.fromJson(reader, new TypeToken<Map<Identifier, Boolean>>() {}.getType());
            List<Identifier> removed = new ArrayList<>();
            List<Enchantment> all = new ArrayList<>();
            List<Enchantment> disabled = new ArrayList<>();

            for (Map.Entry<Identifier, Boolean> identifierBooleanEntry : options.entrySet()) {
                Identifier identifier = identifierBooleanEntry.getKey();
                boolean disable = identifierBooleanEntry.getValue();
                Enchantment enchantment = Registry.ENCHANTMENT.get(identifier);
                if (enchantment != null) {
                    all.add(enchantment);
                    if (disable) {
                        disabled.add(enchantment);
                    }
                } else {
                    removed.add(identifier);
                }
            }

            removed.forEach(identifier -> {
                options.remove(identifier);
                LOGGER.warn("Enchantment with id {} does not exist; removing...", identifier);
            });

            EnchantmentUtil.ENCHANTMENT_BLACKLIST = new ArrayList<>(disabled);

            List<Enchantment> newEnchantments = new ArrayList<>(ENCHANTMENTS);
            newEnchantments.removeAll(all);

            try (FileWriter writer = new FileWriter(configFile.toFile())) {
                if (!newEnchantments.isEmpty()) {
                    newEnchantments.forEach(enchantment -> options.put(EnchantmentHelper.getEnchantmentId(enchantment), false));
                }
                GSON.toJson(options, writer);
            } catch (IOException e) {
                LOGGER.error("Failed to save mod config", e);
            }
        } catch (FileNotFoundException | JsonSyntaxException e) {
            LOGGER.warn("Config doesn't exist or is malformed, initializing new mod config...");
            initializeConfig();
        } catch (IOException e) {
            LOGGER.error("Failed to read mod config", e);
        }
    }

    private void initializeConfig() {
        try (FileWriter writer = new FileWriter(configFile.toFile())) {
            Map<Identifier, Boolean> options = new Object2BooleanArrayMap<>();

            for (Enchantment enchantment : ENCHANTMENTS) {
                options.put(Objects.requireNonNull(EnchantmentHelper.getEnchantmentId(enchantment)), DISABLED_BY_DEFAULT.contains(enchantment));
            }

            EnchantmentUtil.ENCHANTMENT_BLACKLIST = new ArrayList<>(DISABLED_BY_DEFAULT);
            GSON.toJson(options, writer);
        } catch (IOException e) {
            LOGGER.error("Failed to save mod config", e);
        }
    }
}