package dev.meteorbee.basedInv;

import dev.meteorbee.basedInv.config.GearConfigManager;
import dev.meteorbee.basedInv.listener.FirstJoinListener;
import dev.meteorbee.basedInv.listener.RespawnListener;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

public final class BasedInv extends JavaPlugin implements CommandExecutor {

    private GearConfigManager configManager;

    @Override
    public void onEnable() {
        configManager = new GearConfigManager(this);
        configManager.loadAll();

        getServer().getPluginManager().registerEvents(new FirstJoinListener(this), this);
        getServer().getPluginManager().registerEvents(new RespawnListener(this), this);

        var cmd = getCommand("basedinv");
        if (cmd != null) cmd.setExecutor(this);

        getLogger().info("BasedInv enabled.");
    }

    @Override
    public void onDisable() {
        getLogger().info("BasedInv disabled.");
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command,
                             @NotNull String label, @NotNull String[] args) {
        if (args.length == 1 && args[0].equalsIgnoreCase("reload")) {
            if (!sender.hasPermission("basedinv.admin")) {
                sender.sendMessage(Component.text("You don't have permission.", NamedTextColor.RED));
                return true;
            }
            configManager.loadAll();
            sender.sendMessage(Component.text("[BasedInv] Configs reloaded!", NamedTextColor.GREEN));
            return true;
        }
        sender.sendMessage(Component.text("[BasedInv] Usage: /bi reload", NamedTextColor.YELLOW));
        return true;
    }

    public GearConfigManager getConfigManager() {
        return configManager;
    }
}