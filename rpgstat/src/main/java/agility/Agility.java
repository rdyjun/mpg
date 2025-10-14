package agility;

import files.PlayerFile;
import namegenerator.KeyNameGenerator;
import org.bukkit.entity.Player;
import rpgstat.RpgStat;

public class Agility {

    private static final String STAT_NAME = "agility";
    private static final String SPEED_STAT_NAME = "speed";

    private static RpgStat rpgStat;

    public Agility(RpgStat rpgStat) {
        Agility.rpgStat = rpgStat;
    }

    public void updateAgility(Player player) {
        String speedKeyName = KeyNameGenerator.getKey(STAT_NAME, SPEED_STAT_NAME);

        float speedStatOption = (float) rpgStat.getConfig().getDouble(speedKeyName);
        float playerSpeed = (Integer) PlayerFile.getPlayerFile(player, SPEED_STAT_NAME);

        float updatedSpeed = playerSpeed + (playerSpeed * speedStatOption);

        player.setWalkSpeed(updatedSpeed);
        player.setFlySpeed(updatedSpeed);

        PlayerFile.setPlayerFile(player, STAT_NAME + "." + SPEED_STAT_NAME, updatedSpeed);
    }

    public void initAgility(Player player) {
        float speed = (float) PlayerFile.getPlayerFile(player, KeyNameGenerator.getKey(STAT_NAME, SPEED_STAT_NAME));

        player.setWalkSpeed(speed);
        player.setFlySpeed(speed);
    }
}
