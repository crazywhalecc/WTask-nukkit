package cc.crazywhale.WTask.Commands;

import cc.crazywhale.WTask.NormalTaskAPI;
import cc.crazywhale.WTask.WTask;
import cn.nukkit.Player;
import cn.nukkit.command.Command;
import cn.nukkit.command.CommandSender;
import cn.nukkit.command.ConsoleCommandSender;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Map;

public class CustomCommand extends Command {
    WTask plugin;
    String cmd;
    Map<String, Object> setting;
    String defaultHelp;
    Map<String, Object> desc;

    @SuppressWarnings("unchecked")
    public CustomCommand(WTask plugin, Map<String, Object> desc) {
        super((String) desc.get("command"), (String) desc.get("desctiption"), null, (String[]) (desc.get("multiple") == null ? new String[0] : desc.get("multiple")));
        plugin.getServer().getLogger().info("正在注册自定义指令：" + desc.get("command"));
        this.setPermission((desc.get("permission")).equals("op") ? "wtask.command.op" : "wtask.command.player");
        this.plugin = plugin;
        this.cmd = (String) desc.get("command");
        this.setting = (Map<String, Object>) desc.get("setting");
        this.defaultHelp = (String) desc.get("default");
        this.desc = desc;
    }

    @Override
    public boolean execute(CommandSender sender, String s, String[] args) {
        if (args.length >= 1) {
            if (setting.containsKey(args[0])) {
                int i = 1;
                ArrayList<String> data = new ArrayList<>();
                for (; i < args.length; i++) {
                    data.add(args[1]);
                }
                if (!data.isEmpty()) {
                    if (desc.containsKey("cover")) {
                        if (((boolean) desc.get("cover"))) {
                            NormalTaskAPI t = new NormalTaskAPI((Player) sender, this.plugin.api);
                            for (int p = 0; p < data.size(); p++) {
                                t.writePrivateData(cmd + (new BigDecimal(p)).toString() + "\\|" + data.get(p));
                            }
                        }
                    }
                }
                if (((String) setting.get(args[0])).substring(0, 1).equals("/")) {
                    this.plugin.getServer().dispatchCommand(sender, ((String) setting.get(args[0])).substring(1).replaceAll("%p", sender.getName()));
                } else if (((String) setting.get(args[0])).substring(0, 1).equals("*")) {
                    this.plugin.api.preNormalTask(((String) setting.get(args[0])).substring(1), (Player) sender);
                    return true;
                } else {
                    sender.sendMessage(this.plugin.api.msgs((String) setting.get(args[0]), (Player) sender));
                }
                return true;
            } else {
                sender.sendMessage(this.plugin.api.msgs(defaultHelp, (Player) sender));
                return true;
            }
        } else {
            switch (defaultHelp.substring(0, 1)) {
                case "/":
                    this.plugin.getServer().dispatchCommand(sender, defaultHelp.substring(1).replaceAll("%p", sender.getName()));
                    break;
                case "*":
                    this.plugin.api.preNormalTask(defaultHelp.substring(1), (Player) sender);
                    break;
                default:
                    if(sender instanceof ConsoleCommandSender) sender.sendMessage(defaultHelp);
                    else if(sender instanceof Player)
                        sender.sendMessage(this.plugin.api.msgs(defaultHelp, (Player) sender));
                    break;
            }
            return true;
        }
    }
}
