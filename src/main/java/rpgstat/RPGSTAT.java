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
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerExpChangeEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffectType;

import files.playerData;
import files.playerFile;

import java.io.File;
import java.io.IOException;
import java.util.*;


public class RPGSTAT extends JavaPlugin implements Listener {
    private playerData playerData;
    private playerFile playerFile;

    //플러그인 활성화
    @Override
    public void onEnable() {
        Bukkit.getConsoleSender().sendMessage(ChatColor.GREEN + "스텟 플러그인이 활성화되었습니다");
        Bukkit.getPluginManager().registerEvents(this, this);
        if(!getDataFolder().exists()){
            getDataFolder().mkdirs();
        }
        this.playerData = new playerData(this);
        this.playerFile = new playerFile(this);
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
        Bukkit.getConsoleSender().sendMessage("------------------A---------------------");
        playerFile.createFile(p);
        Bukkit.getConsoleSender().sendMessage("------------------C---------------------");
        //플레이어 체력 설정
        playerPatience(p);
    }
    public ItemStack StatInformation(Player p, String ItemName){
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
        //기본 아이템 설명
        for(String s : getConfig().getStringList(head + "." + ItemName + "." + "lore" + "." + "itemlore")){
            lore.add(ChatColor.BOLD + "" + ChatColor.translateAlternateColorCodes('&', s));
        }
        if(head == "stats"){
            //현재 레벨
            lore.add(ChatColor.GOLD + "현재 레벨 : " + playerFile.getPlayerFile(p, ItemName));
            //현재 레벨 설명
            int i = 0;
            for(String s : getConfig().getConfigurationSection(head + "." + ItemName + ".stat").getKeys(false)){
                lore.add(ChatColor.WHITE + "  " + s + " : +" + (Integer)playerFile.getPlayerFile(p, ItemName) * Double.valueOf(String.valueOf(getConfig().get(head + "." + ItemName + "." + "stat" + "." + s))).doubleValue());
                i++;
            }
            //다음 레벨
            if((Integer)(playerFile.getPlayerFile(p, ItemName)) < (Integer)getConfig().get("setting.max")){
                lore.add(ChatColor.DARK_PURPLE + "다음 레벨 : " + ((Integer)(playerFile.getPlayerFile(p, ItemName)) + 1));
            } else {
                lore.add(ChatColor.DARK_PURPLE + "다음 레벨 : MAX");
            }
            //다음 레벨 설명
            for(String s : getConfig().getStringList(head + "." + ItemName + "." + "lore" + "." + "nextlevellore")){
                if((Integer)(playerFile.getPlayerFile(p, ItemName)) < (Integer)getConfig().get("setting.max")) {
                    lore.add(ChatColor.GRAY + s);
                } else {
                    lore.add(ChatColor.GRAY + "  마지막 레벨입니다.");
                }
            }
            //공백
            lore.add(" ");
            //버튼 힌트
            for(String s : getConfig().getStringList(head + "." + ItemName + "." + "lore" + "." + "statup")){
                if((Integer)(playerFile.getPlayerFile(p, ItemName)) < (Integer)getConfig().get("setting.max")) {
                    lore.add(ChatColor.DARK_GRAY + s);
                } else {
                    lore.add(ChatColor.DARK_GRAY + "이 스텟은 더 이상 레벨을 올릴 수 없습니다.");
                }
            }
        }
        //아이템 선언
        ItemStack stat;
        //아이템 수량 지정
        if(ItemName.equalsIgnoreCase("statpoint")){
            int n;
            if((Integer) playerFile.getPlayerFile(p, "statpoint") <= 0){
                n = 1;
            } else {
                n = (Integer) playerFile.getPlayerFile(p, "statpoint");
            }
            stat = new ItemStack(material, n);
        } else if(material == Material.getMaterial("STAINED_GLASS_PANE")){
            stat = new ItemStack(material, 1, (short) 7);
        } else {
            stat = new ItemStack(material);
        }

        ItemMeta statMeta = stat.getItemMeta();
        //아이템 이름
        if(ItemName.equalsIgnoreCase("statpoint")){
            statMeta.setDisplayName(chatColor + "" + ChatColor.BOLD + statName + " : " + ChatColor.YELLOW + playerFile.getPlayerFile(p, "statpoint"));
        } else {
            statMeta.setDisplayName(chatColor + "" + ChatColor.BOLD + statName);
        }
        statMeta.setLore(lore);
        statMeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        stat.setItemMeta(statMeta);

        return stat;
    }
    //스텟창 설정 ----------------------------
    public Inventory inv(Player p){
        //스텟 메뉴 틀
        Inventory inv = Bukkit.createInventory(null, 9, "스텟");
        inv.setMaxStackSize(100);
        for(String s : getConfig().getConfigurationSection("info").getKeys(false)){
            inv.setItem(getConfig().getInt("info." + s + ".position"), StatInformation(p, s));
        }
        for(String s : getConfig().getConfigurationSection("stats").getKeys(false)){
            inv.setItem(getConfig().getInt("stats." + s + ".position"), StatInformation(p, s));
        }
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
                            p.sendMessage(messageHead() + ChatColor.WHITE + "/stat reset [플레이어] " + ChatColor.GREEN + "플레이어의 스텟을 초기화한다. " + ChatColor.GRAY + "(레벨 0)");
                            p.sendMessage(messageHead() + ChatColor.WHITE + "/stat set [플레이어] [레벨] " + ChatColor.GREEN + "플레이어의 레벨을 임의로 지정합니다. " + ChatColor.GRAY + "(스텟은 초기화)");
                        } else if(args[0].equalsIgnoreCase("reset")){
                            p.sendMessage(messageHead() + ChatColor.RED + "명령어가 올바르지 않습니다 !");
                            p.sendMessage(messageHead() + ChatColor.WHITE + "/stat reset [플레이어] " + ChatColor.GREEN + "플레이어의 스텟을 초기화한다. " + ChatColor.GRAY + "(레벨 0)");
                        } else if(args[0].equalsIgnoreCase("set")){
                            p.sendMessage(messageHead() + ChatColor.RED + "명령어가 올바르지 않습니다 !");
                            p.sendMessage(messageHead() + ChatColor.WHITE + "/stat set [플레이어] [레벨] " + ChatColor.GREEN + "플레이어의 레벨을 임의로 지정합니다. " + ChatColor.GRAY + "(스텟은 초기화)");
                        }
                    } else if(args.length == 2){  //명령어 문자를 3개만 받은 경우-----------------------
                        if(args[0].equalsIgnoreCase("reset")){
                            try{  //플레이어 오류검사
                                //플레이어 선언
                                Player a = Bukkit.getServer().getPlayerExact(args[1]);
                                //플레이어 파일 검사
                                if(playerFile.existPlayerFile(a)){
                                    a.setLevel(0);
                                    //스텟 초기화
                                    for(String b : playerFile.getPlayerKeys(a)){
                                        playerFile.setPlayerFile(a, b, 0);
                                    }
                                    //체력 재설정
                                    playerPatience(a);
                                    p.sendMessage(messageHead() + ChatColor.WHITE + "[" + a.getName() + "] 님의 레벨(스텟)을 초기화시켰습니다 !");
                                } else {
                                    playerErrorMessage(p);
                                }
                            } catch (NullPointerException e){  //플레이어에 오류가 있을 때
                                playerErrorMessage(p);
                                e.printStackTrace();
                                return false;
                            }
                        } else if(args[0].equalsIgnoreCase("set")){
                            p.sendMessage(messageHead() + ChatColor.RED + "명령어가 올바르지 않습니다 !");
                            p.sendMessage(messageHead() + ChatColor.WHITE + "/stat set [플레이어] [레벨] " + ChatColor.GREEN + "플레이어의 레벨을 임의로 지정합니다. " + ChatColor.GRAY + "(스텟은 초기화)");
                        }
                    } else if(args.length == 3){  //명령어 문자가 4개 이상인 경우------------------------
                        if(args[0].equalsIgnoreCase("set")){
                            try{  //플레이어 오류검사
                                //플레이어 선언
                                Player a = Bukkit.getServer().getPlayerExact(args[1]);
                                if(playerFile.existPlayerFile(a)){
                                    a.setLevel(Integer.parseInt(args[2]));
                                    //스텟 초기화
                                    for(String b : playerFile.getPlayerKeys(p)){
                                        playerFile.setPlayerFile(a, b, 0);
                                    }
                                    //체력 재설정
                                    playerPatience(a);
                                    //스텟 포인트 설정
                                    playerFile.setPlayerFile(a, "statpoint", Integer.parseInt(args[2]));
                                    p.sendMessage(messageHead() + ChatColor.WHITE + "[" + a.getName() + "] 님의 스텟이 정상적으로 세팅되었습니다 !");
                                } else {
                                    playerErrorMessage(p);
                                }
                            } catch (NullPointerException e){ //플레이어에 오류가 있을 때
                                e.printStackTrace();
                                playerErrorMessage(p);
                                return false;
                            }

                        }
                    } //args.length 가 3개인 경우---------------------
                }
            }
        return false;
    }
    //스텟 창 내 버튼 기능---------------------------------------------------------
    @EventHandler
    public void ivClick(InventoryClickEvent e){
        Player p = (Player) e.getWhoClicked();
        //열려있는(스텟) 인벤토리
        if(!e.getView().getTitle().contains("스텟") || e.getCurrentItem() == null) {
            return;
        }
        if((Integer)playerFile.getPlayerFile(p, "patience") < 50 && e.getCurrentItem().getType() == Material.getMaterial(String.valueOf(getConfig().get("stats.patience.material")))){
            playerPatience(p);
        }
        //클릭한 아이템에 따른 작동
        e.setCancelled(true);
        for(String a : getConfig().getConfigurationSection("stats").getKeys(false)){
            if(e.getCurrentItem().getItemMeta().getDisplayName().contains(String.valueOf(getConfig().get("stats" + "." + a + "." + "name")))){
                playerData.statUp(p, a);
                p.openInventory(inv(p));
            }
        }
    }
    @EventHandler
    public void playerAttack(EntityDamageByEntityEvent e){
        Player p = (Player) e.getDamager();
        if(isCritical(p)){
            e.setDamage(e.getDamage() / 1.5);
        }
        if((Integer)playerFile.getPlayerFile(p, "attack") != 0){
            e.setDamage(e.getDamage() * ((Integer)playerFile.getPlayerFile(p, "attack") * Double.valueOf(String.valueOf(getConfig().get("stats.attack.stat.치명타확률"))).doubleValue()));
            e.setDamage(Math.round(e.getDamage()*100)/100);
            if(Math.random() <= (Integer)playerFile.getPlayerFile(p, "attack") / 5 * Double.valueOf(String.valueOf(getConfig().get("stats.attack.stat.치명타확률"))).doubleValue()){
                e.setDamage(e.getDamage() * 1.5);
                p.sendMessage(messageHead() + ChatColor.YELLOW + "" + ChatColor.BOLD + "크리티컬 ! +" + e.getDamage());
            }
        } else {
            e.setDamage(Math.round(e.getDamage()*100)/100);
        }
        e.getDamager().sendMessage(String.valueOf(e.getDamage()));
    }

    public void playerPatience(Player p){
        p.setHealthScale(20 + (Integer)playerFile.getPlayerFile(p, "patience") * Integer.parseInt(String.valueOf(getConfig().get("stats.patience.stat.체력"))));
    }
    //플레이어 크리티컬 여부 확인-------------------------------------
    private boolean isCritical(Player p){
        return
                p.getFallDistance() > 0.0F &&
                !p.isOnGround() &&
                !p.isInsideVehicle() &&
                !p.hasPotionEffect(PotionEffectType.BLINDNESS) &&
                p.getLocation().getBlock().getType() != Material.LADDER &&
                p.getLocation().getBlock().getType() != Material.VINE;
    }
    //메시지 헤더--------------------------------------------------------------
    public String messageHead(){
        return ChatColor.BOLD + "" + ChatColor.DARK_GREEN + "[" + ChatColor.GREEN + "STAT" + ChatColor.DARK_GREEN + "] " + ChatColor.RESET + "";
    }
    //플레이어 입력 오류 메시지--------------------------------------------------------------
    public void playerErrorMessage(Player p){
        p.sendMessage(messageHead() + ChatColor.RED + "대상이 올바르지 않습니다 !");
    }

}
