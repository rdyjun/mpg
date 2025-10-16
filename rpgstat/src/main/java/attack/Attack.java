package attack;

import files.PlayerFile;
import namegenerator.KeyNameGenerator;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import rpgstat.RpgStat;

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

        return rpgStat.getConfig().getDouble(damageKeyName);
    }

    public String getNowLevelLore(Player player) {
        Integer statLevel = (Integer) PlayerFile.getPlayerFile(player, STAT_NAME);
        Double statOption = rpgStat.getConfig().getDouble(
                KeyNameGenerator.getKey(STAT_NAME, DAMAGE_STAT_NAME));
        return ChatColor.WHITE + "최종 데미지 " + ChatColor.GREEN + (statLevel * statOption) + ChatColor.WHITE + " 증가";
    }

    public String getNextLevelLore(Player player) {
        Integer statLevel = ((Integer) PlayerFile.getPlayerFile(player, STAT_NAME) + 1);
        Double statOption = rpgStat.getConfig().getDouble(
                KeyNameGenerator.getKey(STAT_NAME, DAMAGE_STAT_NAME));
        return ChatColor.GRAY + "최종 데미지가 " + ChatColor.DARK_GREEN + (statLevel * statOption) + ChatColor.WHITE
                + " 만큼 증가합니다.";
    }
}
