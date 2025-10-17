package org.rdyjun.agility;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.entity.Player;
import org.rdyjun.files.PlayerFile;
import org.rdyjun.namegenerator.KeyNameGenerator;
import org.rdyjun.rpgstat.RpgStat;
import org.rdyjun.rpgstat.Stat;

public class Agility extends Stat {

    public static final String TYPE = "agility";
    private static final Float DEFAULT_SPEED = 0.2f;
    private static final String STAT_NAME = "agility";
    private static final String SPEED_STAT_NAME = "speed";

    public Agility(RpgStat rpgStat) {
        super(rpgStat);
    }

    public void increase(Player player) {
        String agilityKeyName = KeyNameGenerator.getKey(STAT_NAME, SPEED_STAT_NAME);

        float agilityStatOption = (float) rpgStat.getConfig().getDouble(agilityKeyName);
        int playerAgilityLevel = (Integer) PlayerFile.getPlayerFile(player, STAT_NAME);

        // 최대 1.0f 까지 속도 증가
        float updatedSpeed = Math.min(1f, DEFAULT_SPEED + (playerAgilityLevel * agilityStatOption));

        player.setWalkSpeed(updatedSpeed);
        player.setFlySpeed(updatedSpeed);
    }

    public void init(Player player) {
        increase(player);
    }

    public Component getNowLevelLore(Player player) {
        Integer statLevel = (Integer) PlayerFile.getPlayerFile(player, STAT_NAME);
        double statOption = rpgStat.getConfig().getDouble(
                KeyNameGenerator.getKey(STAT_NAME, SPEED_STAT_NAME));

        double lastSpeed = Math.floor(statLevel * statOption * 1000.0) / 1000.0;

        return Component.text("최종 속도 ", NamedTextColor.WHITE)
                .append(Component.text(String.valueOf(lastSpeed), NamedTextColor.GREEN))
                .append(Component.text(" 증가", NamedTextColor.WHITE));
    }

    public Component getNextLevelLore(Player player) {
        int statLevel = ((Integer) PlayerFile.getPlayerFile(player, STAT_NAME) + 1);
        double statOption = rpgStat.getConfig().getDouble(
                KeyNameGenerator.getKey(STAT_NAME, SPEED_STAT_NAME));

        double lastSpeed = Math.floor(statLevel * statOption * 1000.0) / 1000.0;

        return Component.text("최종 속도가 ", NamedTextColor.GRAY)
                .append(Component.text(String.valueOf(lastSpeed), NamedTextColor.DARK_GREEN))
                .append(Component.text(" 만큼 증가합니다.", NamedTextColor.WHITE));
    }

    public Component getDisplayName() {
        return Component.text("민첩", NamedTextColor.AQUA);
    }
}
