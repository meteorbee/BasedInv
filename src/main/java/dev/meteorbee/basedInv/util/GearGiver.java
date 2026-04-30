package dev.meteorbee.basedInv.util;

import dev.meteorbee.basedInv.config.GearConfig;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Map;

public final class GearGiver {

    private GearGiver() {}

    public static void apply(Player player, GearConfig config, JavaPlugin plugin) {
        if (!config.isEnabled()) return;

        if (config.isClearFirst())
            player.getInventory().clear();

        for (Map.Entry<Integer, ItemStack> entry : config.getItems().entrySet()) {
            int slot = entry.getKey();
            if (slot < 0 || slot > 40) {
                plugin.getLogger().warning("GearGiver: slot " + slot + " out of range, skipping.");
                continue;
            }
            player.getInventory().setItem(slot, entry.getValue().clone());
        }

        for (String cmd : config.getCommands()) {
            plugin.getServer().dispatchCommand(
                    plugin.getServer().getConsoleSender(),
                    cmd.replace("{player}", player.getName())
            );
        }
    }
}