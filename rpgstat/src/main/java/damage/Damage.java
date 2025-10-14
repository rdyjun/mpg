package damage;

import files.PlayerFile;
import namegenerator.KeyNameGenerator;
import org.bukkit.entity.Player;
import rpgstat.RpgStat;
import rpgstat.Stat;

public class Damage extends Stat {

    private static final String STAT_NAME = "damage";
    private static final String DAMAGE_STAT_NAME = "damage";

    public Damage(RpgStat rpgStat) {
        super(rpgStat);
    }

    public void increase(Player player) {
        String damageKeyName = KeyNameGenerator.getKey(STAT_NAME, DAMAGE_STAT_NAME);

        float damageStatOption = (float) rpgStat.getConfig().getDouble(damageKeyName);
        float playerDamage = (Integer) PlayerFile.getPlayerFile(player, DAMAGE_STAT_NAME);

        float updatedDamage = playerDamage + damageStatOption;

        player.setLastDamage(updatedDamage);

        PlayerFile.setPlayerFile(player, STAT_NAME + "." + DAMAGE_STAT_NAME, updatedDamage);
    }

    public void init(Player player) {
        float playerDamage = (Integer) PlayerFile.getPlayerFile(player, DAMAGE_STAT_NAME);

        player.setLastDamage(playerDamage);
    }
}
