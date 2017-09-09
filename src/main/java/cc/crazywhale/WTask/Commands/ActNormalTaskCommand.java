package cc.crazywhale.WTask.Commands;

import cc.crazywhale.WTask.WTask;
import cc.crazywhale.WTask.WTaskAPI;
import cn.nukkit.Player;
import cn.nukkit.command.Command;
import cn.nukkit.command.CommandSender;
import cn.nukkit.command.ConsoleCommandSender;

import java.util.ArrayList;
import java.util.Map;

/**
 * Created by whale on 2017/7/22.
 */
public class ActNormalTaskCommand extends Command {

    private WTask plugin;
    private String cmd;
    private WTaskAPI api;

    public ActNormalTaskCommand(WTask plugin, Map<String, Object> desc) {
        super((String) desc.get("command"),(String) desc.get("desctiption"), null, (String[]) (desc.get("multiple") == null ? new String[0] : ((ArrayList<String>)desc.get("multiple")).toArray(new String[((ArrayList<String>)desc.get("multiple")).size()])));
        this.cmd = (String) desc.get("command");
        this.api = plugin.api;
        this.plugin = plugin;
        this.setPermission((String) desc.get("permission"));
    }

    public boolean execute(CommandSender sender, String ssss, String[] args)
    {
        if(args.length == 0)
        {
            sender.sendMessage("§e用法: /" + this.cmd + " [任务名称]");
            return true;
        }
        else
        {
            String taskname = args[0];
            if(this.plugin.api.isTaskExists(taskname))
            {
                if(sender instanceof ConsoleCommandSender){
                    sender.sendMessage("请在游戏内运行任务！");
                    return true;
                }
                this.api.preNormalTask(taskname,(Player) sender);
                return true;
            }
            else
            {
                sender.sendMessage("§c对不起，任务不存在！");
                return true;
            }
        }
    }
}