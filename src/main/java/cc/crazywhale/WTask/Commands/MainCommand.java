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
    private ArrayList<String> mainHelp2 = new ArrayList<>();

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
        mainHelp.add("§a/" + c + " info: §b关于WTask插件及版权");
        mainHelp2.add("§6=====WTask主菜单(§a2 §6/ §e2§6)=====");
        mainHelp2.add("§a/" + c + " 权限: §b设置玩家权限");
    }

    public boolean execute(CommandSender sender, String ssss, String[] args)
    {
        if(args.length == 0)
        {
            sender.sendMessage("§c[WTask] 请输入 /" + cmd + " help [页数]");
            return true;
        }
        else
        {
            switch(args[0])
            {
                case "help":
                    if(args.length == 1){
                        sendHelp(sender,1);
                        return true;
                    }
                    else{
                        if(Integer.parseInt(args[1]) > 2){
                            sender.sendMessage("§e[WTask] 对不起，页码不存在！");
                            return true;
                        }
                        sendHelp(sender,Integer.parseInt(args[1]));
                        return true;
                    }
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
                case "info":
                    sender.sendMessage("§6===============\n§b*    WTask-nukkit    *\n§eTwitter: @BlockForWhale\n§a鲸鱼QQ: 627577391\n§d插件页面: pl.zxda.net/plugins/532.html\n§6===============");
                    return true;
                case "权限":
                    if(args.length <= 2){
                        sender.sendMessage("§e[用法] /"+cmd+" 权限 [玩家ID] [目标权限]");
                        return true;
                    }
                    args[1] = args[1].toLowerCase();
                    plugin.playerPerm.set(args[1],Integer.parseInt(args[2]));
                    plugin.playerPerm.save();
                    sender.sendMessage("§a[WTask] 成功设置玩家 "+args[1]+" 的权限为" + args[2]+"!");
                    return true;
                default:
                    sender.sendMessage("§c[WTask] 输入指令错误！请输入 /"+cmd+" help [页数");
                    return true;
            }
        }
        return true;
    }

    private void sendHelp(CommandSender sender, int page)
    {
        if(page == 1){
            for(String help : this.mainHelp)
            {
                sender.sendMessage(help);
            }
        }
        else if(page == 2){
            for(String help2 : this.mainHelp2){
                sender.sendMessage(help2);
            }
        }
    }
}
