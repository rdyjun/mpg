package rpgstat;

import agility.Agility;
import attack.Attack;
import files.PlayerFile;
import java.util.ArrayList;
import java.util.List;
import luck.Luck;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import vitality.Vitality;

public class ItemLore {

    private static final String STAT_POINT_NAME = "statpoint";

    private final RpgStat rpgStat;

    public ItemLore(RpgStat rpgStat) {
        this.rpgStat = rpgStat;
    }

    public ItemStack generate(Player p, String itemName) {
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
        if (head.equals("stats")) {  //스텟일때
            int maxStatLevel = (Integer) rpgStat.getConfig().get(head + "." + itemName + ".max-level");
            int nowStatLevel = (Integer) PlayerFile.getPlayerFile(p, itemName);

            if (nowStatLevel < maxStatLevel - 1) {
                // 현재 레벨
                lore.add(ChatColor.GOLD + "현재 레벨 : " + nowStatLevel);
                // 현재 레벨 설명
                lore.add(getNowLevelLore(p, itemName));
                lore.add("\n");
                // 다음 레벨
                lore.add(ChatColor.DARK_PURPLE + "다음 레벨 : " + (nowStatLevel + 1));
            } else {
                // 현재 레벨
                lore.add(ChatColor.GOLD + "현재 레벨 : MAX");
                // 현재 레벨 설명
                lore.add(getNowLevelLore(p, itemName));
                lore.add("\n");
                // 다음 레벨
                lore.add(ChatColor.DARK_PURPLE + "다음 레벨 : MAX");
            }

            //다음 레벨 설명
            if (nowStatLevel < maxStatLevel - 1) {
                lore.add(getNextLevelLore(p, itemName, lore));
            } else {
                lore.add(ChatColor.GRAY + "  마지막 레벨입니다.");
            }

            //공백
            lore.add(" ");

            //버튼 힌트
            for (String s : rpgStat.getConfig().getStringList(head + "." + itemName + "." + "lore" + "." + "statup")) {
                if (nowStatLevel < maxStatLevel) {
                    lore.add(ChatColor.DARK_GRAY + s);
                    continue;
                }

                lore.add(ChatColor.DARK_GRAY + "이 스텟은 더 이상 레벨을 올릴 수 없습니다.");
            }
        }
        //아이템 선언
        ItemStack stat;
        //아이템 수량 지정
        if (itemName.equalsIgnoreCase(STAT_POINT_NAME)) {
            int n;
            if ((Integer) PlayerFile.getPlayerFile(p, STAT_POINT_NAME) <= 0) {
                n = 1;
            } else {
                n = (Integer) PlayerFile.getPlayerFile(p, STAT_POINT_NAME);
            }
            stat = new ItemStack(material, n);
        } else if (material == Material.getMaterial("STAINED_GLASS_PANE")) {
            stat = new ItemStack(material, 1, (short) 7);
        } else {
            stat = new ItemStack(material);
        }

        ItemMeta statMeta = stat.getItemMeta();
        //아이템 이름
        if (itemName.equalsIgnoreCase(STAT_POINT_NAME)) {
            statMeta.setDisplayName(
                    chatColor + "" + ChatColor.BOLD + statName + " : " + ChatColor.YELLOW + PlayerFile.getPlayerFile(p,
                            STAT_POINT_NAME));
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
    public String getNowLevelLore(Player p, String itemName) {
        if (itemName.equals(Agility.TYPE)) {
            return rpgStat.agility.getNowLevelLore(p);
        }

        if (itemName.equals(Attack.TYPE)) {
            return rpgStat.attack.getNowLevelLore(p);
        }

        if (itemName.equals(Vitality.TYPE)) {
            return rpgStat.vitality.getNowLevelLore(p);
        }

        if (itemName.equals(Luck.TYPE)) {
            return rpgStat.luck.getNowLevelLore(p);
        }

        return "";
    }

    public String getNextLevelLore(Player p, String itemName, List<String> lore) {
        if (itemName.equals(Agility.TYPE)) {
            return rpgStat.agility.getNextLevelLore(p);
        }

        if (itemName.equals(Attack.TYPE)) {
            return rpgStat.attack.getNextLevelLore(p);
        }

        if (itemName.equals(Vitality.TYPE)) {
            return rpgStat.vitality.getNextLevelLore(p);
        }

        if (itemName.equals(Luck.TYPE)) {
            return rpgStat.luck.getNextLevelLore(p);
        }

        return "";
    }
}
