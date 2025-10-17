package org.rdyjun.rpgstat;

import org.bukkit.entity.Player;

public abstract class Stat {

    protected final RpgStat rpgStat;

    public Stat(RpgStat rpgStat) {
        this.rpgStat = rpgStat;
    }

    public abstract void increase(Player player);

    public abstract void init(Player player);
}
