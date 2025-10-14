package files;

import java.io.File;
import java.io.IOException;
import java.util.Set;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import rpgstat.RpgStat;

public class PlayerFile {

    private final RpgStat rpgStat;
    private File file;
    private FileConfiguration config;

    public PlayerFile(RpgStat rpgStat) {
        this.rpgStat = rpgStat;
    }

    public void createFile(Player p) {
        fileGet(p);
        configGet(p);

        if (existPlayerFile(p)) {
            return;
        }

        try {
            file.createNewFile();

            for (String s : RPGSTAT.getConfig().getConfigurationSection("stats").getKeys(false)) {
                setPlayerFile(p, s, 0);
            }
            setPlayerFile(p, "statpoint", 0);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //플레이어 파일 관련--------------------------------------------------------------
    //플레이어 파일에서 가져오기
    public Object getPlayerFile(Player p, String s) {  //플레이어 파일에서 가져오기
        configGet(p);

        return config.get(p.getUniqueId() + "." + s);
    }

    public void setPlayerFile(Player p, String s, Object a) {  //플레이어 파일 수정하기
        configGet(p);

        config.set(p.getUniqueId() + "." + s, a);
        savePlayerFile(p);
    }

    public Set<String> getPlayerKeys(Player p) {  //플레이어 파일 키값 가져오기
        configGet(p);

        return config.getConfigurationSection(String.valueOf(p.getUniqueId())).getKeys(false);
    }

    public boolean existPlayerFile(Player p) {  //플레이어 파일 존재여부 확인
        fileGet(p);

        return file.exists();
    }

    public void savePlayerFile(Player p) {  //플레이어 파일 저장
        try {
            config.save(file);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void fileGet(Player p) {
        this.file = new File(RPGSTAT.getDataFolder(), "playerdata/" + p.getUniqueId() + ".yml");
    }

    public void configGet(Player p) {
        fileGet(p);
        this.config = YamlConfiguration.loadConfiguration(file);
    }
}