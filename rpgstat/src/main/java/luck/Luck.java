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
        String materialName = material.name();
        return materialName.endsWith("_ORE") || materialName.equals("ANCIENT_DEBRIS");
    }

    public String getNowLevelLore(Player player) {
        Integer statLevel = (Integer) PlayerFile.getPlayerFile(player, STAT_NAME);
        Double statOption = rpgStat.getConfig().getDouble(
                KeyNameGenerator.getKey(STAT_NAME, CHANCE_STAT_NAME));

        double lastChance = Math.floor(statLevel * statOption * 1000.0) / 1000.0;

        return ChatColor.WHITE + "최종 행운 " + ChatColor.GREEN + ChatColor.BOLD + lastChance + "%" + ChatColor.WHITE
                + " 증가";
    }

    public String getNextLevelLore(Player player) {
        Integer statLevel = ((Integer) PlayerFile.getPlayerFile(player, STAT_NAME) + 1);
        Double statOption = rpgStat.getConfig().getDouble(
                KeyNameGenerator.getKey(STAT_NAME, CHANCE_STAT_NAME));

        double lastChance = Math.floor(statLevel * statOption * 1000.0) / 1000.0;

        return ChatColor.GRAY + "최종 행운이 " + ChatColor.DARK_GREEN + ChatColor.BOLD + lastChance + "%" + ChatColor.WHITE
                + " 만큼 증가합니다.";
    }
}
