package vitality;

import files.PlayerFile;
import namegenerator.KeyNameGenerator;
import org.bukkit.entity.Player;
import rpgstat.RpgStat;

public class Vitality {

    private static final String STAT_NAME = "vitality";
    private static final String HEALTH_STAT_NAME = "health";

    private final RpgStat rpgStat;

    public Vitality(RpgStat rpgStat) {
        this.rpgStat = rpgStat;
    }

    public void updateVitality(Player player) {
        String healthKeyName = KeyNameGenerator.getKey(STAT_NAME, HEALTH_STAT_NAME);

        double healthStatOption = rpgStat.getConfig().getDouble(healthKeyName);
        double playerHealth = (double) PlayerFile.getPlayerFile(player, STAT_NAME);

        double updatedHealth = healthStatOption + playerHealth;

        player.setHealthScale(updatedHealth);

        PlayerFile.setPlayerFile(player, "vitality", updatedHealth);
    }

    public void initVitality(Player player) {
        double playerHealth = (double) PlayerFile.getPlayerFile(player, STAT_NAME);

        player.setHealthScale(playerHealth);
    }
}
