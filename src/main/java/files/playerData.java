package files;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import rpgstat.RPGSTAT;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.util.Iterator;

public class playerData extends playerFile {

    String attack = "attack";
    String patience = "patience";
    String luck = "luck";
    String proficiency = "proficiency";
    String agility = "agility";
    String statpoint = "statpoint";


    public playerData(RPGSTAT main, String playerName){
        super(main, playerName);
    }

    private playerData playerData;
    private RPGSTAT RPGSTAT;

    public void newPlayer(Player p, Plugin plugin){
        String pID = p.getUniqueId().toString();
        if(plugin.getConfig().contains("statpoint")){
            Bukkit.getConsoleSender().sendMessage("-----------------------------------------------");
            Bukkit.getConsoleSender().sendMessage("file get null");
            Bukkit.getConsoleSender().sendMessage("-----------------------------------------------");
            newPlayer(p, plugin);
        } else {
            Bukkit.getConsoleSender().sendMessage("-----------------------------------------------");
            Bukkit.getConsoleSender().sendMessage(String.valueOf(plugin.getConfig().getConfigurationSection("stats").getKeys(false)));
            Bukkit.getConsoleSender().sendMessage("-----------------------------------------------");
        }

    }
    public void statUp(Player p, String stat, Plugin plugin){
        String pID = p.getUniqueId().toString();
        int playerStat = (int)config.get(pID + "." + stat);

        if((Integer)config.get(pID + "." + "statpoint") > 0){
            config.set(pID + "." + stat, playerStat + 1);
            config.set(pID + "." + "statpoint", (Integer)config.get(pID + "." + "statpoint") - 1);
            p.sendMessage(ChatColor.BOLD + "" + ChatColor.DARK_GREEN + "[" + ChatColor.GREEN + "STAT" + ChatColor.DARK_GREEN + "] " + ChatColor.getByChar(String.valueOf(plugin.getConfig().get(stat + "." + "color"))) + plugin.getConfig().get(stat + ".name") + ChatColor.RESET + "" + ChatColor.WHITE + (playerStat - 1) + " -> " + playerStat + " 스텟 상승!");
        }
    }
    public FileConfiguration getUserDataConfig (){
        return this.config;
    }
}