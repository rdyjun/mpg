package customitem;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

public final class CustomItem extends JavaPlugin implements Listener {

    @Override
    public void onEnable() {
        Bukkit.getConsoleSender().sendMessage(
                ChatColor.BOLD + "" + ChatColor.GRAY + "[커스텀 아이템]" + ChatColor.RESET + ChatColor.GREEN
                        + "플러그인이 활성화되었습니다");
        Bukkit.getPluginManager().registerEvents(this, this);

    }

    @Override
    public void onDisable() {
        Bukkit.getConsoleSender().sendMessage(
                ChatColor.BOLD + "" + ChatColor.GRAY + "[커스텀 아이템]" + ChatColor.RESET + ChatColor.RED
                        + "플러그인이 비활성화되었습니다");
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        Player p = (Player) sender;
        if (label.equalsIgnoreCase("customitem")) {
            if (args.length != 0) {

            } else {
                p.sendMessage("----------------");
                p.sendMessage("CUSTOM ITEM 명령어");
                p.sendMessage(
                        ChatColor.WHITE + "/customitem create [지정이름] [아이템코드]" + ChatColor.GREEN + "커스텀 아이템을 생성한다. "
                                + ChatColor.GRAY + "(지정이름은 명령어 사용 시 사용할 아이템 이름)");
                p.sendMessage(ChatColor.WHITE + "/customitem remove [지정이름]" + ChatColor.GREEN + "커스텀 아이템을 제거한다. ");
                p.sendMessage(
                        ChatColor.WHITE + "/customitem [지정이름] name [아이템 이름]" + ChatColor.GREEN + "커스텀 아이템의 이름을 지정한다. "
                                + ChatColor.GRAY + "(실제 아이템에 표시될 이름)");
                p.sendMessage(
                        ChatColor.WHITE + "/customitem [지정이름] lore [아이템 설명]" + ChatColor.GREEN + "커스텀 아이템의 설명을 추가한다. "
                                + ChatColor.GRAY + "(한 번에 한 줄씩 입력 가능)");
                p.sendMessage(
                        ChatColor.WHITE + "/customitem [지정이름] rmlore [줄 번호]" + ChatColor.GREEN + "커스텀 아이템의 설명을 제거한다. "
                                + ChatColor.GRAY + "(지정한 줄의 설명 제거)");
                p.sendMessage(ChatColor.WHITE + "/customitem [지정이름] material [아이템 코드]" + ChatColor.GREEN
                        + "커스텀 아이템의 아이템을 지정한다. ");
                p.sendMessage(ChatColor.WHITE + "/customitem [지정이름] enchant [강화수치]" + ChatColor.GREEN
                        + "커스텀 아이템의 강화수치를 지정한다. " + ChatColor.GRAY + "(최대 10강)");
            }
        }
        return false;
    }
}
