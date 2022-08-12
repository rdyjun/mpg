package files;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import rpgstat.RPGSTAT;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.Set;

public class playerFile{
    private RPGSTAT RPGSTAT;
    private File file;
    public playerFile(RPGSTAT RPGSTAT){
        this.RPGSTAT = RPGSTAT;
    }
    public void createFile(Player p){
        File pf = new File(RPGSTAT.getDataFolder(),"playerdata/" + p.getUniqueId() + ".yml");
        if(!existPlayerFile(p)){
            try{
                pf.createNewFile();
            } catch (IOException e){
                e.printStackTrace();
            }
        }
    }
    //플레이어 파일 관련--------------------------------------------------------------
    public Object getPlayerFile(Player p, String s){  //플레이어 파일에서 가져오기
        return gpfc(p).get(p.getUniqueId() + "." + s);
    }
    public void setPlayerFile(Player p, String s, Object a){  //플레이어 파일 수정하기
        gpfc(p).set(p.getUniqueId() + "." + s, a);
        savePlayerFile(p);
    }
    public void setConfigFile(Player p){
        this.file = new File(RPGSTAT.getDataFolder(),"playerdata/" + p.getUniqueId() + ".yml");
    }
    public FileConfiguration gpfc(Player p){
        setConfigFile(p);
        return YamlConfiguration.loadConfiguration(file);
    }
    public Set<String> getPlayerKeys(Player p){  //플레이어 파일 키값 가져오기
        return gpfc(p).getConfigurationSection(String.valueOf(p.getUniqueId())).getKeys(false);
    }
    public boolean existPlayerFile(Player p){  //플레이어 파일 존재여부 확인
        setConfigFile(p);
        return file.exists();
    }
    public void savePlayerFile(Player p){  //플레이어 파일 저장
        setConfigFile(p);
        try {
            gpfc(p).save(file);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}