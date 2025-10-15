package vitality;

import files.PlayerFile;
import namegenerator.KeyNameGenerator;
import org.bukkit.entity.Player;
import rpgstat.Stat;

public class Vitality extends Stat {

    private static final Double BASE_HEALTH = 20d;
    private static final String STAT_NAME = "vitality";
    private static final String HEALTH_STAT_NAME = "health";

    public Vitality(rpgstat.RpgStat rpgStat) {
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
}
