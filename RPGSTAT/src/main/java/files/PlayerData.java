package files;

import rpgstat.RPGSTAT;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class playerData {
    private RPGSTAT RPGSTAT;
    private playerFile playerFile;

    public playerData(RPGSTAT RPGSTAT){
        this.RPGSTAT = RPGSTAT;
        this.playerFile = new playerFile(RPGSTAT);
    }

    synchronized public void statUp(Player p, String statName){
        int statPoint = (Integer) playerFile.getPlayerFile(p, "statpoint");

        if (statPoint <= 0) {
            p.sendMessage(RPGSTAT.messageHead() + ChatColor.RED + "스텟 포인트가 부족합니다 !");
            return;
        }

        int statLevel = (Integer) playerFile.getPlayerFile(p, statName);
        int maxStatLevel = (Integer) RPGSTAT.getConfig().get("setting.max");

        if (statLevel > maxStatLevel) {
            p.sendMessage(RPGSTAT.messageHead() + ChatColor.RED + "최대 스텟 포인트를 초과했습니다 !");
            return;
        }

        playerFile.setPlayerFile(p, statName, statLevel + 1);
        playerFile.setPlayerFile(p, "statpoint", statPoint - 1);
        playerFile.savePlayerFile(p);
        p.sendMessage(ChatColor.BOLD + "" + ChatColor.DARK_GREEN + "[" + ChatColor.GREEN + "STAT" + ChatColor.DARK_GREEN + "] " + ChatColor.getByChar(String.valueOf(RPGSTAT.getConfig().get("stats." + statName + "." + "color"))) + RPGSTAT.getConfig().get("stats." + statName + ".name") + ChatColor.RESET + " " + ChatColor.WHITE + ((Integer) playerFile.getPlayerFile(p, stat) - 1) + " -> " + playerFile.getPlayerFile(p, statName) + " 스텟 상승!");
    }

}
