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
        String speedKeyName = KeyNameGenerator.getKey(STAT_NAME, SPEED_STAT_NAME);

        float speedStatOption = (float) rpgStat.getConfig().getDouble(speedKeyName);
        float playerSpeed = (Integer) PlayerFile.getPlayerFile(player, SPEED_STAT_NAME);

        float updatedSpeed = playerSpeed + speedStatOption;

        player.setWalkSpeed(updatedSpeed);
        player.setFlySpeed(updatedSpeed);

        PlayerFile.setPlayerFile(player, STAT_NAME + "." + SPEED_STAT_NAME, updatedSpeed);
    }

    public void init(Player player) {
        float speed = (float) PlayerFile.getPlayerFile(player, KeyNameGenerator.getKey(STAT_NAME, SPEED_STAT_NAME));

        player.setWalkSpeed(speed);
        player.setFlySpeed(speed);
    }
}
