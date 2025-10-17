package org.rdyjun.attack;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.entity.Player;
import org.rdyjun.componentgenerator.ComponentGenerator;
import org.rdyjun.files.PlayerFile;
import org.rdyjun.namegenerator.KeyNameGenerator;
import org.rdyjun.rpgstat.RpgStat;

public class Attack {

    public static final String TYPE = "attack";
    private static final String STAT_NAME = "attack";
    private static final String DAMAGE_STAT_NAME = "damage";

    private final RpgStat rpgStat;

    public Attack(RpgStat rpgStat) {
        this.rpgStat = rpgStat;
    }

    public double getAdditionalDamage(Player player) {
        String damageKeyName = KeyNameGenerator.getKey(STAT_NAME, DAMAGE_STAT_NAME);

        double statOption = rpgStat.getConfig().getDouble(damageKeyName);
        Integer statLevel = (Integer) PlayerFile.getPlayerFile(player, STAT_NAME);

        return calcDamage(statLevel, statOption);
    }

    public Component getNowLevelLore(Player player) {
        int statLevel = (Integer) PlayerFile.getPlayerFile(player, STAT_NAME);
        double statOption = rpgStat.getConfig().getDouble(
                KeyNameGenerator.getKey(STAT_NAME, DAMAGE_STAT_NAME));

        double lastDamage = calcDamage(statLevel, statOption);

        return ComponentGenerator.text("최종 데미지 ", NamedTextColor.WHITE)
                .append(ComponentGenerator.text(lastDamage + "%", NamedTextColor.GREEN)
                        .decorate(TextDecoration.BOLD))
                .append(ComponentGenerator.text(" 증가", NamedTextColor.WHITE));
    }

    public Component getNextLevelLore(Player player) {
        int statLevel = ((Integer) PlayerFile.getPlayerFile(player, STAT_NAME) + 1);
        double statOption = rpgStat.getConfig().getDouble(
                KeyNameGenerator.getKey(STAT_NAME, DAMAGE_STAT_NAME));

        double lastDamage = calcDamage(statLevel, statOption);

        return ComponentGenerator.text("최종 데미지가 ", NamedTextColor.GRAY)
                .append(ComponentGenerator.text(lastDamage + "%", NamedTextColor.DARK_GREEN)
                        .decorate(TextDecoration.BOLD))
                .append(ComponentGenerator.text(" 만큼 증가합니다.", NamedTextColor.WHITE));
    }

    public double calcDamage(int statLevel, double statOption) {
        return Math.floor((statLevel * statOption + 1) * 100.0) / 100.0;
    }

    public Component getDisplayName() {
        return Component.text("공격력", NamedTextColor.DARK_RED);
    }
}
