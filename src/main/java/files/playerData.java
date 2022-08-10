package files;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;
import rpgstat.RPGSTAT;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.UUID;

public class playerData extends playerFile {
    private playerData playerData;
    private RPGSTAT RPGSTAT;
    public playerData(RPGSTAT main, String playerName){
        super(main, playerName);
        this.RPGSTAT = main;
    }



    public void newPlayer(Player p){
        String pID = p.getUniqueId().toString();
        for(String a : RPGSTAT.getConfig().getConfigurationSection("stats").getKeys(false)){
            config.set(pID + "." + a, 0);
        }
        config.set(pID + ".statpoint", 0);
        save();
    }
    public void statUp(Player p, String stat){
        if((Integer)RPGSTAT.getPlayerFile(p, "statpoint") > 0){
            if((Integer)RPGSTAT.getPlayerFile(p, stat) < (Integer)RPGSTAT.getConfig().get("setting.max")){
                RPGSTAT.setPlayerFile(p, stat, (Integer)RPGSTAT.getPlayerFile(p, stat) + 1);
                RPGSTAT.setPlayerFile(p, "statpoint", (Integer)RPGSTAT.getPlayerFile(p, "statpoint") - 1);
                p.sendMessage(ChatColor.BOLD + "" + ChatColor.DARK_GREEN + "[" + ChatColor.GREEN + "STAT" + ChatColor.DARK_GREEN + "] " + ChatColor.getByChar(String.valueOf(RPGSTAT.getConfig().get("stats." + stat + "." + "color"))) + RPGSTAT.getConfig().get("stats." + stat + ".name") + ChatColor.RESET + " " + ChatColor.WHITE + ((Integer)RPGSTAT.getPlayerFile(p, stat) - 1) + " -> " + RPGSTAT.getPlayerFile(p, stat) + " 스텟 상승!");
            }
        }
        RPGSTAT.savePlayerFile(p);
    }
}
