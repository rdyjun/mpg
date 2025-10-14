package vitality;

import files.PlayerFile;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Player;

public class Vitality {

    private final PlayerFile playerFile;

    public Vitality(PlayerFile playerFile) {
        this.playerFile = playerFile;
    }

    public void updateVitality(Player player) {
        double vitality = (double) playerFile.getPlayerFile(player, "vitality");
        player.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(player.getHealthScale() + vitality);
    }
}
