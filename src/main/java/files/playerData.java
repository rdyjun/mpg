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
            config.set(pID + "." + a, 1);
        }
        config.set(pID + ".statpoint", 0);
        save();
    }
    public void statUp(Player p, String stat) throws IOException {
        UUID pID = p.getUniqueId();

        File pf = new File(RPGSTAT.getDataFolder(),"playerdata/" + p.getUniqueId() + ".yml");
        FileConfiguration pFile = YamlConfiguration.loadConfiguration(pf);
        if(pFile.getInt(pID + ".statpoint") > 0){
            if(pFile.getInt(pID + "." + stat) < 100){
                pFile.set(pID + "." + stat, pFile.getInt(pID + "." + stat) + 1);
                pFile.set(pID + "." + "statpoint", pFile.getInt(pID + ".statpoint") - 1);
                p.sendMessage(ChatColor.BOLD + "" + ChatColor.DARK_GREEN + "[" + ChatColor.GREEN + "STAT" + ChatColor.DARK_GREEN + "] " + ChatColor.getByChar(String.valueOf(RPGSTAT.getConfig().get("stats." + stat + "." + "color"))) + RPGSTAT.getConfig().get("stats." + stat + ".name") + ChatColor.RESET + " " + ChatColor.WHITE + (pFile.getInt(pID + "." + stat) - 1) + " -> " + pFile.getInt(pID + "." + stat) + " 스텟 상승!");
            }
        }
        pFile.save(pf);
    }
}
