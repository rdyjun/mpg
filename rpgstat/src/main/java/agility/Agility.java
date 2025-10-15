package agility;

import files.PlayerFile;
import namegenerator.KeyNameGenerator;
import org.bukkit.entity.Player;
import rpgstat.Stat;

public class Agility extends Stat {

    private static final String STAT_NAME = "agility";
    private static final String SPEED_STAT_NAME = "speed";

    public Agility(rpgstat.RpgStat rpgStat) {
        super(rpgStat);
    }

    public void increase(Player player) {
        String agilityKeyName = KeyNameGenerator.getKey(STAT_NAME, STAT_NAME);

        float agilityStatOption = (float) rpgStat.getConfig().getDouble(agilityKeyName);
        float playerAgilityStat = (Integer) PlayerFile.getPlayerFile(player, STAT_NAME);
        float playerSpeed = player.getWalkSpeed();

        float updatedSpeed = playerSpeed + (playerAgilityStat * agilityStatOption);

        player.setWalkSpeed(updatedSpeed);
        player.setFlySpeed(updatedSpeed);
    }

    public void init(Player player) {
        increase(player);
    }
}
