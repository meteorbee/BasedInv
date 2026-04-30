package dev.meteorbee.basedInv.config;

import dev.meteorbee.basedInv.BasedInv;
import dev.meteorbee.basedInv.util.ItemBuilder;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.util.*;
import java.util.logging.Level;

public class GearConfigManager {

    private final BasedInv plugin;
    private GearConfig firstJoinConfig;
    private GearConfig respawnConfig;

    public GearConfigManager(BasedInv plugin) {
        this.plugin = plugin;
    }

    public void loadAll() {
        saveDefault("first_join.yml");
        saveDefault("respawn.yml");
        firstJoinConfig = parseConfig("first_join.yml", "on-join-commands");
        respawnConfig   = parseConfig("respawn.yml",   "on-respawn-commands");
    }

    private void saveDefault(String fileName) {
        File file = new File(plugin.getDataFolder(), fileName);
        if (!file.exists()) plugin.saveResource(fileName, false);
    }

    private GearConfig parseConfig(String fileName, String commandsKey) {
        File file = new File(plugin.getDataFolder(), fileName);
        FileConfiguration cfg = YamlConfiguration.loadConfiguration(file);

        boolean enabled    = cfg.getBoolean("enabled", true);
        boolean clearFirst = cfg.getBoolean("clear-inventory-first", true);
        List<String> commands = cfg.getStringList(commandsKey);

        Map<Integer, ItemStack> items = new LinkedHashMap<>();

        List<?> itemsList = cfg.getList("items", Collections.emptyList());
        for (Object raw : itemsList) {
            if (!(raw instanceof Map<?, ?> rawMap)) continue;

            @SuppressWarnings("unchecked")
            Map<String, Object> map = (Map<String, Object>) rawMap;

            int slot = toInt(map.get("slot"), -1);
            if (slot < 0 || slot > 40) {
                plugin.getLogger().warning("[" + fileName + "] Invalid slot " + slot + " — skipping.");
                continue;
            }

            String material = (String) map.get("material");
            if (material == null) {
                plugin.getLogger().warning("[" + fileName + "] Slot " + slot + " has no material — skipping.");
                continue;
            }

            try {
                ItemStack item = ItemBuilder.build(
                        material,
                        toInt(map.get("amount"), 1),
                        (String) map.get("name"),
                        toStringList(map.get("lore")),
                        toEnchantmentMap(map.get("enchantments")),
                        Boolean.TRUE.equals(map.getOrDefault("unbreakable", false)),
                        (String) map.get("color")
                );
                items.put(slot, item);
            } catch (Exception e) {
                plugin.getLogger().log(Level.WARNING,
                        "[" + fileName + "] Could not build item at slot " + slot + ": " + e.getMessage(), e);
            }
        }

        plugin.getLogger().info("Loaded " + fileName + ": " + items.size() + " item(s), enabled=" + enabled);
        return new GearConfig(enabled, clearFirst, items, commands);
    }

    private static int toInt(Object v, int fallback) {
        if (v instanceof Number n) return n.intValue();
        if (v instanceof String s) { try { return Integer.parseInt(s.trim()); } catch (NumberFormatException ignored) {} }
        return fallback;
    }

    @SuppressWarnings("unchecked")
    private static List<String> toStringList(Object v) {
        return v instanceof List<?> list ? (List<String>) list : Collections.emptyList();
    }

    @SuppressWarnings("unchecked")
    private static Map<String, Integer> toEnchantmentMap(Object v) {
        if (!(v instanceof Map<?, ?> raw)) return Collections.emptyMap();
        Map<String, Integer> result = new LinkedHashMap<>();
        for (Map.Entry<?, ?> e : raw.entrySet()) result.put(String.valueOf(e.getKey()), toInt(e.getValue(), 1));
        return result;
    }

    public GearConfig getFirstJoinConfig() { return firstJoinConfig; }
    public GearConfig getRespawnConfig()   { return respawnConfig; }
}