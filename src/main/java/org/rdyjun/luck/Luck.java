package org.rdyjun.luck;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.rdyjun.componentgenerator.ComponentGenerator;
import org.rdyjun.files.PlayerFile;
import org.rdyjun.namegenerator.KeyNameGenerator;
import org.rdyjun.rpgstat.RpgStat;

public class Luck {

    public static final String TYPE = "luck";
    private static final String STAT_NAME = "luck";
    private static final String CHANCE_STAT_NAME = "chance";

    private final RpgStat rpgStat;

    public Luck(RpgStat rpgStat) {
        this.rpgStat = rpgStat;
    }

    public boolean isAppliedMaterial(Material material) {
        String materialName = material.name();
        return materialName.endsWith("_ORE") || materialName.equals("ANCIENT_DEBRIS");
    }

    public Component getNowLevelLore(Player player) {
        int statLevel = (Integer) PlayerFile.getPlayerFile(player, STAT_NAME);
        double statOption = rpgStat.getConfig().getDouble(
                KeyNameGenerator.getKey(STAT_NAME, CHANCE_STAT_NAME));

        double lastChance = Math.floor(statLevel * statOption * 1000.0) / 1000.0;

        return ComponentGenerator.text("최종 행운 ", NamedTextColor.WHITE)
                .append(ComponentGenerator.text(lastChance + "%", NamedTextColor.GREEN).decorate(TextDecoration.BOLD))
                .append(ComponentGenerator.text(" 증가", NamedTextColor.WHITE));
    }

    public Component getNextLevelLore(Player player) {
        int statLevel = ((Integer) PlayerFile.getPlayerFile(player, STAT_NAME) + 1);
        double statOption = rpgStat.getConfig().getDouble(
                KeyNameGenerator.getKey(STAT_NAME, CHANCE_STAT_NAME));

        double lastChance = Math.floor(statLevel * statOption * 1000.0) / 1000.0;

        return ComponentGenerator.text("최종 행운이 ", NamedTextColor.GRAY)
                .append(ComponentGenerator.text(lastChance + "%", NamedTextColor.DARK_GREEN)
                        .decorate(TextDecoration.BOLD))
                .append(ComponentGenerator.text(" 만큼 증가합니다.", NamedTextColor.WHITE));
    }

    public Component getDisplayName() {
        return Component.text("행운", NamedTextColor.GREEN)
                .decorate(TextDecoration.BOLD);
    }
}
