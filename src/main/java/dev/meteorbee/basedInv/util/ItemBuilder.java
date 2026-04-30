package dev.meteorbee.basedInv.util;

import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.Registry;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public final class ItemBuilder {

    private static final LegacyComponentSerializer LEGACY =
            LegacyComponentSerializer.legacyAmpersand();

    private ItemBuilder() {}

    public static ItemStack build(
            String materialName,
            int amount,
            String displayName,
            List<String> lore,
            Map<String, Integer> enchantments,
            boolean unbreakable,
            String colorRgb
    ) {
        Material material = Material.matchMaterial(materialName.toUpperCase());
        if (material == null) throw new IllegalArgumentException("Unknown material: " + materialName);

        ItemStack item = new ItemStack(material, Math.max(1, amount));
        ItemMeta meta = item.getItemMeta();
        if (meta == null) return item;

        if (displayName != null && !displayName.isBlank())
            meta.displayName(LEGACY.deserialize(displayName));

        if (lore != null && !lore.isEmpty())
            meta.lore(lore.stream().map(LEGACY::deserialize).collect(Collectors.toList()));

        if (unbreakable)
            meta.setUnbreakable(true);

        if (colorRgb != null && !colorRgb.isBlank() && meta instanceof LeatherArmorMeta leatherMeta) {
            String[] parts = colorRgb.split(",");
            if (parts.length == 3) {
                int r = Integer.parseInt(parts[0].trim());
                int g = Integer.parseInt(parts[1].trim());
                int b = Integer.parseInt(parts[2].trim());
                leatherMeta.setColor(Color.fromRGB(r, g, b));
            }
        }

        item.setItemMeta(meta);

        for (Map.Entry<String, Integer> entry : enchantments.entrySet()) {
            Enchantment enchant = resolveEnchantment(entry.getKey());
            if (enchant == null) throw new IllegalArgumentException("Unknown enchantment: " + entry.getKey());
            item.addUnsafeEnchantment(enchant, entry.getValue());
        }

        return item;
    }

    private static Enchantment resolveEnchantment(String name) {
        for (Enchantment e : Registry.ENCHANTMENT) {
            if (e.getKey().getKey().equalsIgnoreCase(name) ||
                    e.getKey().getKey().toUpperCase().replace(".", "_").equals(name.toUpperCase()))
                return e;
        }
        return null;
    }
}