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
    public void statUp(Player p, String stat){
        p.sendMessage("aaa");
        if((Integer)playerFile.getPlayerFile(p, "statpoint") > 0){
            if((Integer)playerFile.getPlayerFile(p, stat) < (Integer)RPGSTAT.getConfig().get("setting.max")){
                playerFile.setPlayerFile(p, stat, (Integer)playerFile.getPlayerFile(p, stat) + 1);
                playerFile.setPlayerFile(p, "statpoint", (Integer)playerFile.getPlayerFile(p, "statpoint") - 1);
                p.sendMessage(ChatColor.BOLD + "" + ChatColor.DARK_GREEN + "[" + ChatColor.GREEN + "STAT" + ChatColor.DARK_GREEN + "] " + ChatColor.getByChar(String.valueOf(RPGSTAT.getConfig().get("stats." + stat + "." + "color"))) + RPGSTAT.getConfig().get("stats." + stat + ".name") + ChatColor.RESET + " " + ChatColor.WHITE + ((Integer)playerFile.getPlayerFile(p, stat) - 1) + " -> " + playerFile.getPlayerFile(p, stat) + " 스텟 상승!");
            }
        }
        playerFile.savePlayerFile(p);
    }
}
