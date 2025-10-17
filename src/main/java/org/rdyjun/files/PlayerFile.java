package org.rdyjun.files;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.Set;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.rdyjun.rpgstat.RpgStat;

public class PlayerFile {

    private static RpgStat rpgStat;

    public static void init(RpgStat rpgStat) {
        PlayerFile.rpgStat = rpgStat;
    }

    /**
     * 플레이어 파일 생성
     *
     * @param player 플레이어
     */
    public static void createFile(Player player) {
        if (existPlayerFile(player)) {
            return;
        }

        File file = getFile(player);
        FileConfiguration config = getConfig(player);

        try {
            ConfigurationSection stats = rpgStat.getConfig()
                    .getConfigurationSection("stats");
            if (stats == null) {
                rpgStat.getLogger().warning("No stats section found");
                return;
            }

            for (String statName : stats.getKeys(false)) {
                config.set(player.getUniqueId() + "." + statName, 0);
            }
            config.set(player.getUniqueId() + ".statpoint", 0);

            config.save(file);

        } catch (IOException e) {
            String warningMessage = String.format(" 플레이어 파일 생성 중 오류가 발생했습니다.: %s", e.getMessage());
            rpgStat.getLogger()
                    .warning(warningMessage);
        }
    }

    /**
     * 플레이어 파일 값 가져오기
     *
     * @param player   플레이어
     * @param statName 스텟 이름
     * @return 스텟 값
     */
    public static Object getPlayerFile(Player player, String statName) {
        FileConfiguration config = getConfig(player);

        Object result = config.get(player.getUniqueId() + "." + statName);

        if (result == null) {
            throw new IllegalArgumentException("플레이어 파일에 " + statName + " 스텟이 존재하지 않습니다.");
        }
        return result;
    }

    /**
     * 플레이어 스탯 수정
     *
     * @param player   플레이어
     * @param statName 스텟 이름
     * @param value    스텟 값
     */
    public static void setPlayerFile(Player player, String statName, Object value) {
        FileConfiguration config = getConfig(player);

        config.set(player.getUniqueId() + "." + statName, value);
    }

    public static Set<String> getPlayerKeys(Player player) {  //플레이어 파일 키값 가져오기
        FileConfiguration config = getConfig(player);

        String uuid = String.valueOf(player.getUniqueId());

        ConfigurationSection configurationSection = config.getConfigurationSection(uuid);
        if (configurationSection == null) {
            String warningMessage = String.format("플레이어 스탯을 조회할 수 없습니다: %s", player.getName());
            rpgStat.getLogger()
                    .warning(warningMessage);

            return Collections.emptySet();
        }

        return configurationSection.getKeys(false);
    }

    public static boolean existPlayerFile(Player player) {  //플레이어 파일 존재여부 확인
        File file = getFile(player);

        return file.exists();
    }

    public static void savePlayerFile(Player player) {  //플레이어 파일 저장
        File file = getFile(player);
        FileConfiguration config = getConfig(player);

        try {
            config.save(file);
        } catch (IOException e) {
            String warningMessage = String.format(" 파일 생성 중 오류가 발생했습니다: %s", player.getName());
            rpgStat.getLogger()
                    .warning(warningMessage);
        }
    }

    public static File getFile(Player p) {
        return new File(rpgStat.getDataFolder(), "playerdata/" + p.getUniqueId() + ".yml");
    }

    public static FileConfiguration getConfig(Player player) {
        File file = getFile(player);
        return YamlConfiguration.loadConfiguration(file);
    }
}
