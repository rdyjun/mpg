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

public class playerData extends playerFile {
    private playerData playerData;
    private RPGSTAT RPGSTAT;
    public playerData(RPGSTAT main, String playerName){
        super(main, playerName);
        this.RPGSTAT = main;
    }



    public void newPlayer(Player p){
        String pID = p.getUniqueId().toString();
        Bukkit.getConsoleSender().sendMessage("----------------생성-------------------");
        for(String a : RPGSTAT.getConfig().getConfigurationSection("stats").getKeys(false)){
            config.set(pID + "." + a, 1);
        }
        Bukkit.getConsoleSender().sendMessage("----------------스텟포인트-------------------");
        config.set(pID + ".statpoint", 0);
    }
    public void statUp(Player p, String stat){
        String pID = p.getUniqueId().toString();

        //플레이어 파일 생성
        File pf = new File(RPGSTAT.getDataFolder(),"playerdata/" + p.getUniqueId() + ".yml");
        FileConfiguration pFile = YamlConfiguration.loadConfiguration(pf);

        int playerStat = (int)pFile.get(pID + "." + stat);
        p.sendMessage(String.valueOf(playerStat));
        if((Integer)pFile.get("statpoint") > 0){
            pFile.set(stat, playerStat + 1);
            pFile.set("statpoint", (Integer)pFile.get("statpoint") - 1);
            p.sendMessage(ChatColor.BOLD + "" + ChatColor.DARK_GREEN + "[" + ChatColor.GREEN + "STAT" + ChatColor.DARK_GREEN + "] " + ChatColor.getByChar(String.valueOf(RPGSTAT.getConfig().get(stat + "." + "color"))) + RPGSTAT.getConfig().get(stat + ".name") + ChatColor.RESET + "" + ChatColor.WHITE + (playerStat - 1) + " -> " + playerStat + " 스텟 상승!");
        }
    }
}
