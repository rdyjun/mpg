package org.rdyjun.files;

import java.io.File;
import java.io.IOException;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.codehaus.plexus.util.StringUtils;
import org.rdyjun.componentgenerator.ComponentGenerator;
import org.rdyjun.rpgstat.RpgStat;

public class PlayerData {

    private static final String STAT_POINT_NAME = "statpoint";

    private final RpgStat rpgStat;

    public PlayerData(RpgStat rpgStat) {
        this.rpgStat = rpgStat;
    }

    /**
     * 스텟 상승
     *
     * @param player   플레이어
     * @param statName 스탯명
     */
    public synchronized boolean statUp(Player player, String statName) {
        int statPoint = (Integer) PlayerFile.getPlayerFile(player, STAT_POINT_NAME);

        // 스텟 포인트가 없을 때 종료
        if (statPoint <= 0) {
            player.sendMessage(
                    rpgStat.messageHead().append(ComponentGenerator.text("스텟 포인트가 부족합니다 !", NamedTextColor.RED)));
            return false;
        }

        int statLevel = (Integer) PlayerFile.getPlayerFile(player, statName);
        String key = String.format("stats.%s.max-level", statName);
        int maxLevel = rpgStat.getConfig().getInt(key);
        if (statLevel >= maxLevel) {
            player.sendMessage(
                    rpgStat.messageHead().append(ComponentGenerator.text("최대 레벨에 도달했습니다 !", NamedTextColor.RED)));
            return false;
        }

        File file = PlayerFile.getFile(player);
        FileConfiguration config = PlayerFile.getConfig(player);
        config.set(player.getUniqueId() + "." + statName, statLevel + 1);
        config.set(player.getUniqueId() + "." + STAT_POINT_NAME, statPoint - 1);
        try {
            config.save(file);
        } catch (IOException e) {
            rpgStat.getLogger()
                    .warning(e.getMessage());
            return false;
        }
        PlayerFile.savePlayerFile(player);

        String statColor = getStatColor(statName);
        String statDisplayName = getDisplayName(statName);

        Component message = Component.text("[")
                .color(NamedTextColor.DARK_GREEN)
                .decorate(TextDecoration.BOLD)
                .append(Component.text("STAT")
                        .color(NamedTextColor.DARK_GREEN)
                        .decorate(TextDecoration.BOLD))
                .append(Component.text("] ")
                        .color(NamedTextColor.DARK_GREEN)
                        .decorate(TextDecoration.BOLD))
                .append(LegacyComponentSerializer.legacyAmpersand()
                        .deserialize(statColor + statDisplayName + " "))
                .append(Component.text(String.format("%d -> %d 스탯 상승 !", statLevel, statLevel + 1),
                        NamedTextColor.WHITE));

        player.sendMessage(message);

        return true;
    }

    public String getStatColor(String statName) {
        String statColor = (String) rpgStat.getConfig().get("stats." + statName + "." + "color");
        if (StringUtils.isBlank(statColor)) {
            String warningMessage = String.format(" 스텟 %s의 색상이 올바르지 않습니다. 기본값(흰색)으로 설정됩니다.", statName);
            rpgStat.getLogger()
                    .warning(warningMessage);
            return "&f";
        }

        return statColor;
    }

    public String getDisplayName(String statName) {
        return String.valueOf(rpgStat.getConfig().get("stats." + statName + ".name"));
    }

}
