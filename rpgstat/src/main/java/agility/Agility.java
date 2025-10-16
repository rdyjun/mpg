package agility;

import files.PlayerFile;
import namegenerator.KeyNameGenerator;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import rpgstat.Stat;

public class Agility extends Stat {

    public static final String TYPE = "agility";
    private static final Float DEFAULT_SPEED = 0.2f;
    private static final String STAT_NAME = "agility";
    private static final String SPEED_STAT_NAME = "speed";

    public Agility(rpgstat.RpgStat rpgStat) {
        super(rpgStat);
    }

    public void increase(Player player) {
        String agilityKeyName = KeyNameGenerator.getKey(STAT_NAME, SPEED_STAT_NAME);

        float agilityStatOption = (float) rpgStat.getConfig().getDouble(agilityKeyName);
        int playerAgilityLevel = (Integer) PlayerFile.getPlayerFile(player, STAT_NAME);

        // 최대 1.0f 까지 속도 증가
        float updatedSpeed = Math.min(1f, DEFAULT_SPEED + (playerAgilityLevel * agilityStatOption));

        player.setWalkSpeed(updatedSpeed);
        player.setFlySpeed(updatedSpeed);
    }

    public void init(Player player) {
        increase(player);
    }

    public String getNowLevelLore(Player player) {
        Integer statLevel = (Integer) PlayerFile.getPlayerFile(player, STAT_NAME);
        Double statOption = rpgStat.getConfig().getDouble(
                KeyNameGenerator.getKey(STAT_NAME, SPEED_STAT_NAME));

        double lastSpeed = Math.floor(statLevel * statOption * 1000.0) / 1000.0;

        return ChatColor.WHITE + "최종 속도 " + ChatColor.GREEN + lastSpeed + ChatColor.WHITE + " 증가";
    }

    public String getNextLevelLore(Player player) {
        Integer statLevel = ((Integer) PlayerFile.getPlayerFile(player, STAT_NAME) + 1);
        Double statOption = rpgStat.getConfig().getDouble(
                KeyNameGenerator.getKey(STAT_NAME, SPEED_STAT_NAME));

        double lastSpeed = Math.floor(statLevel * statOption * 1000.0) / 1000.0;

        return ChatColor.GRAY + "최종 속도가 " + ChatColor.DARK_GREEN + lastSpeed + ChatColor.WHITE
                + " 만큼 증가합니다.";
    }
}
