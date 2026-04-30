package dev.meteorbee.basedInv.listener;

import dev.meteorbee.basedInv.BasedInv;
import dev.meteorbee.basedInv.util.GearGiver;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class FirstJoinListener implements Listener {

    private final BasedInv plugin;

    public FirstJoinListener(BasedInv plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onFirstJoin(PlayerJoinEvent event) {
        if (event.getPlayer().hasPlayedBefore()) return;

        var config = plugin.getConfigManager().getFirstJoinConfig();
        if (!config.isEnabled()) return;

        // 1-tick delay so spawn teleports from other plugins settle first
        plugin.getServer().getScheduler().runTaskLater(plugin, () -> {
            if (event.getPlayer().isOnline())
                GearGiver.apply(event.getPlayer(), config, plugin);
        }, 1L);
    }
}