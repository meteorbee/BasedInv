package dev.meteorbee.basedInv.listener;

import dev.meteorbee.basedInv.BasedInv;
import dev.meteorbee.basedInv.util.GearGiver;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerRespawnEvent;

public class RespawnListener implements Listener {

    private final BasedInv plugin;

    public RespawnListener(BasedInv plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onRespawn(PlayerRespawnEvent event) {
        var config = plugin.getConfigManager().getRespawnConfig();
        if (!config.isEnabled()) return;

        // 1-tick delay so the inventory is fully cleared before we write to it
        plugin.getServer().getScheduler().runTaskLater(plugin, () -> {
            if (event.getPlayer().isOnline())
                GearGiver.apply(event.getPlayer(), config, plugin);
        }, 1L);
    }
}