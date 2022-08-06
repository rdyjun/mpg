package rpgstat;

import files.playerFile;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerExpChangeEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;

import files.playerData;
import java.io.FileNotFoundException;
import java.util.*;


public class RPGSTAT extends JavaPlugin implements Listener {

    private playerData playerData;

    //플러그인 활성화
    @Override
    public void onEnable() {
        Bukkit.getConsoleSender().sendMessage(ChatColor.GREEN + "스텟 플러그인이 활성화되었습니다");
        Bukkit.getPluginManager().registerEvents(this, this);
        saveDefaultConfig();
        if(!getDataFolder().exists()){
            getDataFolder().mkdirs();
        }
        this.playerData = new playerData(this);
    }
    //플러그인 비활성화
    @Override
    public void onDisable() {
        Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "스텟 플러그인이 비활성화되었습니다");
        playerData.save();
    }
    //레벨업 했을 때
    @EventHandler
    public void onLevelUp(PlayerExpChangeEvent e) throws FileNotFoundException {
        Player p = (Player) e.getPlayer();
        playerData.getUserDataConfig().set(p.getUniqueId() + "." + "statpoint", (Integer)playerData.getUserDataConfig().get(p.getUniqueId() + "." + "statpoint") + 1);
        playerData.save();
        p.sendMessage(ChatColor.BOLD + "" + ChatColor.DARK_GREEN + "[" + ChatColor.GREEN + "STAT" + ChatColor.DARK_GREEN + "]" + ChatColor.RESET + "" + ChatColor.WHITE + "레벨업! 현재 " + p.getLevel() + "레벨 입니다 !");
    }
    //처음 접속했을 때
    @EventHandler
    public void newPlayer(PlayerJoinEvent e){
        Player p = (Player) e.getPlayer();
        playerData.newPlayer(e.getPlayer());
        playerData.save();
    }
    public ItemStack StatInformation(Player p, String ItemName){
        Material material = Material.matchMaterial(String.valueOf(getConfig().get(ItemName + "." + "material")));
        ChatColor chatColor = ChatColor.getByChar(String.valueOf(getConfig().get(ItemName + "." + "color")));
        String statName = String.valueOf(getConfig().get(ItemName + "." + "name"));
        //LORE 영역
        List<String> lore = new ArrayList<>();
        for(String s : getConfig().getStringList(ItemName + "." + "lore" + "." + "itemlore")){
            lore.add(ChatColor.BOLD + "" + ChatColor.translateAlternateColorCodes('&', s));
        }
        for(String s : getConfig().getStringList(ItemName + "." + "lore" + "." + "nowlevel")){
            lore.add(ChatColor.GOLD + s + playerData.getUserDataConfig().get(p.getUniqueId() + "." + ItemName));
        }
        for(String s : getConfig().getStringList(ItemName + "." + "lore" + "." + "nowlevellore")){
            lore.add(ChatColor.WHITE + s);
        }

        for(String s : getConfig().getStringList(ItemName + "." + "lore" + "." + "nextlevel")){
            lore.add(ChatColor.DARK_PURPLE + s);
        }
        for(String s : getConfig().getStringList(ItemName + "." + "lore" + "." + "nextlevellore")){
            lore.add(ChatColor.GRAY + s);
        }
        lore.add(" ");
        for(String s : getConfig().getStringList(ItemName + "." + "lore" + "." + "statup")){
            lore.add(ChatColor.DARK_GRAY + s);
        }

        ItemStack stat;
        if(ItemName == "statpoint"){
            int n;
            if((Integer) playerData.getUserDataConfig().get(p.getUniqueId() + "." + "statpoint") <= 0){
                n = 1;
            } else {
                n = (Integer) playerData.getUserDataConfig().get(p.getUniqueId() + "." + "statpoint");
            }
            stat = new ItemStack(material, n);
        } else if(String.valueOf(material) == "STAINED_GLASS_PANE"){
            stat = new ItemStack(material, 1, (short) 7);
        } else {
            stat = new ItemStack(material);
        }
        ItemMeta statMeta = stat.getItemMeta();
        if(ItemName == "statpoint"){
            statMeta.setDisplayName(chatColor + "" + ChatColor.BOLD + statName + playerData.getUserDataConfig().get(p.getUniqueId() + "." + "statpoint"));
        } else {
            statMeta.setDisplayName(chatColor + "" + ChatColor.BOLD + statName);
        }
        statMeta.setLore(lore);
        statMeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        stat.setItemMeta(statMeta);

        return stat;
    }
    public Inventory inv(Player p){
        //스텟 메뉴 틀
        Inventory inv = Bukkit.createInventory(null, 9, "스텟");
        inv.setItem(0, StatInformation(p, "statpoint"));
        inv.setItem(1, StatInformation(p, "statinfo1"));
        inv.setItem(2, StatInformation(p, "attack"));
        inv.setItem(3, StatInformation(p, "patience"));
        inv.setItem(4, StatInformation(p, "agility"));
        inv.setItem(5, StatInformation(p, "luck"));
        inv.setItem(6, StatInformation(p, "proficiency"));
        inv.setItem(7, StatInformation(p, "statinfo2"));
        inv.setItem(8, StatInformation(p, "reset"));
        p.openInventory(inv);
        return inv;
    }
    //스텟 메뉴 세부
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args){
        Player p = (Player) sender;
            if(cmd.getName().equalsIgnoreCase("stat")){
                if(args.length == 0){
                    this.inv(p);
                } else if(p.isOp()){
                    if(args.length == 1){
                        if(args[0] == "admin"){
                            p.sendMessage(ChatColor.BOLD + "" + ChatColor.DARK_GREEN + "[" + ChatColor.GREEN + "STAT" + ChatColor.DARK_GREEN + "]" + ChatColor.RESET + "" + ChatColor.WHITE + "/stat reset [플레이어]" + ChatColor.GREEN + "플레이어의 스텟을 초기화한다. " + ChatColor.GRAY + "(레벨 0)");
                            p.sendMessage(ChatColor.BOLD + "" + ChatColor.DARK_GREEN + "[" + ChatColor.GREEN + "STAT" + ChatColor.DARK_GREEN + "]" + ChatColor.RESET + "" + ChatColor.WHITE + "/stat set [플레이어] [레벨]" + ChatColor.GREEN + "플레이어의 레벨을 임의로 지정합니다. " + ChatColor.GRAY + "(스텟은 초기화)");
                        } else if(args[0] == "reset"){
                            p.sendMessage(ChatColor.BOLD + "" + ChatColor.DARK_GREEN + "[" + ChatColor.GREEN + "STAT" + ChatColor.DARK_GREEN + "]" + ChatColor.RESET + "" + ChatColor.RED + "명령어가 올바르지 않습니다 !");
                            p.sendMessage(ChatColor.BOLD + "" + ChatColor.DARK_GREEN + "[" + ChatColor.GREEN + "STAT" + ChatColor.DARK_GREEN + "]" + ChatColor.RESET + "" + ChatColor.WHITE + "/stat reset [플레이어]" + ChatColor.GREEN + "플레이어의 스텟을 초기화한다. " + ChatColor.GRAY + "(레벨 0)");
                        } else if(args[0] == "set"){
                            p.sendMessage(ChatColor.BOLD + "" + ChatColor.DARK_GREEN + "[" + ChatColor.GREEN + "STAT" + ChatColor.DARK_GREEN + "]" + ChatColor.RESET + "" + ChatColor.RED + "명령어가 올바르지 않습니다 !");
                            p.sendMessage(ChatColor.BOLD + "" + ChatColor.DARK_GREEN + "[" + ChatColor.GREEN + "STAT" + ChatColor.DARK_GREEN + "]" + ChatColor.RESET + "" + ChatColor.WHITE + "/stat set [플레이어] [레벨]" + ChatColor.GREEN + "플레이어의 레벨을 임의로 지정합니다. " + ChatColor.GRAY + "(스텟은 초기화)");
                        }
                    } else if(args.length == 2){
                        if(args[0] == "reset"){
                            if(getConfig().contains(args[1])){
                                Player a = Bukkit.getServer().getPlayer(args[1]);
                                a.setExp(0);
                                getConfig().set(a.getUniqueId() + ".statpoint", 0);
                            } else {
                                p.sendMessage(ChatColor.BLUE + "[STAT]" + ChatColor.RED + "대상이 올바르지 않습니다 !");
                            }
                        } else if(args[0] == "set"){
                            p.sendMessage(ChatColor.BLUE + "[STAT]" + ChatColor.RED + "명령어가 올바르지 않습니다 !");
                            p.sendMessage(ChatColor.BLUE + "[STAT]" + ChatColor.WHITE + "/stat set [플레이어] [레벨]" + ChatColor.GREEN + "플레이어의 레벨을 임의로 지정합니다. " + ChatColor.GRAY + "(스텟은 초기화)");
                        }
                    } else {
                        if(args[0] == "set"){
                            if(getConfig().contains(args[1])){
                                Player a = Bukkit.getServer().getPlayer(args[1]);
                                a.setLevel(Integer.parseInt(args[3]));
                                getConfig().set(a.getUniqueId() + ".statpoint", args[3]);
                            } else {
                                p.sendMessage(ChatColor.BLUE + "[STAT]" + ChatColor.RED + "대상이 올바르지 않습니다 !");
                            }
                        }
                    }
                }
            }
        return false;
    }
    //스텟 창 내 버튼 기능
    @EventHandler
    public void ivClick(InventoryClickEvent e) {
        Player p = (Player) e.getWhoClicked();
        //열려있는(스텟) 인벤토리
        if(!e.getView().getTitle().contains("스텟")){
            return;
        }
        //클릭한 아이템에 따른 작동
        e.setCancelled(true);

        if(e.getCurrentItem().getType() == StatInformation(p, "attack").getType()){
            playerData.statUp(p, "attack", this);
            p.openInventory(inv(p));
        } else
        if(e.getCurrentItem().getType() == StatInformation(p, "patience").getType()){
            playerData.statUp(p, "patience", this);
            p.openInventory(inv(p));
        } else
        if(e.getCurrentItem().getType() == StatInformation(p, "agility").getType()){
            playerData.statUp(p, "agility", this);
            p.openInventory(inv(p));
        } else
        if(e.getCurrentItem().getType() == StatInformation(p, "luck").getType()){
            playerData.statUp(p, "luck",this);
        } else
        if(e.getCurrentItem().getType() == StatInformation(p, "proficiency").getType()){
            playerData.statUp(p, "proficiency", this);
        }
        if(e.getCurrentItem().getType() == StatInformation(p, "statreset").getType()){

        }
        playerData.save();
        p.performCommand("stat");
    }

    public FileConfiguration getItemDataConfig (){
        return getConfig();
    }
}