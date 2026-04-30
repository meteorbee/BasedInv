package dev.meteorbee.basedInv.config;

import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.Map;

public class GearConfig {

    private final boolean enabled;
    private final boolean clearFirst;
    private final Map<Integer, ItemStack> items;
    private final List<String> commands;

    public GearConfig(boolean enabled, boolean clearFirst,
                      Map<Integer, ItemStack> items, List<String> commands) {
        this.enabled = enabled;
        this.clearFirst = clearFirst;
        this.items = items;
        this.commands = commands;
    }

    public boolean isEnabled()                   { return enabled; }
    public boolean isClearFirst()                { return clearFirst; }
    public Map<Integer, ItemStack> getItems()    { return items; }
    public List<String> getCommands()            { return commands; }
}