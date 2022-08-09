package rpgstat;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
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

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.*;


public class RPGSTAT extends JavaPlugin implements Listener {

    private playerData playerData;

    //플러그인 활성화
    @Override
    public void onEnable() {
        Bukkit.getConsoleSender().sendMessage(ChatColor.GREEN + "스텟 플러그인이 활성화되었습니다");
        Bukkit.getPluginManager().registerEvents(this, this);
        if(!getDataFolder().exists()){
            getDataFolder().mkdirs();
        }
        saveDefaultConfig();

    }
    //플러그인 비활성화
    @Override
    public void onDisable() {
        Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "스텟 플러그인이 비활성화되었습니다");
        saveConfig();
    }

    //레벨업 했을 때
    @EventHandler
    public void onLevelUp(PlayerExpChangeEvent e) throws IOException {
        Player p = e.getPlayer();
        //플레이어 파일 생성
        File pf = new File(getDataFolder(), "playerdata/" + p.getUniqueId() + ".yml");
        FileConfiguration pFile = YamlConfiguration.loadConfiguration(pf);

        p.sendMessage(String.valueOf(e.getAmount()));
        if(e.getAmount() > 0){
            if(p.getExp() + e.getAmount() / (float)p.getExpToLevel() >= 1.0) {
                UUID pID = p.getUniqueId();

                pFile.set(pID + ".statpoint", pFile.getInt(p.getUniqueId() + "." + "statpoint") + 1);
                pFile.save(pf);
                p.sendMessage(ChatColor.BOLD + "" + ChatColor.DARK_GREEN + "[" + ChatColor.GREEN + "STAT" + ChatColor.DARK_GREEN + "] " + ChatColor.RESET + "" + ChatColor.WHITE + "레벨업! 현재 " + p.getLevel() + "레벨 입니다 !");
                p.sendMessage(ChatColor.BOLD + "" + ChatColor.DARK_GREEN + "[" + ChatColor.GREEN + "STAT" + ChatColor.DARK_GREEN + "] " + ChatColor.RESET + "" + ChatColor.WHITE + "보유 스텟 : " + pFile.get(pID + ".statpoint"));
            }
        }
    }
    //처음 접속했을 때
    @EventHandler
    public void newPlayer(PlayerJoinEvent e){
        Player p = e.getPlayer();
        //플레이어 파일 생성
        File pf = new File(getDataFolder(), "playerdata/" + p.getUniqueId() + ".yml");
        if(!pf.exists()){
            this.playerData = new playerData(this, "playerdata/" + p.getUniqueId() + ".yml");
            playerData.newPlayer(p);
        }


    }
    public ItemStack StatInformation(Player p, String ItemName){
        File pf = new File(getDataFolder(),"playerdata/" + p.getUniqueId() + ".yml");
        FileConfiguration pFile = YamlConfiguration.loadConfiguration(pf);
        String head;

        if(getConfig().contains("stats." + ItemName)){
            head = "stats";
        } else {
            head = "info";
        }
        Material material = Material.matchMaterial(String.valueOf(getConfig().get(head + "." + ItemName + "." + "material")));
        ChatColor chatColor = ChatColor.getByChar(String.valueOf(getConfig().get(head + "." + ItemName + "." + "color")));
        String statName = String.valueOf(getConfig().get(head + "." + ItemName + "." + "name"));
        //LORE 영역
        List<String> lore = new ArrayList<>();
        for(String s : getConfig().getStringList(head + "." + ItemName + "." + "lore" + "." + "itemlore")){
            lore.add(ChatColor.BOLD + "" + ChatColor.translateAlternateColorCodes('&', s));
        }
        if(head == "stats"){
            //현재 레벨
            lore.add(ChatColor.GOLD + "현재 레벨 : " + pFile.get(p.getUniqueId() + "." + ItemName));
            //현재 레벨 설명
            for(String s : getConfig().getStringList(head + "." + ItemName + "." + "lore" + "." + "nowlevellore")){
                lore.add(ChatColor.WHITE + s);
            }
            //다음 레벨
            if((Integer)(pFile.get(p.getUniqueId() + "." + ItemName)) < (Integer)getConfig().get("setting.max")){
                lore.add(ChatColor.DARK_PURPLE + "다음 레벨 : " + ((Integer)(pFile.get(p.getUniqueId() + "." + ItemName)) + 1));
            } else {
                lore.add(ChatColor.DARK_PURPLE + "다음 레벨 : MAX");
            }
            //다음 레벨 설명
            for(String s : getConfig().getStringList(head + "." + ItemName + "." + "lore" + "." + "nextlevellore")){
                if((Integer)(pFile.get(p.getUniqueId() + "." + ItemName)) < (Integer)getConfig().get("setting.max")) {
                    lore.add(ChatColor.GRAY + s);
                } else {
                    lore.add(ChatColor.GRAY + "  마지막 레벨입니다.");
                }
            }
            //공백
            lore.add(" ");
            //버튼 힌트
            for(String s : getConfig().getStringList(head + "." + ItemName + "." + "lore" + "." + "statup")){
                if((Integer)(pFile.get(p.getUniqueId() + "." + ItemName)) < (Integer)getConfig().get("setting.max")) {
                    lore.add(ChatColor.DARK_GRAY + s);
                } else {
                    lore.add(ChatColor.DARK_GRAY + "더 이상 이 스텟 레벨을 올릴 수 없습니다.");
                }
            }
        }
        //아이템 선언
        ItemStack stat;
        //스텟포인트 아이템이라면 갯수 지정
        if(ItemName == "statpoint"){
            int n;
            if((Integer) pFile.get(p.getUniqueId() + ".statpoint") <= 0){
                n = 1;
            } else {
                n = (Integer) pFile.get(p.getUniqueId() + ".statpoint");
            }
            stat = new ItemStack(material, n);
        } else if(String.valueOf(material) == "STAINED_GLASS_PANE"){
            stat = new ItemStack(material, 1, (short) 7);
        } else {
            stat = new ItemStack(material);
        }

        ItemMeta statMeta = stat.getItemMeta();

        if(ItemName == "statpoint"){
            statMeta.setDisplayName(chatColor + "" + ChatColor.BOLD + statName + pFile.get(p.getUniqueId() + ".statpoint"));
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
                        if(args[0].equalsIgnoreCase("admin")){
                            p.sendMessage(logo() + ChatColor.WHITE + "/stat reset [플레이어] " + ChatColor.GREEN + "플레이어의 스텟을 초기화한다. " + ChatColor.GRAY + "(레벨 0)");
                            p.sendMessage(logo() + ChatColor.WHITE + "/stat set [플레이어] [레벨] " + ChatColor.GREEN + "플레이어의 레벨을 임의로 지정합니다. " + ChatColor.GRAY + "(스텟은 초기화)");
                        } else if(args[0].equalsIgnoreCase("reset")){
                            p.sendMessage(logo() + ChatColor.RED + "명령어가 올바르지 않습니다 !");
                            p.sendMessage(logo() + ChatColor.WHITE + "/stat reset [플레이어] " + ChatColor.GREEN + "플레이어의 스텟을 초기화한다. " + ChatColor.GRAY + "(레벨 0)");
                        } else if(args[0].equalsIgnoreCase("set")){
                            p.sendMessage(logo() + ChatColor.RED + "명령어가 올바르지 않습니다 !");
                            p.sendMessage(logo() + ChatColor.WHITE + "/stat set [플레이어] [레벨] " + ChatColor.GREEN + "플레이어의 레벨을 임의로 지정합니다. " + ChatColor.GRAY + "(스텟은 초기화)");
                        }
                    } else if(args.length == 2){
                        if(args[0].equalsIgnoreCase("reset")){
                            //플레이어 오류검사
                            try{
                                //플레이어 선언
                                Player a = Bukkit.getServer().getPlayerExact(args[1]);
                                //플레이어 파일 선언
                                File pf = new File(getDataFolder(), "playerdata/" + a.getUniqueId() + ".yml");
                                FileConfiguration pFile = YamlConfiguration.loadConfiguration(pf);
                                //플레이어 파일 검사
                                if(pf.exists()){
                                    a.setLevel(0);
                                    //스텟 초기화
                                    for(String b : pFile.getConfigurationSection(String.valueOf(a.getUniqueId())).getKeys(false)){
                                        pFile.set(a.getUniqueId() + "." + b, 1);
                                        p.sendMessage(b);
                                        p.sendMessage(String.valueOf(pFile.get(a.getUniqueId() + "." + b)));
                                    }
                                    pFile.set(a.getUniqueId() + ".statpoint", 0);
                                    p.sendMessage(logo() + ChatColor.WHITE + "[" + a.getName() + "] 님의 레벨(스텟)을 초기화시켰습니다 !");
                                    //파일 저장
                                    try {
                                        pFile.save(pf);
                                    } catch (IOException e) {
                                        throw new RuntimeException(e);
                                    }
                                } else {
                                    p.sendMessage(logo() + ChatColor.RED + "대상이 올바르지 않습니다 !");
                                }
                            } catch (NullPointerException e){
                                p.sendMessage(logo() + ChatColor.RED + "대상이 올바르지 않습니다 !");
                                e.printStackTrace();
                                return false;
                            }

                        } else if(args[0].equalsIgnoreCase("set")){
                            p.sendMessage(logo() + ChatColor.RED + "명령어가 올바르지 않습니다 !");
                            p.sendMessage(logo() + ChatColor.WHITE + "/stat set [플레이어] [레벨] " + ChatColor.GREEN + "플레이어의 레벨을 임의로 지정합니다. " + ChatColor.GRAY + "(스텟은 초기화)");
                        }
                    //args.length 가 3개인 경우
                    } else {
                        if(args[0].equalsIgnoreCase("set")){
                            //플레이어 오류검사
                            try{
                                //플레이어 선언
                                Player a = Bukkit.getServer().getPlayerExact(args[1]);
                                //파일
                                File pf = new File(getDataFolder(),"playerdata/" + a.getUniqueId() + ".yml");
                                FileConfiguration pFile = YamlConfiguration.loadConfiguration(pf);
                                if(pf.exists()){
                                    a.setLevel(Integer.parseInt(args[2]));
                                    //스텟 초기화
                                    for(String b : pFile.getConfigurationSection(String.valueOf(a.getUniqueId())).getKeys(false)){
                                        pFile.set(a.getUniqueId() + "." + b, 1);
                                    }
                                    //스텟 포인트 설정
                                    pFile.set(a.getUniqueId() + ".statpoint", Integer.parseInt(args[2]));
                                    try {
                                        pFile.save(pf);
                                    } catch (IOException e) {
                                        throw new RuntimeException(e);
                                    }
                                    p.sendMessage(logo() + ChatColor.WHITE + "[" + a.getName() + "] 님의 스텟이 정상적으로 세팅되었습니다 !");
                                } else {
                                    p.sendMessage(logo() + ChatColor.RED + "대상이 올바르지 않습니다 !");
                                }
                            } catch (NullPointerException e){
                                e.printStackTrace();
                                p.sendMessage(logo() + ChatColor.RED + "대상이 올바르지 않습니다 !");
                                return false;
                            }

                        }
                    }
                }
            }
        return false;
    }
    //스텟 창 내 버튼 기능
    @EventHandler
    public void ivClick(InventoryClickEvent e) throws IOException {
        Player p = (Player) e.getWhoClicked();
        //열려있는(스텟) 인벤토리
        if(!e.getView().getTitle().contains("스텟") || e.getCurrentItem() == null){
            return;
        }
        //클릭한 아이템에 따른 작동
        e.setCancelled(true);
        for(String a : getConfig().getConfigurationSection("stats").getKeys(false)){
            if(e.getCurrentItem().getItemMeta().getDisplayName().contains(String.valueOf(getConfig().get("stats." + a + ".name")))){
                playerData.statUp(p, a);
                p.openInventory(inv(p));
            }
        }
    }
    public String logo(){
        return ChatColor.BOLD + "" + ChatColor.DARK_GREEN + "[" + ChatColor.GREEN + "STAT" + ChatColor.DARK_GREEN + "] " + ChatColor.RESET + "";
    }
}
