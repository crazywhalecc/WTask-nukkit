package cc.crazywhale.WTask;

import cc.crazywhale.WTask.tasks.DelayedTask;
import cn.nukkit.AdventureSettings;
import cn.nukkit.Nukkit;
import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.item.Item;
import cn.nukkit.network.protocol.TextPacket;
import cn.nukkit.utils.TextFormat;
import me.onebone.economyapi.EconomyAPI;
import money.Money;

import java.io.*;
import java.math.BigDecimal;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by whale on 2017/7/22.
 */
public class WTaskAPI{

    public WTask plugin = null;
    Map<String, String> mode = new LinkedHashMap<>();

    public WTaskAPI(WTask plugin) {
        this.plugin = plugin;
    }


    public boolean loadTasks() {
        plugin.taskData.clear();
        File[] files = (new File(plugin.getDataFolder(), "tasks/")).listFiles();
        ArrayList<String> line = new ArrayList<>();
        if (files == null) {
            return false;
        }
        for (File file : files) {
            if (file.isFile()) {
                String qw;
                try {
                    BufferedReader reader = new BufferedReader(new FileReader(file));
                    while ((qw = reader.readLine()) != null) {
                        if (!qw.equals(""))
                            line.add(qw);
                    }
                } catch (Exception e) {
                    return false;
                }
            }
        }
        line.add("&&eof");
        //for(String sss : line){ System.out.println(sss);}
        for (int id = 0; id < line.size(); id++) {
            if (line.get(id).substring(0, 1).equals("[")) {
                String executeMain = line.get(id).substring(1, line.get(id).lastIndexOf("]"));
                //System.out.println(executeMain);
                String[] subMain = executeMain.split(":");
                String taskname = subMain[1];
                switch (subMain[0]) {
                    case "普通任务":
                        Map<String, ArrayList<String>> taskIns = this.subLoadTasks(line, id);
                        if (taskIns == null) {
                            plugin.getLogger().critical("任务解析出错！请检查： " + line.get(id));
                            return false;
                        }
                        Map<String, String> ss = executeFunctions(taskIns.get("function"));
                        Map<String, Object> task = new LinkedHashMap<>();
                        task.putAll(ss);
                        task.put("type", subMain[0]);
                        task.put("taskline", taskIns.get("taskline"));
                        task.put("function", taskIns.get("function"));
                        plugin.taskData.put(taskname, task);
                        break;
                    case "动作任务":
                        taskname = subMain[1];
                        if (subMain.length < 4) {
                            plugin.getLogger().critical("任务解析出错！请检查动作任务是否指定了类型和开服激活设置的填写！");
                            return false;
                        }
                        Map<String, ArrayList<String>> taskIns2 = this.subLoadTasks(line, id);
                        if (taskIns2 == null) {
                            plugin.getLogger().critical("任务解析出错！请检查： " + line.get(id));
                            return false;
                        }
                        Map<String, Object> task2 = new LinkedHashMap<>();
                        task2.put("type", subMain[0]);
                        task2.put("taskline", taskIns2.get("taskline"));
                        task2.put("actType", subMain[2]);
                        task2.put("actActive", (subMain[3].equals("true")));
                        plugin.taskData.put(taskname, task2);
                        break;
                    case "循环任务":
                        taskname = subMain[1];
                        if (subMain.length < 4) {
                            plugin.getLogger().critical("任务解析出错！请检查循环任务是否指定了循环周期和开服激活设置的填写！");
                            return false;
                        }
                        Map<String, ArrayList<String>> taskIns3 = subLoadTasks(line, id);
                        if (taskIns3 == null) {
                            plugin.getLogger().critical("任务解析出错！请检查： " + line.get(id));
                            return false;
                        }
                        Map<String, Object> task3 = new LinkedHashMap<>();
                        task3.put("type", subMain[0]);
                        task3.put("taskline", taskIns3.get("taskline"));
                        task3.put("repeatTime", subMain[2]);
                        task3.put("repeatActive", (subMain[3].equals("true")));
                        plugin.getServer().getLogger().info("写入循环任务：" + taskname);
                        plugin.taskData.put(taskname, task3);
                        break;
                    default:
                        plugin.getLogger().critical("未知类型的任务解析！");
                        return false;
                }
            }
        }
        return true;
    }

    private Map<String, ArrayList<String>> subLoadTasks(ArrayList<String> line, int id) {
        int finalId = -1;
        //System.out.println("ssss");
        ArrayList<String> taskline = new ArrayList<>();
        ArrayList<String> function = new ArrayList<>();
        for (int i = (id + 1); i < line.size(); i++) {
            if (line.get(i).equals("")) {
                continue;
            }
            if (line.get(i).substring(0, 1).equals("[")) {
                finalId = i - 1;
                break;
            } else if (line.get(i).equals("&&eof")) {
                finalId = i - 1;
                break;
            }
        }
        if (finalId == -1) {
            return null;
        }
        for (int s = (id + 1); s <= finalId; s++) {
            if (line.get(s).equals("")) {
                continue;
            }
            if (line.get(s).substring(0, 1).equals("<")) {
                int fpos = line.get(s).lastIndexOf(">");
                if (fpos < 0) {
                    plugin.getLogger().critical("任务解析出错！");
                    return null;
                }
                taskline.add(line.get(s).substring(1, fpos));
            } else if (line.get(s).substring(0, 1).equals("*")) {
                int fpos = line.get(s).lastIndexOf("*");
                if (fpos < 0) {
                    plugin.getLogger().critical("任务解析出错！");
                    return null;
                }
                function.add(line.get(s).substring(1, fpos));
            }
        }
        Map<String, ArrayList<String>> mao = new LinkedHashMap<>();
        mao.put("taskline", taskline);
        mao.put("function", function);
        return mao;
    }

    public boolean addNormalTask(String taskname) {
        if (isTaskExists(taskname)) {
            System.out.println("r任务真的已经存在了");
            return false;
        }
        File path = new File(plugin.getDataFolder(), "tasks/");
        File file = new File(path, taskname + ".cc");
        if (!file.exists()) {
            try {
                try {
                    boolean r = file.createNewFile();
                    if (!r) {
                        System.out.println("新文件创建不了！");
                        plugin.getLogger().warning("创建任务失败！");
                        return false;
                    }
                } catch (IOException es) {
                    System.out.println("无法创建文件！");
                }
                FileOutputStream os = new FileOutputStream(new File(path, taskname + ".cc"));
                os.write(("[普通任务:" + taskname + "]\n<结束>").getBytes());
                loadTasks();
                return true;
            } catch (Exception e) {
                if (e instanceof FileNotFoundException)
                    System.out.println("新建写入任务时候未找到文件！");
                else if (!(e instanceof IOException)) {
                    System.out.println("无法写入文件！");
                }
                plugin.getLogger().warning("创建任务失败！");
            }
        }
        //System.out.println("文件已经存在");
        return false;
    }

    public boolean addRepeatTask(String type, String taskname, String time, String data){
        this.loadTasks();
        if(this.isTaskExists(taskname)) return false;
        File path = new File(plugin.getDataFolder(), "tasks/");
        File file = new File(path, taskname + ".cc");
        if(file.exists()) return false;
        try{
            try{
                boolean r = file.createNewFile();
                if(!r){
                    System.out.println("新文件创建不了！");
                    plugin.getLogger().warning("创建任务失败！");
                    return false;
                }
            } catch (IOException e){
                System.out.println("无法创建文件！");
            }
            FileOutputStream os = new FileOutputStream(new File(path, taskname + ".cc"));
            os.write(("[循环任务:" + taskname + ":" + time + ":" + type + "]\n" + data).getBytes());
            loadTasks();
        } catch (Exception ignored){
            if (ignored instanceof FileNotFoundException)
                System.out.println("新建写入任务时候未找到文件！");
            else if (!(ignored instanceof IOException)) {
                System.out.println("无法写入文件！");
            }
            plugin.getLogger().warning("创建任务失败！");
        }
        return true;
    }

    public boolean addRepeatTask(String type, String taskname, String time){
        return addRepeatTask(type, taskname, time, "<结束>");
    }

    public boolean addActTask(String type, String taskname, String active){
        if (isTaskExists(taskname)) {
            System.out.println("r任务真的已经存在了");
            return false;
        }
        File path = new File(plugin.getDataFolder(), "tasks/");
        File file = new File(path, taskname + ".cc");
        if (!file.exists()) {
            try {
                try {
                    boolean r = file.createNewFile();
                    if (!r) {
                        System.out.println("新文件创建不了！");
                        plugin.getLogger().warning("创建任务失败！");
                        return false;
                    }
                } catch (IOException es) {
                    System.out.println("无法创建文件！");
                }
                FileOutputStream os = new FileOutputStream(new File(path, taskname + ".cc"));
                os.write(("[动作任务:" + taskname + ":"+type+":"+active+"]\n<结束>").getBytes());
                loadTasks();
                return true;
            } catch (Exception e) {
                if (e instanceof FileNotFoundException)
                    System.out.println("新建写入任务时候未找到文件！");
                else if (!(e instanceof IOException)) {
                    System.out.println("无法写入文件！");
                }
                plugin.getLogger().warning("创建任务失败！");
            }
        }
        //System.out.println("文件已经存在");
        return false;
    }

    public boolean isTaskExists(String taskname) {
        Map<String, Object> t = getTaskData(taskname);
        return t != null;
    }


    Map<String, Object> getTaskData(String taskname) {
        if (plugin.taskData.containsKey(taskname)) {
            return (Map<String, Object>) plugin.taskData.get(taskname);
        }
        return null;
    }

    /**
     * @param taskname: haha
     * @return Map
     * @deprecated
     */
    public Map<String, Object> getTask(String taskname) {
        return getTaskData(taskname);
    }

    public void preNormalTask(String taskname, Player p) {
        if (this.getTaskData(taskname).containsKey("权限")) {
            if (!this.plugin.getPerm(p, (String) this.getTaskData(taskname).get("权限"))) {
                p.sendMessage(TextFormat.RED + "对不起，你没有权限运行本功能！");
                return;
            }
        }
        ArrayList<Map<String, String>> lineData = prepareTask(taskname);
        plugin.normalTaskList.put(taskname, lineData);
        boolean r = this.runNormalTask(taskname, p);
        if (!r) {
            p.sendMessage(TextFormat.RED + "任务运行失败！");
        }
    }


    public ArrayList<Map<String, String>> prepareTask(String taskname) {
        Map<String, Object> data = getTaskData(taskname);
        ArrayList<String> task = (ArrayList<String>) data.get("taskline");
        ArrayList<Map<String, String>> ar = new ArrayList<>();
        if (task.size() == 0) {
            Map<String, String> empty = new LinkedHashMap<>();
            empty.put("type", "end");
            empty.put("function", "");
            ar.add(empty);
            return ar;
        }
        for (String taskLine : task) {
            String[] temp = taskLine.split("\\|");
            Map<String, String> full = new LinkedHashMap<>();
            full.put("type", temp[0]);
            ArrayList<String> ss = new ArrayList<>();
            ss.addAll(Arrays.asList(temp).subList(1, temp.length));
            full.put("function", implode("|", ss));
            ar.add(full);
        }
        return ar;
    }

    public String getF1(String taskline) {
        return taskline.substring(taskline.indexOf("<") + 1, taskline.lastIndexOf(">"));
    }

    public String getF2(String taskline) {
        return taskline.substring(taskline.indexOf("(") + 1, taskline.lastIndexOf(")"));
    }

    public String[] array_shift(String[] li) {
        if (li.length == 1) {
            return new String[0];
        }
        ArrayList<String> my = new ArrayList<>();
        my.addAll(Arrays.asList(li).subList(1, li.length));
        return my.toArray(new String[0]);
    }

    public String implode(String chars, ArrayList<String> list) {
        StringBuilder finals = new StringBuilder();
        for (int i = 0; i < list.size(); i++) {
            if (i != (list.size() - 1))
                finals.append(list.get(i)).append(chars);
            else
                finals.append(list.get(i));
        }
        return finals.toString();
    }

    boolean runNormalTask(String taskname, Player p) {
        return runNormalTask(taskname, p, 0, false);
    }

    public boolean runNormalTaskDaily(String taskname, Map<String, Object> data, Player p, boolean keep) {
        if (!data.containsKey("daily-mode")) {
            plugin.getLogger().warning("每日任务模式不存在！");
            return false;
        }
        String[] line = ((String) data.get("daily-mode")).split(";");
        mode.put(taskname, "false");
        Map<String, Object> trueList = plugin.daily.getMap("普通任务");
        if (!trueList.containsKey(taskname)) {
            trueList.put(taskname, new LinkedHashMap<String, Object>());
            plugin.daily.set("普通任务", trueList);
            plugin.daily.save();
        }
        if (!line[0].substring(0, 1).equals("<")) {
            return true;
        }
        ArrayList<Map<String, String>> ar = new ArrayList<>();
        int i = 0;
        while (i < line.length) {
            String aLine = line[i];
            String[] temp = getF1(aLine).split("\\|");
            Map<String, String> map = new LinkedHashMap<>();
            map.put("type", temp[0]);
            ArrayList<String> ss = new ArrayList<>();
            ss.addAll(Arrays.asList(temp).subList(1, temp.length));
            map.put("function", implode("|", ss));
            ar.add(map);
            i++;
        }
        int ID = 0;
        while (ID < ar.size()) {
            switch (ar.get(ID).get("type")) {
                case "setmode":
                    String[] li = ar.get(ID).get("function").split("\\|");
                    switch (li[0]) {
                        case "false":
                            mode.put(taskname, "false");
                            break;
                        case "一次性":
                            mode.put(taskname, "once");
                            break;
                        case "一天多次":
                            mode.put(taskname, "multi-day:" + li[1]);
                            break;
                        case "多天一次":
                            mode.put(taskname, "single-day:" + li[1]);
                            break;
                        case "限定次":
                            mode.put(taskname, "limit-time:" + li[1]);
                            break;
                        default:
                            break;
                    }
                    break;
                default:
                    break;
            }
            ID++;
        }
        return true;
    }

    public boolean setNormalTaskDaily(String tn, Player p) {
        Map<String, Map<String, Object>> map = (Map<String, Map<String, Object>>) this.plugin.daily.get("普通任务");
        Map<String, Object> map2 = map.containsKey(tn) ? map.get(tn) : new LinkedHashMap<String, Object>();
        String name = p.getName().toLowerCase();
        String[] mode = this.mode.get(tn).split(":");
        switch (mode[0]) {
            case "false":
                return true;
            case "once":
                Map<String, Object> data = new LinkedHashMap<>();
                data.put("cid", p.getClientSecret());
                data.put("date", Calendar.getInstance().get(Calendar.DAY_OF_MONTH));
                data.put("times", 1);
                map2.put(name, data);
                break;
            case "multi-day":
                if (map2.containsKey(name)) {
                    if (((Integer) ((Map<String, Object>) map2.get(name)).get("date")) != Calendar.getInstance().get(Calendar.DAY_OF_MONTH)) {
                        Map<String, Object> data2 = new LinkedHashMap<>();
                        data2.put("cid", p.getClientSecret());
                        data2.put("date", Calendar.getInstance().get(Calendar.DAY_OF_MONTH));
                        data2.put("times", 1);
                        map2.put(name, data2);
                    } else {
                        Map<String, Object> data2 = (Map<String, Object>) map2.get(name);
                        data2.put("times", (((Integer) data2.get("times")) + 1));
                        map2.put(name, data2);
                    }
                } else {
                    Map<String, Object> data2 = new LinkedHashMap<>();
                    data2.put("cid", p.getClientSecret());
                    data2.put("date", Calendar.getInstance().get(Calendar.DAY_OF_MONTH));
                    data2.put("times", 1);
                    map2.put(name, data2);
                }
                break;
            case "single-day":
                if (map2.containsKey(name)) {
                    Map<String, Object> data2 = (Map<String, Object>) map2.get(name);
                    data2.put("date", (new Date()).getTime());
                    map2.put(name, data2);
                } else {
                    Map<String, Object> data2 = new LinkedHashMap<>();
                    data2.put("cid", p.getClientSecret());
                    data2.put("date", (new Date()).getTime());
                    data2.put("times", mode[1]);
                    map2.put(name, data2);
                }
                break;
            case "limit-time":
                if (map2.containsKey(name)) {
                    Map<String, Object> data2 = (Map<String, Object>) map2.get(name);
                    data2.put("times", (((Integer) data2.get("times")) + 1));
                    map2.put(name, data2);
                } else {
                    Map<String, Object> data2 = new LinkedHashMap<>();
                    data2.put("cid", p.getClientSecret());
                    data2.put("date", (new Date()).getTime());
                    data2.put("times", 1);
                    map2.put(name, data2);
                }
                break;
        }
        map.put(tn, map2);
        this.plugin.daily.set("普通任务", map);
        this.plugin.daily.save();
        return true;
    }

    public boolean runNormalTask(String taskname, Player p, int ID, boolean delayStep) {
        if (!delayStep) {
            ID = 0;
            Map<String, Object> taskData = getTaskData(taskname);
            if (taskData.containsKey("daily-mode")) {
                if (p == null) {
                    plugin.getLogger().warning("检测到非玩家运行了每日模式任务！任务强行停止！");
                    return false;
                }
                boolean result = runNormalTaskDaily(taskname, taskData, p, false);
                if (!result) {
                    plugin.getLogger().warning("未设置每日模式，无法使用daily-mode！已强制停止任务！");
                    return false;
                }
            }
            System.out.println("检测到玩家：" + p.getName() + "运行了任务： " + taskname);
        }
        NormalTaskAPI t = new NormalTaskAPI(p, this);
        ArrayList<Map<String, String>> taskInside = this.plugin.normalTaskList.get(taskname);
        System.out.println("size: " + taskInside.size());
        while (ID < taskInside.size()) {
            Map<String, String> currentMap = taskInside.get(ID);
            switch (currentMap.get("type")) {
                case "delay":
                case "延迟":
                    this.plugin.getServer().getScheduler().scheduleDelayedTask(new DelayedTask(this.plugin, p, taskname, ID + 1), Integer.parseInt(currentMap.get("function")) * 20);
                    return true;
                case "daily-mode-on":
                    break;
                case "daily-mode-check":
                    String rs = t.checkFinish(currentMap.get("function"), taskname, p);
                    String[] newss = rs.split(":");
                    switch (newss[0]) {
                        case "true":
                            break;
                        case "false":
                            plugin.getLogger().warning("在运行任务：" + taskname + " 第 " + (ID + 1) + " 号时出现了错误！\n错误信息：" + newss[1]);
                            break;
                        case "end":
                            ID = 10000;
                            break;
                        default:
                            String[] pps = newss[0].split("-");
                            if (pps.length == 2) {
                                ID = Integer.parseInt(pps[1]) - 2;
                            }
                    }
                    break;
                case "daily-mode-setfinish":
                    boolean resultf = this.setNormalTaskDaily(taskname, p);
                    if (resultf)
                        break;
                    else {
                        plugin.getLogger().warning("");
                        break;
                    }
                default:
                    String r = defaultFunction(t, currentMap);
                    String[] newr = r.split(":");
                    switch (newr[0]) {
                        case "true":
                            break;
                        case "false":
                            plugin.getLogger().warning("在运行任务：" + taskname + " 第 " + (ID + 1) + " 号时出现了错误！\n错误信息：" + newr[1]);
                            break;
                        case "end":
                            ID = 10000;
                            break;
                        default:
                            String[] pp = newr[0].split("-");
                            if (pp.length == 2) {
                                ID = Integer.parseInt(pp[1]) - 2;
                            } else {
                                break;
                            }
                    }
                    break;
            }
            ID++;
        }
        return true;
    }

    public String defaultFunction(NormalTaskAPI t, Map<String, String> currentMap) {
        switch (currentMap.get("type")) {
            case "消息":
            case "msg":
                return t.sendMessage(currentMap.get("function"));
            case "tip":
            case "提示":
                return t.sendTip(currentMap.get("function"));
            case "底部":
            case "popup":
                return t.sendPopup(currentMap.get("function"));
            case "跳转":
            case "jump":
                return "jump-" + currentMap.get("function");
            case "标题":
            case "title":
                return t.sendTitle(currentMap.get("function"));
            case "消息to":
            case "msgto":
                return t.sendMessageTo(currentMap.get("function"));
            case "tipto":
            case "提示to":
                return t.sendTipTo(currentMap.get("function"));
            case "底部to":
            case "popupto":
                return t.sendPopupTo(currentMap.get("function"));
            case "结束":
            case "end":
                return "end";
            case "写私":
                return t.writePrivateData(currentMap.get("function"));
            case "写公":
                return t.writePublicData(currentMap.get("function"));
            case "传送":
            case "tp":
                return t.teleport(currentMap.get("function"));
            case "加钱":
            case "addmoney":
                return t.addMoney(currentMap.get("function"));
            case "删私":
                return t.deletePrivateData(currentMap.get("function"));
            case "减钱":
            case "reducemoney":
                return t.reduceMoney(currentMap.get("function"));
            case "cmd":
            case "指令":
                return t.runCommand(currentMap.get("function"));
            case "scmd":
            case "控制台指令":
                return t.runConsoleCommand(currentMap.get("function"));
            case "添加物品":
            case "additem":
                return t.addItem(currentMap.get("function"));
            case "玩家动作":
                String[] curDat = currentMap.get("function").split("\\|");
                for (String line : curDat) {
                    System.out.println(line);
                }
                if (t.player == null) {
                    return "false:玩家不存在，无法食用玩家动作！";
                }
                switch (curDat[0]) {
                    case "允许飞行":
                        t.player.getAdventureSettings().set(AdventureSettings.Type.ALLOW_FLIGHT, true);
                        return "true";
                    case "取消飞行":
                        t.player.getAdventureSettings().set(AdventureSettings.Type.FLYING, false);
                        t.player.getAdventureSettings().set(AdventureSettings.Type.ALLOW_FLIGHT, false);
                        return "true";
                    case "设置血量":
                    case "sethealth":
                        t.player.setHealth(Float.parseFloat(executeReturnData(curDat[1], t.player)));
                        return "true";
                    case "加血":
                    case "addhealth":
                        t.player.setHealth(t.player.getHealth() + Float.parseFloat(executeReturnData(curDat[1], t.player)));
                        return "true";
                    case "设置血量上限":
                        t.player.setMaxHealth(strtoint(executeReturnData(curDat[1], t.player)));
                        return "true";
                    case "减血":
                    case "reducehealth":
                        int ori = (new BigDecimal(t.player.getHealth())).intValue();
                        t.player.setHealth(ori - strtoint(executeReturnData(curDat[1], t.player)));
                        return "true";
                    case "设置饥饿":
                        return "false:不通用的方法";
                    case "加经验":
                    case "addexp":
                        t.player.addExperience(strtoint(executeReturnData(curDat[1], t.player)));
                        return "true";
                    case "加经验等级":
                        return "false:nukkit不通用的方法";
                    case "切换创造":
                        return t.player.setGamemode(1, true) ? "true" : "false:切换模式失败！";
                    case "切换生存":
                        return t.player.setGamemode(0, true) ? "true" : "false:切换模式失败！";
                    case "穿鞋":
                        Item item = t.executeItem(executeReturnData(curDat[1], t.player));
                        return t.player.getInventory().setBoots(item) ? "true" : "false:穿鞋失败！";
                    case "穿衣":
                        Item item2 = t.executeItem(executeReturnData(curDat[1], t.player));
                        return t.player.getInventory().setChestplate(item2) ? "true" : "false:穿衣失败！";
                    case "穿裤":
                        Item item3 = t.executeItem(executeReturnData(curDat[1], t.player));
                        return t.player.getInventory().setLeggings(item3) ? "true" : "false:穿裤失败！";
                    case "戴头盔":
                        Item item4 = t.executeItem(executeReturnData(curDat[1], t.player));
                        return t.player.getInventory().setHelmet(item4) ? "true" : "false:戴头盔失败！";
                    case "kick":
                        return t.player.kick() ? "end" : "false:踢出玩家失败！";
                    case "ban":
                        t.player.setBanned(true);
                        return "true";
                    case "皮肤伪装":
                        return t.setCustomSkin(curDat[1]);
                    case "设置大小":
                        t.player.setScale(strtoint(executeReturnData(curDat[1], t.player)));
                        return "true";
                    case "设置权限":
                        if (plugin == null) {
                            return "false:内部错误！";
                        }
                        int perm = strtoint(executeReturnData(curDat[1], t.player));
                        t.setPermission(perm);
                        return "true";
                    default:
                        return "false:未知类型的玩家动作！";
                }
            case "添加效果":
                return t.addEffect(currentMap.get("function"));
            case "声音":
            case "sound":
                return t.makeSound(currentMap.get("function"));
            case "设置名片":
            case "setnametag":
                return t.setNameTag(currentMap.get("function"));
            case "配置文件":
            case "config":
                return t.manageConfig(currentMap.get("function"));
            case "爆炸":
            case "explode":
                return t.makeExplosion(currentMap.get("function"));
            case "检查物品":
            case "checkitem":
                return t.checkInventory(currentMap.get("function"));
            case "检查手持物品":
            case "checkiteminhand":
                return t.checkItemInHand(currentMap.get("function"));
            case "检查金钱":
            case "checkmoney":
                return t.checkMoney(currentMap.get("function"));
            case "比较":
            case "compare":
                return t.checkCount(currentMap.get("function"));
            case "比较浮点数":
            case "comparedouble":
                return t.checkCountDouble(currentMap.get("function"));
            case "检查游戏模式":
            case "checkgm":
                return t.checkGm(currentMap.get("function"));
            case "掉落物品":
            case "dropitem":
                return t.dropItem(currentMap.get("function"));
            case "概率":
                return t.calculatePercentTask(currentMap.get("function"));
            case "block":
            case "方块":
                return "false:方块功能暂时不在nukkit版提供！";
            case "写标签":
                return "false:现已废弃写标签功能！";
            case "缓存":
                return t.manageTemp(currentMap.get("function"));
            case "c":
                return "false:nukkit版本WTask不支持用c功能！";
            case "比较字符串":
                return t.compareText(currentMap.get("function"));
            case "console":
                this.plugin.getServer().getLogger().info(executeReturnData(currentMap.get("function"),t.player));
                return "true";
                //TODO
            default:
                return "false:未知类型的功能！";
        }
    }

    int strtoint(String line) {
        return Integer.parseInt(line);
    }


    public String executeReturnData(String line, Player p) {
        if (!line.substring(0, 1).equals("(")) {
            return line;
        }
        String m1 = this.getF2(line);
        String[] mtype = m1.split(":");
        String type = mtype[0];
        String function = m1.substring(m1.indexOf(":")+1);
        String[] templist = this.array_shift(mtype);
        String lis = function;
        switch (type) {
            case "玩家":
                if (p == null) {
                    return "(error:null_player)";
                }
                String r = this.checkCirculate(lis, p);
                String backString = (r.equals("none") ? lis : r);
                switch (backString) {
                    case "名字":
                        return p.getName();
                    case "小写名字":
                        return p.getName().toLowerCase();
                    case "手持":
                        String hand = "";
                        Item item = p.getInventory().getItemInHand();
                        hand = item.getId() + "-" + item.getDamage() + "-" + item.getCount();
                        return hand;
                    case "鞋子id":
                        return String.valueOf(p.getInventory().getBoots().getId());
                    case "鞋子damage":
                        return String.valueOf(p.getInventory().getBoots().getDamage());
                    case "衣服id":
                        return String.valueOf(p.getInventory().getChestplate().getId());
                    case "衣服damage":
                        return String.valueOf(p.getInventory().getChestplate().getDamage());
                    case "裤子id":
                        return String.valueOf(p.getInventory().getLeggings().getId());
                    case "裤子damage":
                        return String.valueOf(p.getInventory().getLeggings().getDamage());
                    case "头盔id":
                        return String.valueOf(p.getInventory().getHelmet().getId());
                    case "头盔damage":
                        return String.valueOf(p.getInventory().getHelmet().getDamage());
                    case "金钱":
                    case "金钱整数":
                        switch (plugin.getEconomyType()) {
                            case "EconomyAPI":
                                return String.valueOf((new BigDecimal(EconomyAPI.getInstance().myMoney(p))).intValue());
                            case "Money":
                                return String.valueOf((new BigDecimal(Money.getInstance().getMoney(p)).intValue()));
                            default:
                                return "(error:no_economy)";
                        }
                    case "金钱exact":
                        switch (plugin.getEconomyType()) {
                            case "EconomyAPI":
                                return String.valueOf((new BigDecimal(EconomyAPI.getInstance().myMoney(p))).floatValue());
                            case "Money":
                                return String.valueOf((new BigDecimal(Money.getInstance().getMoney(p)).floatValue()));
                            default:
                                return "(error:no_economy)";
                        }
                    case "x":
                        return String.valueOf(p.x);
                    case "y":
                        return String.valueOf(p.y);
                    case "z":
                        return String.valueOf(p.z);
                    case "地图名":
                    case "level":
                    case "world":
                        return p.getLevel().getFolderName();
                    case "与最近玩家的距离":
                        ArrayList<Double> dis = new ArrayList<>();
                        Map<UUID, Player> all = plugin.getServer().getOnlinePlayers();
                        for (Map.Entry<UUID, Player> player : all.entrySet()) {
                            if (p.getName().equals(player.getValue().getName()))
                                continue;
                            if (!p.getLevel().getFolderName().equals(player.getValue().getLevel().getFolderName()))
                                continue;
                            dis.add(p.distance(player.getValue()));
                        }
                        if (dis.size() == 0) {
                            return "无其他玩家";
                        }

                        Comparator c = new Comparator<Double>() {
                            public int compare(Double o1, Double o2) {
                                if (o1 > o2) return 1;
                                else return -1;
                            }
                        };
                        Collections.sort(dis, c);
                        return String.valueOf(dis.get(0));
                    case "坐标":
                        return String.valueOf(new BigDecimal(p.x).intValue()) + ":" + String.valueOf(new BigDecimal(p.y).intValue()) + ":" + String.valueOf(new BigDecimal(p.z).intValue()) + p.level.getFolderName();
                    case "饥饿值":
                        return String.valueOf(p.getFoodData().getLevel());
                    case "血量":
                    case "health":
                        return String.valueOf(p.getHealth());
                    case "最大血量":
                        return String.valueOf(p.getMaxHealth());
                    case "ip":
                        return p.getAddress();
                    case "port":
                        return String.valueOf(p.getPort());
                    case "ip归属地":
                        return "Java版WTask暂不支持ip归属地数据库！";
                    case "whale":
                        return "哈哈大帅哥！";
                    case "cc":
                        return "It's Whale's secret.";
                    default:
                        return "(error:unknown_func)";
                }
            case "物品解析":
                String[] dataIns = lis.split("\\.");
                System.out.println(dataIns[0]+"\n"+dataIns[1]);
                String r2 = checkCirculate(dataIns[1], p);
                String itemdd = (r2.equals("none") ? dataIns[1] : r2);
                String[] itemlist = itemdd.split("-");
                switch (dataIns[0]) {
                    case "物品id":
                        return itemlist[0];
                    case "物品damage":
                    case "物品特殊值":
                        return itemlist[1];
                    case "物品数量":
                        return itemlist[2];
                    case "物品id和特殊值":
                        return itemlist[0] + "-" + itemlist[1];
                    case "物品id和数量":
                        return itemlist[0] + "-" + itemlist[2];
                    case "物品id和特殊值和数量":
                        return itemdd;
                    case "物品名称":
                        return "未检测到安装了中文名称数据库，请先安装数据库后再食用！";
                    default:
                        return "(error:unknown_selection)";
                }
            case "时间戳":
                Date date = new Date();
                int mini = (new BigDecimal(date.getTime())).intValue();
                return String.valueOf(mini);
            case "小时间戳":
                return String.valueOf((new Date()).getTime());
            case "随机数":
                String[] s = lis.split(",");
                String[] s2 = new String[2];
                s2[0] = checkCirculate(s[0], p);
                s[0] = (s2[0].equals("none") ? s[0] : s2[0]);
                s2[1] = checkCirculate(s[1], p);
                s[1] = (s2[1].equals("none") ? s[1] : s2[1]);
                return String.valueOf(getRandomNum(Integer.parseInt(s[0]), Integer.parseInt(s[1])));
            case "计算":
                String posOf = executePlus(lis);
                if (posOf.equals("none")) {
                    return "(error:character_not_exist)";
                }
                String[] lineList = lis.split(posOf);
                String bs1 = checkCirculate(lineList[0], p);
                lineList[0] = (bs1.equals("none") ? lineList[0] : bs1);
                String bs2 = checkCirculate(lineList[1], p);
                lineList[1] = (bs2.equals("none") ? lineList[1] : bs2);
                return this.cal(lineList[0], lineList[1], posOf);
            case "读公":
                String rr = checkCirculate(lis, p);
                lis = (rr.equals("none") ? lis : rr);
                if (plugin.publicTempData.containsKey(lis)) {
                    return plugin.publicTempData.get(lis);
                } else {
                    return "";
                }
            case "读私":
                if (p == null) {
                    return "(error:null_player)";
                }
                String rr2 = checkCirculate(lis, p);
                lis = (rr2.equals("none") ? lis : rr2);
                if (plugin.privateTempData.containsKey(p.getName().toLowerCase())) {
                    if (plugin.privateTempData.get(p.getName().toLowerCase()).containsKey(lis)) {
                        return plugin.privateTempData.get(p.getName().toLowerCase()).get(lis);
                    }
                }
                return "(error:none)";
            case "时":
                return String.valueOf(Calendar.getInstance().get(Calendar.HOUR_OF_DAY));
            case "分":
                return String.valueOf(Calendar.getInstance().get(Calendar.MINUTE));
            case "秒":
                return String.valueOf(Calendar.getInstance().get(Calendar.SECOND));
            case "日":
                return String.valueOf(Calendar.getInstance().get(Calendar.DAY_OF_MONTH));
            case "月":
                return String.valueOf(Calendar.getInstance().get(Calendar.MONTH) + 1);
            case "年":
                return String.valueOf(Calendar.getInstance().get(Calendar.YEAR));
            case "implode":
            case "字符串拼接":
                String[] strList = lis.split(",");
                String[] newList = new String[strList.length];
                for (int i = 0; i < strList.length; i++) {
                    String newr = checkCirculate(strList[i], p);
                    newList[i] = (newr.equals("none") ? strList[i] : newr);
                }
                return plugin.implode("", newList);
            default:
                return "";
        }
    }



    public String checkCirculate(String line, Player p) {
        if (line.substring(0, 1).equals("(")) {
            return this.executeReturnData(line, p);
        } else
            return "none";
    }

    public void sendMsgPacket(String msg, Player p, int boy) {
        TextPacket pk = new TextPacket();
        pk.message = this.msgs(msg, p);
        switch (boy) {
            case 0:
                pk.type = TextPacket.TYPE_RAW;
                break;
            case 1:
                pk.type = TextPacket.TYPE_TIP;
                break;
            case 2:
                pk.type = TextPacket.TYPE_POPUP;
                break;
        }
        p.dataPacket(pk);
    }

    public Map<String, String> executeFunctions(ArrayList<String> functions) {
        Map<String, String> functionList = new LinkedHashMap<>();
        for (String fc : functions) {
            String[] ls = fc.split(":");
            functionList.put(ls[0], ls[1]);
        }
        return functionList;
    }

    public int getRandomNum(int min, int max) {
        Random rdm = new Random();
        return rdm.nextInt(max - min + 1) + min;
    }

    public String executePlus(String origin) {
        if (origin.contains("{+}")) {
            return "{+}";
        } else if (origin.contains("{-}")) {
            return "{-}";
        } else if (origin.contains("{*}")) {
            return "{*}";
        } else if (origin.contains("/")) {
            return "{/}";
        } else {
            return "none";
        }
    }

    public String msgs(String msg, Player p) {
        msg = msg.replace("{tps}", String.valueOf(Server.getInstance().getTicksPerSecondAverage()));
        int uptime = (new BigDecimal((new Date()).getTime() - Nukkit.START_TIME)).intValue();
        uptime = uptime / 60;
        msg = msg.replace("{runtime}", String.valueOf(uptime));
        msg = msg.replace("{load}", String.valueOf(Server.getInstance().getTickUsageAverage()));
        String time = String.valueOf(Calendar.getInstance().get(Calendar.HOUR_OF_DAY)) + ":" + String.valueOf(Calendar.getInstance().get(Calendar.MINUTE)) + ":" + String.valueOf(Calendar.getInstance().get(Calendar.SECOND));
        msg = msg.replace("{time}", time);
        msg = msg.replace("{online}", String.valueOf(Server.getInstance().getOnlinePlayers().size()));
        if (p != null) {
            switch (plugin.getEconomyType()) {
                case "EconomyAPI":
                    msg = msg.replace("{money}", String.valueOf(EconomyAPI.getInstance().myMoney(p)));
                    break;
                case "Money":
                    msg = msg.replace("{money}", String.valueOf(Money.getInstance().getMoney(p)));
                    break;
                default:
                    break;
            }
            Item item = p.getInventory().getItemInHand();
            msg = msg.replace("{itemid}", String.valueOf(item.getId()));
            msg = msg.replace("{itemdamage}", String.valueOf(item.getDamage()));
            msg = msg.replace("%p", p.getName());
            msg = msg.replace("{name}", p.getName());
            msg = msg.replace("{hp}", String.valueOf(p.getHealth()));
            msg = msg.replace("{mhp}", String.valueOf(p.getMaxHealth()));
            msg = msg.replace("{level}", p.getLevel().getFolderName());
            msg = msg.replace("{food}", String.valueOf(p.getFoodData().getLevel()));
            msg = msg.replace("{ip}", p.getAddress());
            msg = msg.replace("{port}", String.valueOf(p.getPort()));
            String x = String.valueOf((new BigDecimal(p.x)).intValue());
            String y = String.valueOf((new BigDecimal(p.y)).intValue());
            String z = String.valueOf((new BigDecimal(p.z)).intValue());
            msg = msg.replace("{x}", x);
            msg = msg.replace("{y}", y);
            msg = msg.replace("{z}", z);
        }
        msg = msg.replace("%n", "\n");
        msg = msg.replace("+", " ");
        return msg;
    }

    public String cal(String mins, String mins2, String fuhao) {
        switch (fuhao) {
            case "{+}":
                return String.valueOf(Float.parseFloat(mins) + Float.parseFloat(mins2));
            case "{-}":
                return String.valueOf(Float.parseFloat(mins) - Float.parseFloat(mins2));
            case "{*}":
                return String.valueOf(Float.parseFloat(mins) * Float.parseFloat(mins2));
            case "{/}":
                return String.valueOf(Float.parseFloat(mins) / Float.parseFloat(mins2));
            default:
                return "0";
        }
    }

    public String executeCompare(String it) {
        if(it.contains("{大于}")){
            return "{大于}";
        }
        else if(it.contains("{小于}")){
            return "{小于}";
        }
        else if(it.contains("{等于}")){
            return "{等于}";
        }
        else if(it.contains("{大于等于}")){
            return "{大于等于}";
        }
        else if(it.contains("{小于等于}")){
            return "{小于等于}";
        }
        else if(it.contains("{比较字符串}") || it.contains("{str_compare}")){
            return "{比较字符串}";
        }
        else{
            return "";
        }
    }

    public boolean calCompare(String[] number, String fuhao){
        return calCompare(number,fuhao,false);
    }

    public boolean calCompare(String[] number, String fuhao, boolean containDouble){
        if(containDouble){
            switch(fuhao){
                case "{大于}":
                    return Double.parseDouble(number[0]) > Double.parseDouble(number[1]);
                case "{小于}":
                    return Double.parseDouble(number[0]) < Double.parseDouble(number[1]);
                case "{大于等于}":
                    return Double.parseDouble(number[0]) >= Double.parseDouble(number[1]);
                case "{小于等于}":
                    return Double.parseDouble(number[0]) <= Double.parseDouble(number[1]);
                case "{等于}":
                    return Double.parseDouble(number[0]) == Double.parseDouble(number[1]);
            }
        }
        else {
            switch (fuhao) {
                case "{大于}":
                    return Integer.parseInt(number[0]) > Integer.parseInt(number[1]);
                case "{大于等于}":
                    return Integer.parseInt(number[0]) >= Integer.parseInt(number[1]);
                case "{小于}":
                    return Integer.parseInt(number[0]) < Integer.parseInt(number[1]);
                case "{小于等于}":
                    return Integer.parseInt(number[0]) <= Integer.parseInt(number[1]);
                case "{等于}":
                    if (isDigit2(number[0]) && isDigit2(number[1])) {
                        return Integer.parseInt(number[0]) == Integer.parseInt(number[1]);
                    } else {
                        return number[0].equals(number[1]);
                    }
            }
        }
        return false;
    }

    public boolean isDigit2(String strNum) {
        Pattern pattern = Pattern.compile("[0-9]+");
        Matcher matcher = pattern.matcher(strNum);
        return matcher.matches();
    }
}