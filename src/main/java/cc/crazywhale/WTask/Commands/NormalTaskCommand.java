package cc.crazywhale.WTask.Commands;

import cc.crazywhale.WTask.WTask;
import cc.crazywhale.WTask.WTaskAPI;
import cn.nukkit.Player;
import cn.nukkit.command.Command;
import cn.nukkit.command.CommandSender;

import java.util.Map;

/**
 * Created by whale on 2017/7/22.
 */
public class NormalTaskCommand extends Command {

    private WTask plugin;
    private String cmd;
    private WTaskAPI api;

    public NormalTaskCommand(WTask plugin, String c) {
        super(c);
        Map<String, Object> desc = plugin.getCmdInfo("NormalTaskCommand");
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