package rpgstat;

import files.PlayerFile;
import java.util.ArrayList;
import java.util.List;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class ItemLore {
    private final RpgStat rpgStat;
    private final PlayerFile playerFile;

    public ItemLore(RpgStat rpgStat) {
        this.rpgStat = rpgStat;
        this.playerFile = new PlayerFile(rpgStat);
    }

    public ItemStack StatInformation(Player p, String itemName) {
        String head = getHead(itemName);

        Material material = Material.matchMaterial(
                String.valueOf(rpgStat.getConfig().get(head + "." + itemName + "." + "material")));
        ChatColor chatColor = ChatColor.getByChar(
                String.valueOf(rpgStat.getConfig().get(head + "." + itemName + "." + "color")));
        String statName = String.valueOf(rpgStat.getConfig().get(head + "." + itemName + "." + "name"));
        //LORE 영역
        List<String> lore = new ArrayList<>();
        //기본 아이템 설명
        for (String s : rpgStat.getConfig().getStringList(head + "." + itemName + "." + "lore" + "." + "itemlore")) {
            lore.add(ChatColor.BOLD + ChatColor.translateAlternateColorCodes('&', s));
        }
        if (head == "stats") {  //스텟일때
            //현재 레벨
            lore.add(ChatColor.GOLD + "현재 레벨 : " + playerFile.getPlayerFile(p, itemName));
            //현재 레벨 설명----------
            getNowLevelLore(p, itemName, lore);
            //--------------------------
            //다음 레벨
            if ((Integer) (playerFile.getPlayerFile(p, itemName)) < (Integer) rpgStat.getConfig().get(head + "." + itemName + ".max-level")) {
                lore.add(ChatColor.DARK_PURPLE + "다음 레벨 : " + ((Integer) (playerFile.getPlayerFile(p, itemName)) + 1));
            } else {
                lore.add(ChatColor.DARK_PURPLE + "다음 레벨 : MAX");
            }
            //다음 레벨 설명
            if ((Integer) (playerFile.getPlayerFile(p, itemName)) < (Integer) rpgStat.getConfig().get("setting.max")) {
                getNextLevelLore(p, itemName, lore);
            } else {
                lore.add(ChatColor.GRAY + "  마지막 레벨입니다.");
            }
            //공백
            lore.add(" ");
            //버튼 힌트
            for (String s : rpgStat.getConfig().getStringList(head + "." + itemName + "." + "lore" + "." + "statup")) {
                if ((Integer) (playerFile.getPlayerFile(p, itemName)) < (Integer) rpgStat.getConfig()
                        .get("setting.max")) {
                    lore.add(ChatColor.DARK_GRAY + s);
                } else {
                    lore.add(ChatColor.DARK_GRAY + "이 스텟은 더 이상 레벨을 올릴 수 없습니다.");
                }
            }
        }
        //아이템 선언
        ItemStack stat;
        //아이템 수량 지정
        if (itemName.equalsIgnoreCase("statpoint")) {
            int n;
            if ((Integer) playerFile.getPlayerFile(p, "statpoint") <= 0) {
                n = 1;
            } else {
                n = (Integer) playerFile.getPlayerFile(p, "statpoint");
            }
            stat = new ItemStack(material, n);
        } else if (material == Material.getMaterial("STAINED_GLASS_PANE")) {
            stat = new ItemStack(material, 1, (short) 7);
        } else {
            stat = new ItemStack(material);
        }

        ItemMeta statMeta = stat.getItemMeta();
        //아이템 이름
        if (itemName.equalsIgnoreCase("statpoint")) {
            statMeta.setDisplayName(
                    chatColor + "" + ChatColor.BOLD + statName + " : " + ChatColor.YELLOW + playerFile.getPlayerFile(p,
                            "statpoint"));
        } else {
            statMeta.setDisplayName(chatColor + "" + ChatColor.BOLD + statName);
        }
        statMeta.setLore(lore);
        statMeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        stat.setItemMeta(statMeta);

        return stat;
    }

    private String getHead(String itemName) {
        if (rpgStat.getConfig().contains("stats." + itemName)) {
            return "stats";
        }

        return "info";
    }

    //각 레벨 별 설명
    public void getNowLevelLore(Player p, String itemName, List<String> lore) {
        //stat == 100%기준 배율 증가 - 공격력, 치명타 데미지, 이동속도, 공격속도
        //statper == 0%기준 배율 증가 - 회피율, 경험치 흭득량
        //statpls == 0기준 더하기 증가 - 체력, 기력
        //specific == 5배수 레벨 및 0% 기준 증가 - 치명타 확률
        //specificpls == 5배수 레벨 기준 더하기 증가
        String a = "stats" + "." + itemName + ".";
        if (rpgStat.getConfig().contains(a + "stat")) {  //공격력, 치명타 데미지, 이동속도, 공격속도
            for (String s : rpgStat.getConfig().getConfigurationSection(a + "stat").getKeys(false)) {
                lore.add(ChatColor.WHITE + "  " + s + " : +" +
                        (1 + (Integer) playerFile.getPlayerFile(p, itemName) * Double.valueOf(
                                String.valueOf(rpgStat.getConfig().get(a + "stat" + "." + s))).doubleValue()) * 100
                        + "%");
            }
        }
        if (rpgStat.getConfig().contains(a + "specific")) {  //치명타확률
            for (String s : rpgStat.getConfig().getConfigurationSection(a + ".specific").getKeys(false)) {
                lore.add(ChatColor.WHITE + "  " + s + " : +"
                        + (Integer) playerFile.getPlayerFile(p, itemName) / 5 * Double.valueOf(
                        String.valueOf(rpgStat.getConfig().get(a + "specific" + "." + s))).doubleValue() + "%");
            }
        }
        if (rpgStat.getConfig().contains(a + "statper")) {  //회피율, 경험치 흭득량
            for (String s : rpgStat.getConfig().getConfigurationSection(a + ".statper").getKeys(false)) {
                lore.add(ChatColor.WHITE + "  " + s + " : +" + ((Integer) playerFile.getPlayerFile(p, itemName)
                        * Double.valueOf(String.valueOf(rpgStat.getConfig().get(a + "statper" + "." + s)))
                        .doubleValue()) + "%");
            }
        }
        if (rpgStat.getConfig().contains(a + "statpls")) {  //체력, 기력
            for (String s : rpgStat.getConfig().getConfigurationSection(a + ".statpls").getKeys(false)) {
                lore.add(ChatColor.WHITE + "  " + s + " : +" + ((Integer) playerFile.getPlayerFile(p, itemName)
                        * Double.valueOf(String.valueOf(rpgStat.getConfig().get(a + "statpls" + "." + s)))
                        .doubleValue()));
            }
        }
    }

    public void getNextLevelLore(Player p, String itemName, List<String> lore) {
        //stat == 100%기준 배율 증가 - 공격력, 치명타 데미지, 이동속도, 공격속도
        //statper == 0%기준 배율 증가 - 회피율, 경험치 흭득량
        //statpls == 0기준 더하기 증가 - 체력, 기력
        //specific == 5배수 레벨 및 0% 기준 증가 - 치명타 확률
        //specificpls == 5배수 레벨 기준 더하기 증가
        String a = "stats" + "." + itemName + ".";
        if (rpgStat.getConfig().contains(a + "stat")) {  //공격력, 치명타 데미지, 이동속도, 공격속도
            for (String s : rpgStat.getConfig().getConfigurationSection(a + "stat").getKeys(false)) {
                lore.add(ChatColor.WHITE + "  " + s + " : +" +
                        (1 + ((Integer) playerFile.getPlayerFile(p, itemName) + 1) * Double.valueOf(
                                String.valueOf(rpgStat.getConfig().get(a + "stat" + "." + s))).doubleValue()) * 100
                        + "%");
            }
        }
        if (rpgStat.getConfig().contains(a + "specific")) {  //치명타확률
            for (String s : rpgStat.getConfig().getConfigurationSection(a + ".specific").getKeys(false)) {
                lore.add(ChatColor.WHITE + "  " + s + " : +"
                        + ((Integer) playerFile.getPlayerFile(p, itemName) + 1) / 5 * Double.valueOf(
                        String.valueOf(rpgStat.getConfig().get(a + "specific" + "." + s))).doubleValue() + "%");
            }
        }
        if (rpgStat.getConfig().contains(a + "statper")) {  //회피율, 경험치 흭득량
            for (String s : rpgStat.getConfig().getConfigurationSection(a + ".statper").getKeys(false)) {
                lore.add(ChatColor.WHITE + "  " + s + " : +" + (((Integer) playerFile.getPlayerFile(p, itemName) + 1)
                        * Double.valueOf(String.valueOf(rpgStat.getConfig().get(a + "statper" + "." + s)))
                        .doubleValue()) + "%");
            }
        }
        if (rpgStat.getConfig().contains(a + "statpls")) {  //체력, 기력
            for (String s : rpgStat.getConfig().getConfigurationSection(a + ".statpls").getKeys(false)) {
                lore.add(ChatColor.WHITE + "  " + s + " : +" + (((Integer) playerFile.getPlayerFile(p, itemName) + 1)
                        * Double.valueOf(String.valueOf(rpgStat.getConfig().get(a + "statpls" + "." + s)))
                        .doubleValue()));
            }
        }
    }
}
