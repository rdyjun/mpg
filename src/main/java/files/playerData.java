package files;

import org.bukkit.plugin.Plugin;
import rpgstat.RPGSTAT;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

public class playerData extends playerFile {

    String attack = "attack";
    String patience = "patience";
    String luck = "luck";
    String proficiency = "proficiency";
    String agility = "agility";
    String statpoint = "statpoint";


    public playerData(RPGSTAT main){
        super(main, "Playerdata.yml");
    }

    private playerData playerData;

    public void newPlayer(Player p){
        String ID = p.getUniqueId().toString();
        if(!config.contains(ID)) {
            for(Object s : config.getKeys(false)){
                if(config.get(s + ".type") == "stat"){
                    config.set(ID + "." + String.valueOf(s), 1);
                }
            }
            config.set(ID + "." + this.attack, 1);
            config.set(ID + "." + this.patience, 1);
            config.set(ID + "." + this.luck, 1);
            config.set(ID + "." + this.proficiency, 1);
            config.set(ID + "." + this.agility, 1);
            config.set(ID + "." + this.statpoint, 0);
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