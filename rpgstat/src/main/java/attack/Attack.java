package attack;

import files.PlayerFile;
import namegenerator.KeyNameGenerator;
import org.bukkit.entity.Player;
import rpgstat.RpgStat;

public class Attack {

    private static final String STAT_NAME = "damage";
    private static final String DAMAGE_STAT_NAME = "damage";

    private final RpgStat rpgStat;

    public Attack(RpgStat rpgStat) {
        this.rpgStat = rpgStat;
    }

    public double getAdditionalDamage(Player player) {
        String damageKeyName = KeyNameGenerator.getKey(STAT_NAME, DAMAGE_STAT_NAME);

        return rpgStat.getConfig().getDouble(damageKeyName);
    }
}
