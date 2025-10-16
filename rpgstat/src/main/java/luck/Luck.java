package luck;

import org.bukkit.Material;

public class Luck {

    public boolean isAppliedMaterial(Material material) {
        return material == Material.COAL_ORE ||
                material == Material.IRON_ORE ||
                material == Material.DIAMOND_ORE ||
                material == Material.GOLD_ORE ||
                material == Material.EMERALD_ORE ||
                material == Material.NETHER_QUARTZ_ORE ||
                material == Material.NETHER_GOLD_ORE ||
                material == Material.ANCIENT_DEBRIS ||
                material == Material.LAPIS_ORE ||
                material == Material.REDSTONE_ORE ||
                material == Material.COPPER_ORE ||
                material == Material.DEEPSLATE_COAL_ORE ||
                material == Material.DEEPSLATE_IRON_ORE ||
                material == Material.DEEPSLATE_DIAMOND_ORE ||
                material == Material.DEEPSLATE_GOLD_ORE ||
                material == Material.DEEPSLATE_EMERALD_ORE ||
                material == Material.DEEPSLATE_LAPIS_ORE ||
                material == Material.DEEPSLATE_REDSTONE_ORE ||
                material == Material.DEEPSLATE_COPPER_ORE;
    }
}
