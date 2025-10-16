package luck;

import org.bukkit.Material;
import org.bukkit.block.Block;

public class Luck {

    public boolean isAppliedBlock(Block block) {
            return block.getType() == Material.COAL_ORE ||
                    block.getType() == Material.IRON_ORE ||
                    block.getType() == Material.DIAMOND_ORE ||
                    block.getType() == Material.GOLD_ORE ||
                    block.getType() == Material.EMERALD_ORE ||
                    block.getType() == Material.NETHER_QUARTZ_ORE ||
                    block.getType() == Material.NETHER_GOLD_ORE ||
                    block.getType() == Material.ANCIENT_DEBRIS ||
                    block.getType() == Material.LAPIS_ORE ||
                    block.getType() == Material.REDSTONE_ORE ||
                    block.getType() == Material.COPPER_ORE ||
                    block.getType() == Material.DEEPSLATE_COAL_ORE ||
                    block.getType() == Material.DEEPSLATE_IRON_ORE ||
                    block.getType() == Material.DEEPSLATE_DIAMOND_ORE ||
                    block.getType() == Material.DEEPSLATE_GOLD_ORE ||
                    block.getType() == Material.DEEPSLATE_EMERALD_ORE ||
                    block.getType() == Material.DEEPSLATE_LAPIS_ORE ||
                    block.getType() == Material.DEEPSLATE_REDSTONE_ORE ||
                    block.getType() == Material.DEEPSLATE_COPPER_ORE;
    }
}
