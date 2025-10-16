package vitality;

import files.PlayerFile;
import namegenerator.KeyNameGenerator;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import rpgstat.Stat;

public class Vitality extends Stat {

    public static final String TYPE = "vitality";
    private static final Double BASE_HEALTH = 20d;
    private static final String STAT_NAME = "vitality";
    private static final String HEALTH_STAT_NAME = "health";

    public Vitality(rpgstat.RpgStat rpgStat) {
        super(rpgStat);
    }

    public void increase(Player player) {
        String healthKeyName = KeyNameGenerator.getKey(STAT_NAME, HEALTH_STAT_NAME);

        double healthStatOption = rpgStat.getConfig().getDouble(healthKeyName);
        Integer playerHealthLevel = (Integer) PlayerFile.getPlayerFile(player, STAT_NAME);

        double updatedHealth = BASE_HEALTH + (healthStatOption * playerHealthLevel);

        player.setHealthScale(updatedHealth);
    }

    public void init(Player player) {
        increase(player);
    }

    public String getNowLevelLore(Player player, String statName) {
        Integer statLevel = (Integer) PlayerFile.getPlayerFile(player, statName);
        Double statOption = rpgStat.getConfig().getDouble(
                KeyNameGenerator.getKey(statName, HEALTH_STAT_NAME));
        return ChatColor.WHITE + "최종 체력 " + ChatColor.GREEN + (statLevel * statOption) + ChatColor.WHITE + " 증가";
    }

    public String getNextLevelLore(Player player, String statName) {
        Integer statLevel = ((Integer) PlayerFile.getPlayerFile(player, statName) + 1);
        Double statOption = rpgStat.getConfig().getDouble(
                KeyNameGenerator.getKey(statName, HEALTH_STAT_NAME));
        return ChatColor.GRAY + "최종 체력이 " + ChatColor.DARK_GREEN + (statLevel * statOption) + ChatColor.WHITE + " 만큼 증가합니다.";
    }
}
