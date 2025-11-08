package org.rdyjun.reset;

import java.io.File;
import java.io.IOException;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.rdyjun.componentgenerator.ComponentGenerator;
import org.rdyjun.files.PlayerFile;
import org.rdyjun.rpgstat.ClickItem;
import org.rdyjun.rpgstat.RpgStat;

public class Reset implements ClickItem {
    private final RpgStat rpgStat;

    public Reset(RpgStat rpgStat) {
        this.rpgStat = rpgStat;
    }

    public void onClick(Player player) {
        reset(player);
    }

    public void reset(Player player) {
        if (!PlayerFile.existPlayerFile(player)) {
            System.out.println("플레이어 파일이 존재하지 않습니다.");
            return;
        }

        File file = PlayerFile.getFile(player);
        FileConfiguration config = PlayerFile.getConfig(player);

        int statPoint = 0;
        //스텟 초기화
        for (String stat : PlayerFile.getPlayerKeys(player)) {
            int statLevel = (Integer) PlayerFile.getPlayerFile(player, stat);
            config.set(player.getUniqueId() + "." + stat, 0);
            statPoint += statLevel;
        }

        config.set(player.getUniqueId() + ".statpoint", statPoint);

        try {
            config.save(file);
        } catch (IOException e) {
            System.out.println("플레이어 파일 저장 중 오류가 발생했습니다: " + e.getMessage());
            return;
        }

        int resetPrice = rpgStat.getConfig()
                .getInt("info.reset.price");

        RpgStat.getEconomy().withdrawPlayer(player, resetPrice);
        player.sendMessage(rpgStat.messageHead()
                .append(
                        ComponentGenerator.text("[" + player.getName() + "] 님의 레벨(스텟)을 초기화시켰습니다 !",
                                NamedTextColor.WHITE)));

        rpgStat.statInitialize(player);
    }
}
