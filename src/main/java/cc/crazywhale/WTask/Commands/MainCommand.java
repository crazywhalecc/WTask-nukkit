package cc.crazywhale.WTask.Commands;

import cc.crazywhale.WTask.WTask;
import cn.nukkit.command.Command;
import cn.nukkit.command.CommandSender;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
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
        mainHelp.add("§6======WTask主菜单(§a1 §6/ §e2§6)======\n§7*  当前版本: " + plugin.getDescription().getVersion());
        mainHelp.add("§7*  翻页: /" + cmd + " help [页数]");
        mainHelp.add("§e*  输入/"+cmd+" info 来查看当前版本的更新日志哦～");
        mainHelp.add("§a/" + c + " 添加任务: §b添加一个普通任务");
        mainHelp.add("§a/" + c + " reload: §b重载WTask");

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
                case "ss":
                    try{
                        File ss = new File(this.plugin.getDataFolder(), "default.cc");
                        BufferedReader r = new BufferedReader(new FileReader(ss));
                        String line;
                        ArrayList<String> sLine = new ArrayList<>();
                        while((line = r.readLine()) != null){
                            sLine.add(line);
                            sender.sendMessage(line);
                        }
                    }
                    catch(Exception e){
                        if(e instanceof FileNotFoundException)
                            sender.sendMessage("文件不存在！");
                        else
                            sender.sendMessage("读取错误！");
                    }
                    break;
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
