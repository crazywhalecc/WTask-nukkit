package cc.crazywhale.WTask.Commands;

import cc.crazywhale.WTask.Config;
import cc.crazywhale.WTask.WTask;
import cc.crazywhale.WTask.WTaskAPI;
import cn.nukkit.command.Command;
import cn.nukkit.command.CommandSender;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by whale on 2017/7/22.
 */
public class MainCommand extends Command {

    public WTask plugin;
    private String cmd;
    private ArrayList<String> mainHelp = new ArrayList<>();
    private ArrayList<String> mainHelp2 = new ArrayList<>();
    private WTaskAPI api;

    public MainCommand(WTask plugin, Map<String, Object> desc) {
        super((String) desc.get("command"), (String) desc.get("desctiption"), null, (String[]) (desc.get("multiple") == null ? new String[0] : desc.get("multiple")));
        this.cmd = (String) desc.get("command");
        this.plugin = plugin;
        this.api = plugin.api;
        String c = cmd;
        mainHelp.add("§6======WTask主菜单(§a1 §6/ §e2§6)======\n§7*  当前版本: " + plugin.getDescription().getVersion());
        mainHelp.add("§7*  翻页: /" + cmd + " help [页数]");
        mainHelp.add("§e*  输入/" + cmd + " info 来查看当前版本的更新日志哦～");
        mainHelp.add("§a/" + c + " 添加任务: §b添加一个普通任务");
        mainHelp.add("§a/" + c + " reload: §b重载WTask");
        mainHelp.add("§a/" + c + " 创建配置文件: §b创建一个自定义的空白配置文件");
        mainHelp.add("§a/" + c + " info: §b关于WTask插件及版权");
        mainHelp2.add("§6=====WTask主菜单(§a2 §6/ §e2§6)=====");
        mainHelp2.add("§a/" + c + " 权限: §b设置玩家权限");
        mainHelp2.add("§a/" + c + " 添加动作任务: §b添加一个动作类型的任务");
        mainHelp2.add("§a/" + c + " 创建指令: §b创建一个自定义指令");
        mainHelp2.add("§a/" + c + " 设置自定义指令: §b设置一个自定义指令");
        mainHelp2.add("§a/" + c + " 添加循环任务: §b添加一个循环型任务");
    }

    @SuppressWarnings("unchecked")
    public boolean execute(CommandSender sender, String ssss, String[] args) {
        if (args.length == 0) {
            sender.sendMessage("§c[WTask] 请输入 /" + cmd + " help [页数]");
            return true;
        } else switch (args[0]) {
            case "help":
                if (args.length == 1) {
                    sendHelp(sender, 1);
                    return true;
                } else {
                    if (Integer.parseInt(args[1]) > 2) {
                        sender.sendMessage("§e[WTask] 对不起，页码不存在！");
                        return true;
                    }
                    sendHelp(sender, Integer.parseInt(args[1]));
                    return true;
                }
                //BufferedReader r = new BufferedReader(new FileReader(new File(plugin.getDataFolder().getPath() + "default.cc")));
            case "添加任务":
                if (args.length == 1) {
                    sender.sendMessage("§e[用法] /" + this.cmd + " 添加任务 [任务名称]");
                    return true;
                }
                boolean result = this.plugin.api.addNormalTask(args[1]);
                if (result) {
                    sender.sendMessage("§a[WTask] 成功创建任务 " + args[1] + "!");
                    return true;
                } else {
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
                if (args.length == 1) {
                    sender.sendMessage("§e[用法] " + this.cmd + " 创建配置文件 [文件名]");
                    return true;
                }
                String fileName = args[1];
                File file = new File(this.plugin.getDataFolder().getPath() + "CustomConfig/", fileName);
                if (file.exists()) {
                    sender.sendMessage("§c[WTask] 对不起，这个名字的配置文件已经存在了！");
                    return true;
                }
                new Config(this.plugin.getDataFolder().getPath() + "CustomConfig/" + fileName + ".yml", Config.YAML);
                sender.sendMessage("§a[WTask] 成功创建自定义配置文件！");
                return true;
            case "info":
                sender.sendMessage("§6===============\n§b*    WTask-nukkit    *\n§eTwitter: @BlockForWhale\n§a鲸鱼QQ: 627577391\n§d插件页面: pl.zxda.net/plugins/532.html\n§6===============");
                return true;
            case "权限":
                if (args.length <= 2) {
                    sender.sendMessage("§e[用法] /" + cmd + " 权限 [玩家ID] [目标权限]");
                    return true;
                }
                args[1] = args[1].toLowerCase();
                plugin.playerPerm.set(args[1], Integer.parseInt(args[2]));
                plugin.playerPerm.save();
                sender.sendMessage("§a[WTask] 成功设置玩家 " + args[1] + " 的权限为" + args[2] + "!");
                return true;
            case "添加动作任务":
                if (args.length == 1) {
                    sender.sendMessage("§e[用法] /" + cmd + " 添加动作任务 [动作类型] [任务名称]");
                    sender.sendMessage("§d当前支持的动作类型有: \n§b破坏方块, 放置方块, 玩家点击, 玩家死亡, 玩家丢弃物品, 玩家输入指令, 玩家聊天, 玩家传送, 玩家攻击玩家, 玩家加入");
                    return true;
                } else if (args.length == 2) {
                    sender.sendMessage("§e[用法] /" + cmd + " 添加动作任务 " + args[1] + " [任务名称]");
                    return true;
                } else {
                    switch (args[1]) {
                        case "破坏方块":
                        case "放置方块":
                        case "玩家点击":
                        case "玩家死亡":
                        case "玩家丢弃物品":
                        case "玩家输入指令":
                        case "玩家聊天":
                        case "玩家传送":
                        case "玩家攻击玩家":
                        case "玩家加入":
                            String tn = args[2];
                            boolean results = plugin.api.addActTask(args[1], tn, "true");
                            if (!results) {
                                sender.sendMessage("§c[WTask] 对不起，这个名字的任务已经存在了！");
                                return true;
                            }
                            sender.sendMessage("§a[WTask] 成功创建动作任务 " + tn + "，动作类型是 " + args[1] + "!");
                            return true;
                        default:
                            sender.sendMessage("§c动作类型出错！不支持的动作类型！");
                            return true;
                    }
                }
            case "创建指令":
                if (args.length < 5) {
                    sender.sendMessage("§e用法: /" + this.cmd + " [主指令] [指令描述] [指令权限] [默认提示] （这里指令均无需斜杠）");
                    sender.sendMessage("§e权限： true为全体玩家，op为仅op使用，默认提示可以直接写要运行的普通任务例如： *礼包，就是前面加个*就可以了");
                    return true;
                }
                String mainCommand = args[1];
                LinkedHashMap<String, Object> data = new LinkedHashMap<>();
                data.put("command", mainCommand);
                data.put("description", args[2]);
                data.put("permission", args[3]);
                data.put("default", args[4]);
                data.put("cover", true);
                data.put("setting", new ArrayList<>());
                this.plugin.getCustomCommand().set(mainCommand, data);
                this.plugin.getCustomCommand().save();
                sender.sendMessage("§a[WTask] 成功创建自定义指令 " + mainCommand + "!");
                return true;
            case "设置自定义指令":
                if (args.length >= 2) {
                    String command = args[1];
                    if (this.plugin.getCustomCommand().exists(command)) {
                        if (args.length >= 3) {
                            switch (args[2]) {
                                case "添加副指令":
                                    if (args.length >= 5) {
                                        String subName = args[3];
                                        String inside = args[4];
                                        Map<String, Object> d = (Map<String, Object>) this.plugin.getCustomCommand().get(command);
                                        Map<String, Object> s = (Map<String, Object>) d.get("setting");
                                        s.put(subName, inside);
                                        d.put("setting", s);
                                        this.plugin.getCustomCommand().set(command, d);
                                        this.plugin.getCustomCommand().save();
                                        sender.sendMessage("§a[WTask] 成功添加自定义副指令！");
                                        return true;
                                    } else {
                                        sender.sendMessage("§e[用法] " + this.cmd + " " + args[0] + " " + args[1] + " " + args[2]
                                                + " [副指令(无需斜杠)] [内容]"
                                                + "\n§b*小提示：如果内容中最前面的是斜杠/，那么在运行这个副指令时候就会被识别为指令，通过这个副指令可以运行其他指令哦"
                                                + "\n§b*小提示2：如果输入*xxx，那么就会被识别为是运行WTask任务"
                                                + "\n§b*小提示3：如果直接输入文字，那么就会直接显示信息哦"
                                                + "\n§b*小提示4：这里所有的人的名字均可用 %p 来代替"
                                        );
                                        return true;
                                    }
                                case "删除副指令":
                                    if (args.length >= 4) {
                                        Map<String, Object> d = (Map<String, Object>) this.plugin.getCustomCommand().get(command);
                                        Map<String, Object> s = (Map<String, Object>) d.get("setting");
                                        if (s.containsKey(args[3])) {
                                            s.remove(args[3]);
                                            d.put("setting", s);
                                            this.plugin.getCustomCommand().set(command, d);
                                            this.plugin.getCustomCommand().save();
                                            sender.sendMessage("§a[WTask] 成功删除副指令 " + args[3]);
                                            return true;
                                        } else {
                                            sender.sendMessage("§c对不起。该副指令不存在！");
                                            return true;
                                        }
                                    } else {
                                        sender.sendMessage("用法 /" + cmd + " " + args[0] + " " + args[1] + " " + args[2] + " [副指令(无需斜杠)]");
                                        return true;
                                    }
                                case "设置默认提示":
                                    if (args.length >= 4) {
                                        String tip = args[3];
                                        Map<String, Object> d = (Map<String, Object>) this.plugin.getCustomCommand().get(command);
                                        d.put("default", tip);
                                        this.plugin.getCustomCommand().set(command, d);
                                        this.plugin.getCustomCommand().save();
                                        sender.sendMessage("§a[WTask] 成功设置默认提示！");
                                        return true;
                                    } else {
                                        sender.sendMessage("用法 /" + cmd + " " + args[0] + " " + args[1] + " " + args[2] + " [提示内容]");
                                        return true;
                                    }
                                default:
                                    sender.sendMessage("用法错误！");
                                    return true;
                            }
                        } else {
                            sender.sendMessage("§6=====自定义指令设置======"
                                    + "\n§a/" + cmd + " 设置自定义指令 " + args[1] + " 添加副指令: §b添加一个副指令"
                                    + "\n§a/" + cmd + " 设置自定义指令 " + args[1] + " 删除副指令: §b删除一个副指令"
                                    + "\n§a/" + cmd + " 设置自定义指令 " + args[1] + " 设置默认提示: §b设置这个指令的默认提示"
                            );
                            return true;
                        }
                    } else {
                        sender.sendMessage("§c对不起，这个指令不存在！");
                        return true;
                    }
                } else {
                    sender.sendMessage("§e[用法] /" + cmd + " 设置自定义指令 [主指令]");
                    return true;
                }
            case "添加循环任务":
                if (args.length >= 2) {
                    String name = args[1];
                    if (this.plugin.api.isTaskExists(name)) {
                        sender.sendMessage("§c[WTask] 对不起，这个名字的任务已经存在了！");
                        return true;
                    }
                    if (this.api.addRepeatTask("true", name, "1"))
                        sender.sendMessage("§a[WTask] 成功创建循环任务 " + name + " ! 请到tasks文件夹找到任务配置任务！");
                    else sender.sendMessage("§c[WTask] 创建循环任务失败！");
                    return true;
                } else {
                    sender.sendMessage("§e[WTask] 用法: /" + cmd + " 添加循环任务 <任务名称>");
                    return true;
                }
            default:
                sender.sendMessage("§c[WTask] 输入指令错误！请输入 /" + cmd + " help [页数");
                return true;
        }
        return true;
    }

    private void sendHelp(CommandSender sender, int page) {
        if (page == 1) {
            for (String help : this.mainHelp) {
                sender.sendMessage(help);
            }
        } else if (page == 2) {
            for (String help2 : this.mainHelp2) {
                sender.sendMessage(help2);
            }
        }
    }
}
