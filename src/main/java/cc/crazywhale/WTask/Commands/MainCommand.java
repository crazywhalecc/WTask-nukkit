package cc.crazywhale.WTask.Commands;

import cc.crazywhale.WTask.Config;
import cc.crazywhale.WTask.WTask;
import cn.nukkit.command.Command;
import cn.nukkit.command.CommandSender;

import java.io.File;
import java.util.ArrayList;
import java.util.Map;

/**
 * Created by whale on 2017/7/22.
 */
public class MainCommand extends Command {

    public WTask plugin;
    private String cmd;
    private ArrayList<String> mainHelp = new ArrayList<>();

    public MainCommand(WTask plugin,String c)
    {
        super(c);
        Map<String, Object> desc = plugin.getCmdInfo("MainCommand");
        this.cmd = (String) desc.get("command");

        this.plugin = plugin;
        this.setPermission((String) desc.get("permission"));
        mainHelp.add("§6======WTask主菜单(§a1 §6/ §e2§6)======\n§7*  当前版本: " + plugin.getDescription().getVersion());
        mainHelp.add("§7*  翻页: /" + cmd + " help [页数]");
        mainHelp.add("§e*  输入/"+cmd+" info 来查看当前版本的更新日志哦～");
        mainHelp.add("§a/" + c + " 添加任务: §b添加一个普通任务");
        mainHelp.add("§a/" + c + " reload: §b重载WTask");
        mainHelp.add("§a/" + c + " 创建配置文件: §b创建一个自定义的空白配置文件");
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
                    //BufferedReader r = new BufferedReader(new FileReader(new File(plugin.getDataFolder().getPath() + "default.cc")));
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
                case "reload":
                    plugin.api.loadTasks();
                    plugin.setting.reload();
                    plugin.command.reload();
                    plugin.daily.reload();
                    sender.sendMessage("§a[WTask] 重载完毕！");
                    return true;
                case "exit":
                    this.unregister(this.plugin.getServer().getCommandMap());
                    break;
                case "创建配置文件":
                    if(args.length == 1){
                        sender.sendMessage("§e[用法] " + this.cmd + " 创建配置文件 [文件名]");
                        return true;
                    }
                    String fileName = args[1];
                    File file = new File(this.plugin.getDataFolder().getPath()+"CustomConfig/",fileName);
                    if(file.exists()){
                        sender.sendMessage("§c[WTask] 对不起，这个名字的配置文件已经存在了！");
                        return true;
                    }
                    new Config(this.plugin.getDataFolder().getPath() + "CustomConfig/" + fileName + ".yml",Config.YAML);
                    sender.sendMessage("§a[WTask] 成功创建自定义配置文件！");
                    return true;
                default:
                    this.sendHelp(sender);
                    return true;
            }
        }
        return true;
    }

    private void sendHelp(CommandSender sender)
    {
        for(String help : this.mainHelp)
        {
            sender.sendMessage(help);
        }
    }
}
