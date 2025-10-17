package org.rdyjun.rpgstat;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerExpChangeEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffectType;
import org.rdyjun.agility.Agility;
import org.rdyjun.attack.Attack;
import org.rdyjun.files.PlayerData;
import org.rdyjun.files.PlayerFile;
import org.rdyjun.luck.Luck;
import org.rdyjun.namegenerator.KeyNameGenerator;
import org.rdyjun.vitality.Vitality;


public class RpgStat extends JavaPlugin implements Listener {
    protected Agility agility;
    protected Vitality vitality;
    protected Attack attack;
    protected Luck luck;
    private PlayerData playerData;
    private ItemLore itemLore;

    //플러그인 활성화
    @Override
    public void onEnable() {
        Bukkit.getConsoleSender().sendMessage(NamedTextColor.GREEN + "스텟 플러그인이 활성화되었습니다");
        Bukkit.getPluginManager().registerEvents(this, this);

        if (!getDataFolder().exists()) {
            getDataFolder().mkdirs();
        }

        if (!new File(getDataFolder(), "playerdata").exists()) {
            new File(getDataFolder(), "playerdata").mkdirs();
        }

        PlayerFile.init(this);
        this.playerData = new PlayerData(this);
        this.agility = new Agility(this);
        this.vitality = new Vitality(this);
        this.attack = new Attack(this);
        this.luck = new Luck(this);

        this.itemLore = new ItemLore(this);
        saveDefaultConfig();
    }

    //플러그인 비활성화
    @Override
    public void onDisable() {
        Bukkit.getConsoleSender().sendMessage(NamedTextColor.RED + "스텟 플러그인이 비활성화되었습니다");
        saveConfig();
    }

    //레벨업 했을 때
    @EventHandler
    public void onLevelUp(PlayerExpChangeEvent e) throws IOException {
        Player p = e.getPlayer();
        //플레이어 파일 생성
        File pf = new File(getDataFolder(), "playerdata/" + p.getUniqueId() + ".yml");
        FileConfiguration pFile = YamlConfiguration.loadConfiguration(pf);

        if (e.getAmount() <= 0) {
            return;
        }

        if (p.getExp() + e.getAmount() / (float) p.getExpToLevel() >= 1.0) {
            UUID pID = p.getUniqueId();

            pFile.set(pID + ".statpoint", pFile.getInt(p.getUniqueId() + "." + "statpoint") + 1);
            pFile.save(pf);
            p.sendMessage(TextDecoration.BOLD + "" + NamedTextColor.DARK_GREEN + "[" + NamedTextColor.GREEN + "STAT"
                    + NamedTextColor.DARK_GREEN + "] " + NamedTextColor.WHITE + "레벨업! 현재 "
                    + p.getLevel() + "레벨 입니다 !");
            p.sendMessage(TextDecoration.BOLD + "" + NamedTextColor.DARK_GREEN + "[" + NamedTextColor.GREEN + "STAT"
                    + NamedTextColor.DARK_GREEN + "] " + NamedTextColor.WHITE + "보유 스텟 : "
                    + pFile.get(pID + ".statpoint"));
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onLuck(BlockBreakEvent e) {
        Player player = e.getPlayer();

        Block brokenBlock = e.getBlock();
        System.out.println("block: " + brokenBlock.getType().name());

        List<ItemStack> droppedItems = brokenBlock.getDrops()
                .stream()
                .filter((itemStack) -> !itemStack.getType().equals(Material.AIR))
                .toList();

        droppedItems.forEach((itemStack) -> System.out.println("drop: " + itemStack.getType()));

        Material firstItem = droppedItems
                .get(0)
                .getType();

        int stat = (Integer) PlayerFile.getPlayerFile(player, "luck");
        int amount = stat / 15 + 1;

        // 최종 확률
        double playerChance = getConfig().getDouble(KeyNameGenerator.getKey("luck", "chance")) * stat;
        int randomChance = ThreadLocalRandom.current().nextInt(100);

        // 적용 가능한 블록이 아니거나,
        // 첫 드랍 아이템이 부순 블록이거나(섬손)
        // 확률 미달 시 종료
        if (!luck.isAppliedMaterial(brokenBlock.getType()) || firstItem.equals(brokenBlock.getType())
                || randomChance > playerChance) {
            return;
        }

        int lastAmount = ThreadLocalRandom.current().nextInt(amount) + 1;
        player.sendMessage(
                String.valueOf(NamedTextColor.GREEN) + TextDecoration.BOLD + firstItem.name() + " " + lastAmount
                        + NamedTextColor.YELLOW
                        + TextDecoration.BOLD
                        + "개 추가 획득 !");

        e.getBlock().getDrops().forEach(item -> {
            Location dropLocation = e.getBlock().getLocation();
            ItemStack dropItem = new ItemStack(item.getType(), lastAmount);
            e.getBlock()
                    .getWorld()
                    .dropItemNaturally(dropLocation, dropItem);
        });
    }

    // 플레이어가 접속했을 때 플레이어 파일 생성 및 스탯 초기화 (이미 있다면 제외)
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void newPlayer(PlayerJoinEvent event) {
        Player player = event.getPlayer();

        //플레이어 파일 생성
        PlayerFile.createFile(player);

        Bukkit.getScheduler().runTaskAsynchronously(this, () -> {
            Bukkit.getScheduler().runTaskLater(this, () -> {
                agility.init(player);
                vitality.init(player);
            }, 5L);
        });
    }

    //스텟창 설정 ----------------------------
    public Inventory inv(Player p) {
        //스텟 메뉴 틀
        Inventory inv = Bukkit.createInventory(null, (Integer) getConfig().get("setting.size"), "스텟");
        inv.setMaxStackSize(100);
        for (String s : getConfig().getConfigurationSection("info").getKeys(false)) {
            inv.setItem(getConfig().getInt("info." + s + ".position"), itemLore.generate(p, s));
        }
        for (String s : getConfig().getConfigurationSection("stats").getKeys(false)) {
            inv.setItem(getConfig().getInt("stats." + s + ".position"), itemLore.generate(p, s));
        }
        p.openInventory(inv);
        return inv;
    }

    //스텟 메뉴 세부
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        Player p = (Player) sender;
        if (!cmd.getName().equalsIgnoreCase("stat")) {
            return false;
        }

        if (args.length == 0) {
            this.inv(p);
            return true;
        }

        if (args.length == 1) {
            if (args[0].equalsIgnoreCase("admin")) {
                p.sendMessage(messageHead() + NamedTextColor.WHITE + "/stat reset [플레이어] " + NamedTextColor.GREEN
                        + "플레이어의 스텟을 초기화한다. " + NamedTextColor.GRAY + "(레벨 0)");
                p.sendMessage(messageHead() + NamedTextColor.WHITE + "/stat set [플레이어] [레벨] " + NamedTextColor.GREEN
                        + "플레이어의 레벨을 임의로 지정합니다. " + NamedTextColor.GRAY + "(스텟은 초기화)");
            }

            if (args[0].equalsIgnoreCase("reset")) {
                p.sendMessage(messageHead() + NamedTextColor.RED + "명령어가 올바르지 않습니다 !");
                p.sendMessage(messageHead() + NamedTextColor.WHITE + "/stat reset [플레이어] " + NamedTextColor.GREEN
                        + "플레이어의 스텟을 초기화한다. " + NamedTextColor.GRAY + "(레벨 0)");
            }

            if (args[0].equalsIgnoreCase("set")) {
                p.sendMessage(messageHead() + NamedTextColor.RED + "명령어가 올바르지 않습니다 !");
                p.sendMessage(messageHead() + NamedTextColor.WHITE + "/stat set [플레이어] [레벨] " + NamedTextColor.GREEN
                        + "플레이어의 레벨을 임의로 지정합니다. " + NamedTextColor.GRAY + "(스텟은 초기화)");
            }

            return true;
        }

        if (args.length == 2) {  //명령어 문자를 3개만 받은 경우-----------------------
            if (args[0].equalsIgnoreCase("reset")) {
                try {  //플레이어 오류검사
                    //플레이어 선언
                    Player a = Bukkit.getServer().getPlayerExact(args[1]);
                    //플레이어 파일 검사
                    if (PlayerFile.existPlayerFile(a)) {
                        a.setLevel(0);
                        //스텟 초기화
                        for (String b : PlayerFile.getPlayerKeys(a)) {
                            PlayerFile.setPlayerFile(a, b, 0);
                        }
                        p.sendMessage(messageHead() + NamedTextColor.WHITE + "[" + a.getName()
                                + "] 님의 레벨(스텟)을 초기화시켰습니다 !");
                    } else {
                        playerErrorMessage(p);
                    }
                } catch (NullPointerException e) {  //플레이어에 오류가 있을 때
                    playerErrorMessage(p);
                    e.printStackTrace();
                    return false;
                }

                return true;
            }

            if (args[0].equalsIgnoreCase("set")) {
                p.sendMessage(messageHead() + NamedTextColor.RED + "명령어가 올바르지 않습니다 !");
                p.sendMessage(messageHead() + NamedTextColor.WHITE + "/stat set [플레이어] [레벨] " + NamedTextColor.GREEN
                        + "플레이어의 레벨을 임의로 지정합니다. " + NamedTextColor.GRAY + "(스텟은 초기화)");

                return true;
            }

            return false;
        }

        if (args.length == 3) {  //명령어 문자가 4개 이상인 경우------------------------
            if (!args[0].equalsIgnoreCase("set")) {
                return false;
            }

            try {  //플레이어 오류검사
                //플레이어 선언
                Player a = Bukkit.getServer().getPlayerExact(args[1]);
                if (PlayerFile.existPlayerFile(a)) {
                    a.setLevel(Integer.parseInt(args[2]));
                    //스텟 초기화
                    for (String b : PlayerFile.getPlayerKeys(p)) {
                        PlayerFile.setPlayerFile(a, b, 0);
                    }
                    //스텟 포인트 설정
                    PlayerFile.setPlayerFile(a, "statpoint", Integer.parseInt(args[2]));
                    p.sendMessage(messageHead() + NamedTextColor.WHITE + "[" + a.getName()
                            + "] 님의 스텟이 정상적으로 세팅되었습니다 !");
                } else {
                    playerErrorMessage(p);
                }
            } catch (NullPointerException e) { //플레이어에 오류가 있을 때
                e.printStackTrace();
                playerErrorMessage(p);
                return false;
            }

            return true;
        }

        return false;
    }

    //스텟 창 내 버튼 기능---------------------------------------------------------
    @EventHandler
    public void ivClick(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();
        //열려있는(스텟) 인벤토리
        if (!event.getView().title().equals("스텟") || event.getCurrentItem() == null) {
            return;
        }
        //클릭한 아이템에 따른 작동
        event.setCancelled(true);

        String eventName = event.getCurrentItem()
                .getItemMeta()
                .displayName()
                .toString();

        for (String statName : getConfig().getConfigurationSection("stats").getKeys(false)) {
            String displayName = getConfig().getString("stats." + statName + ".name");
            if (!eventName.contains(displayName)) {
                continue;
            }

            boolean isFailed = !playerData.statUp(player, statName);
            player.openInventory(inv(player));

            if (isFailed) {
                return;
            }

            player.openInventory(inv(player));

            if (statName.contains("agility")) {
                agility.increase(player);
            }

            if (statName.contains("vitality")) {
                vitality.increase(player);
            }
        }
    }

    @EventHandler
    public void playerAttack(EntityDamageByEntityEvent e) {
        if (!(e.getDamager() instanceof Player p)) {
            return;
        }

        double additionalDamage = this.attack.getAdditionalDamage(p);

        double baseDamage = e.getDamage();
        double damage = Math.floor(baseDamage * additionalDamage * 100.0) / 100.0;

        boolean isCritical = isCritical(p);
        if (isCritical) {
            double lastDamage = Math.floor((damage * 1.5) * 10.0) / 10.0;
            e.setDamage(lastDamage);
            p.sendMessage(messageHead() + NamedTextColor.YELLOW + TextDecoration.BOLD + "크리티컬 ! +" + lastDamage);

            return;
        }

        e.setDamage(damage);
    }

    //플레이어 크리티컬 여부 확인-------------------------------------
    private boolean isCritical(Player p) {
        Location location = p.getLocation();

        Block block = location.getBlock();
        if (block.getType() != Material.AIR) {
            return false;
        }

        return
                p.getFallDistance() > 0.0F &&
                        !block.getRelative(0, -1, 0).getType().isSolid() &&
                        !p.isInsideVehicle() &&
                        !p.hasPotionEffect(PotionEffectType.BLINDNESS) &&
                        block.getType() != Material.LADDER &&
                        block.getType() != Material.VINE;
    }

    //메시지 헤더--------------------------------------------------------------
    public String messageHead() {
        return TextDecoration.BOLD + "" + NamedTextColor.DARK_GREEN + "[" + NamedTextColor.GREEN + "STAT"
                + NamedTextColor.DARK_GREEN + "] ";
    }

    //플레이어 입력 오류 메시지--------------------------------------------------------------
    public void playerErrorMessage(Player p) {
        p.sendMessage(messageHead() + NamedTextColor.RED + "대상이 올바르지 않습니다 !");
    }
}
