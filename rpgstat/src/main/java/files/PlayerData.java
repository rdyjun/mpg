package files;

import java.io.File;
import java.io.IOException;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import rpgstat.RpgStat;

public class PlayerData {

    private final static String STAT_POINT_NAME = "statpoint";

    private final RpgStat rpgStat;

    public PlayerData(RpgStat rpgStat) {
        this.rpgStat = rpgStat;
    }

    /**
     * 스텟 상승
     *
     * @param player
     * @param statName
     */
    synchronized public boolean statUp(Player player, String statName) {
        int statPoint = (Integer) PlayerFile.getPlayerFile(player, STAT_POINT_NAME);

        // 스텟 포인트가 없을 때 종료
        if (statPoint <= 0) {
            player.sendMessage(rpgStat.messageHead() + ChatColor.RED + "스텟 포인트가 부족합니다 !");
            return false;
        }

        int statLevel = (Integer) PlayerFile.getPlayerFile(player, statName);
        int maxLevel = rpgStat.getConfig().getInt("stats." + statName + ".max-level");
        if (statLevel >= maxLevel) {
            player.sendMessage(rpgStat.messageHead() + ChatColor.RED + "최대 레벨에 도달했습니다 !");
            return false;
        }

        File file = PlayerFile.getFile(player);
        FileConfiguration config = PlayerFile.getConfig(player);
        config.set(player.getUniqueId() + "." + statName, statLevel + 1);
        config.set(player.getUniqueId() + "." + STAT_POINT_NAME, statPoint - 1);
        try {
            config.save(file);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        PlayerFile.savePlayerFile(player);

        ChatColor statColor = getStatColor(statName);
        String statDisplayName = getDisplayName(statName);
        int stat = (Integer) PlayerFile.getPlayerFile(player, statName);

        String message =
                ChatColor.BOLD + "" + ChatColor.DARK_GREEN + "[" + ChatColor.GREEN + "STAT" + ChatColor.DARK_GREEN
                        + "] " + statColor + statDisplayName + ChatColor.RESET + " " + ChatColor.WHITE + stat + " -> "
                        + (stat + 1) + " 스텟 상승!";

        player.sendMessage(message);

        return true;
    }

    public ChatColor getStatColor(String statName) {
        String statColorString = String.valueOf(rpgStat.getConfig().get("stats." + statName + "." + "color"));
        return ChatColor.getByChar(statColorString);
    }

    public String getDisplayName(String statName) {
        return String.valueOf(rpgStat.getConfig().get("stats." + statName + ".name"));
    }

}
