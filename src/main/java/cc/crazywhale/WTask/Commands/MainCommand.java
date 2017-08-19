package cc.crazywhale.WTask.Commands;

import cc.crazywhale.WTask.WTask;
import cn.nukkit.command.Command;
import cn.nukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.Map;

/**
 * Created by whale on 2017/7/22.
 */
public class MainCommand extends Command {

    public WTask plugin;
    public String cmd;
    public ArrayList<String> mainHelp = new ArrayList<>();

    public MainCommand(WTask plugin,String c)
    {
        super(c);
        Map<String, Object> desc = plugin.getCmdInfo("MainCommand");
        this.cmd = (String) desc.get("command");

        this.plugin = plugin;
        this.setPermission((String) desc.get("permission"));
        this.mainHelp.add("§6=====WTask=====");
        this.mainHelp.add("§a/" + c + " 添加任务: §b添加一个普通任务");
    }

    public boolean execute(CommandSender sender, String ssss, String[] args)
    {
        if(args.length == 0)
        {
            this.sendHelp(sender);
            return true;
        }
        else
        {
            switch(args[0])
            {
                case "添加任务":
                    if(args.length == 1)
                    {
                        sender.sendMessage("§e[用法] /" + this.cmd + " 添加任务 [任务名称]");
                        return true;
                    }
                    boolean result = this.plugin.api.addNormalTask(args[1]);
                    if(result)
                    {
                        sender.sendMessage("§a[WTask] 成功创建任务 " + args[1] + "!");
                        return true;
                    }
                    else
                    {
                        sender.sendMessage("§c[WTask] 创建任务 " + args[1] + " 失败！");
                        return true;
                    }
                case "exit":
                    this.unregister(this.plugin.getServer().getCommandMap());
                    break;
                default:
                    this.sendHelp(sender);
                    return true;
            }
        }
        return true;
    }

    public void sendHelp(CommandSender sender)
    {
        for(String help : this.mainHelp)
        {
            sender.sendMessage(help);
        }
    }
}
