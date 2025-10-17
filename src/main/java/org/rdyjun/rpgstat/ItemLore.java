package org.rdyjun.rpgstat;

import java.util.ArrayList;
import java.util.List;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.rdyjun.agility.Agility;
import org.rdyjun.attack.Attack;
import org.rdyjun.componentgenerator.ComponentGenerator;
import org.rdyjun.files.PlayerFile;
import org.rdyjun.luck.Luck;
import org.rdyjun.vitality.Vitality;

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
        String chatColor = String.valueOf(rpgStat.getConfig().get(head + "." + itemName + "." + "color"));
        String statName = String.valueOf(rpgStat.getConfig().get(head + "." + itemName + "." + "name"));

        //LORE 영역
        List<Component> lore = new ArrayList<>();
        //기본 아이템 설명
        for (String s : rpgStat.getConfig().getStringList(head + "." + itemName + "." + "lore" + "." + "itemlore")) {
            Component converted = LegacyComponentSerializer.legacyAmpersand()
                    .deserialize("&l" + chatColor + s)
                    .decoration(TextDecoration.ITALIC, false);
            lore.add(converted);
        }
        if (head.equals("stats")) {  //스텟일때
            String maxStatKey = String.format("%s.%s.max-level", head, itemName);
            int maxStatLevel = (Integer) rpgStat.getConfig().get(maxStatKey);
            int nowStatLevel = (Integer) PlayerFile.getPlayerFile(p, itemName);

            if (nowStatLevel < maxStatLevel - 1) {
                // 현재 레벨
                lore.add(ComponentGenerator.text("현재 레벨 : " + nowStatLevel, NamedTextColor.GOLD));
                // 현재 레벨 설명
                lore.add(getNowLevelLore(p, itemName));
                lore.add(Component.space());
                // 다음 레벨
                lore.add(ComponentGenerator.text("다음 레벨 : " + (nowStatLevel + 1), NamedTextColor.DARK_PURPLE));
            } else {
                // 현재 레벨
                lore.add(ComponentGenerator.text("현재 레벨 : MAX", NamedTextColor.GOLD));
                // 현재 레벨 설명
                lore.add(getNowLevelLore(p, itemName).decoration(TextDecoration.ITALIC, false));
                lore.add(Component.space().decoration(TextDecoration.ITALIC, false));
                // 다음 레벨
                lore.add(ComponentGenerator.text("다음 레벨 : MAX", NamedTextColor.DARK_PURPLE)
                        .decoration(TextDecoration.ITALIC, false));
            }

            //다음 레벨 설명
            if (nowStatLevel < maxStatLevel - 1) {
                lore.add(getNextLevelLore(p, itemName));
            } else {
                lore.add(ComponentGenerator.text("  마지막 레벨입니다.", NamedTextColor.GRAY));
            }

            //공백
            lore.add(Component.space());

            //버튼 힌트
            for (String s : rpgStat.getConfig().getStringList(head + "." + itemName + "." + "lore" + "." + "statup")) {
                if (nowStatLevel < maxStatLevel) {
                    lore.add(ComponentGenerator.text(s, NamedTextColor.DARK_GRAY));
                    continue;
                }

                lore.add(ComponentGenerator.text("이 스텟은 더 이상 레벨을 올릴 수 없습니다.", NamedTextColor.DARK_GRAY));
            }
        }
        //아이템 선언
        ItemStack stat;

        int statPoint = (Integer) PlayerFile.getPlayerFile(p, STAT_POINT_NAME);

        //아이템 수량 지정
        if (itemName.equalsIgnoreCase(STAT_POINT_NAME)) {

            stat = new ItemStack(material, Math.max(statPoint, 1));
        } else if (material == Material.BLACK_STAINED_GLASS_PANE) {
            stat = new ItemStack(material, 1, (short) 7);
        } else {
            stat = new ItemStack(material);
        }

        ItemMeta statMeta = stat.getItemMeta();
        //아이템 이름
        System.out.println(itemName + " :: " + head);
        System.out.println(head.contains(itemName));
        if (head.equals("stats") || itemName.equals(STAT_POINT_NAME)) {
            statMeta.displayName(getDisplayName(itemName, statPoint));
        } else {
            statMeta.displayName(
                    ComponentGenerator.text(statName, NamedTextColor.YELLOW).decorate(TextDecoration.BOLD));
        }
        statMeta.lore(lore);
        statMeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        stat.setItemMeta(statMeta);

        return stat;
    }

    private Component getDisplayName(String statName, int statPoint) {
        if (statName.equals("statpoint")) {
            return ComponentGenerator.text("보유 스탯 : ", NamedTextColor.WHITE)
                    .decorate(TextDecoration.BOLD)
                    .decoration(TextDecoration.ITALIC, false)
                    .append(ComponentGenerator.text(String.valueOf(statPoint), NamedTextColor.GOLD)
                            .decorate(TextDecoration.BOLD)
                            .decoration(TextDecoration.ITALIC, false));
        }

        if (statName.equals("luck")) {
            return rpgStat.luck.getDisplayName();
        }

        if (statName.equals("attack")) {
            return rpgStat.attack.getDisplayName();
        }

        if (statName.equals("vitality")) {
            return rpgStat.vitality.getDisplayName();
        }

        if (statName.equals("agility")) {
            return rpgStat.agility.getDisplayName();
        }

        return ComponentGenerator.text("알 수 없음", NamedTextColor.WHITE);
    }

    private String getHead(String itemName) {
        if (rpgStat.getConfig().contains("stats." + itemName)) {
            return "stats";
        }

        return "info";
    }

    //각 레벨 별 설명
    public Component getNowLevelLore(Player p, String itemName) {
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

        return Component.empty();
    }

    public Component getNextLevelLore(Player p, String itemName) {
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

        return Component.empty();
    }
}
