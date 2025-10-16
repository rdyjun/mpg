package luck;

import files.PlayerFile;
import namegenerator.KeyNameGenerator;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import rpgstat.RpgStat;

public class Luck {

    public static final String TYPE = "luck";
    private static final String STAT_NAME = "luck";
    private static final String CHANCE_STAT_NAME = "chance";

    private final RpgStat rpgStat;

    public Luck(RpgStat rpgStat) {
        this.rpgStat = rpgStat;
    }

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

    public String getNowLevelLore(Player player) {
        Integer statLevel = (Integer) PlayerFile.getPlayerFile(player, STAT_NAME);
        Double statOption = rpgStat.getConfig().getDouble(
                KeyNameGenerator.getKey(STAT_NAME, CHANCE_STAT_NAME));
        return ChatColor.WHITE + "최종 행운 " + ChatColor.GREEN + (statLevel * statOption) + ChatColor.WHITE + " 증가";
    }

    public String getNextLevelLore(Player player) {
        Integer statLevel = ((Integer) PlayerFile.getPlayerFile(player, STAT_NAME) + 1);
        Double statOption = rpgStat.getConfig().getDouble(
                KeyNameGenerator.getKey(STAT_NAME, CHANCE_STAT_NAME));
        return ChatColor.GRAY + "최종 행운이 " + ChatColor.DARK_GREEN + (statLevel * statOption) + ChatColor.WHITE
                + " 만큼 증가합니다.";
    }
}
