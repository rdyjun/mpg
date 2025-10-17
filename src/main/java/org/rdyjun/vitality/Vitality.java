package org.rdyjun.vitality;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.entity.Player;
import org.rdyjun.files.PlayerFile;
import org.rdyjun.namegenerator.KeyNameGenerator;
import org.rdyjun.rpgstat.RpgStat;
import org.rdyjun.rpgstat.Stat;


public class Vitality extends Stat {

    public static final String TYPE = "vitality";
    private static final Double BASE_HEALTH = 20d;
    private static final String STAT_NAME = "vitality";
    private static final String HEALTH_STAT_NAME = "health";

    public Vitality(RpgStat rpgStat) {
        super(rpgStat);
    }

    public void increase(Player player) {
        String healthKeyName = KeyNameGenerator.getKey(STAT_NAME, HEALTH_STAT_NAME);

        double healthStatOption = rpgStat.getConfig().getDouble(healthKeyName);
        Integer playerHealthLevel = (Integer) PlayerFile.getPlayerFile(player, STAT_NAME);

        double updatedHealth = BASE_HEALTH + (healthStatOption * playerHealthLevel);

        player.setHealthScale(updatedHealth);
    }

    public void init(Player player) {
        increase(player);
    }

    public Component getNowLevelLore(Player player) {
        Integer statLevel = (Integer) PlayerFile.getPlayerFile(player, STAT_NAME);
        double statOption = rpgStat.getConfig().getDouble(
                KeyNameGenerator.getKey(STAT_NAME, HEALTH_STAT_NAME));

        double lastHealth = Math.floor(statLevel * statOption * 10.0) / 10.0;

        return Component.empty()
                .append(Component.text("최종 체력 ", NamedTextColor.WHITE))
                .append(Component.text(String.valueOf(lastHealth), NamedTextColor.GREEN).decorate(TextDecoration.BOLD))
                .append(Component.text(" 증가", NamedTextColor.WHITE));
    }

    public Component getNextLevelLore(Player player) {
        int statLevel = ((Integer) PlayerFile.getPlayerFile(player, STAT_NAME) + 1);
        double statOption = rpgStat.getConfig().getDouble(
                KeyNameGenerator.getKey(STAT_NAME, HEALTH_STAT_NAME));

        return Component.empty()
                .append(Component.text("최종 체력이 ", NamedTextColor.GRAY))
                .append(Component.text(String.valueOf(Math.floor(statLevel * statOption * 10.0) / 10.0),
                        NamedTextColor.DARK_GREEN).decorate(TextDecoration.BOLD))
                .append(Component.text(" 만큼 증가합니다.", NamedTextColor.WHITE));
    }

    public Component getDisplayName() {
        return Component.text("생명력", NamedTextColor.RED);
    }
}
