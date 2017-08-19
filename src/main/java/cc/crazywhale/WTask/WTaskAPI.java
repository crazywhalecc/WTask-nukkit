package cc.crazywhale.WTask;

import cc.crazywhale.WTask.tasks.DelayedTask;
import cn.nukkit.Player;
import cn.nukkit.item.Item;
import cn.nukkit.network.protocol.TextPacket;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by whale on 2017/7/22.
 */
public class WTaskAPI {

    public WTask plugin = null;
    public Map<String, Object> taskLine = new LinkedHashMap<>();

    public WTaskAPI(WTask plugin)
    {
        this.plugin = plugin;
    }

    public boolean addNormalTask(String name)
    {
        Config task = new Config(this.plugin.getDataFolder().getPath() + "/normalTasks/" + name + ".json",Config.JSON);
        Map<String, Object> list = new LinkedHashMap<>();
        list.put("任务线程","<end>");
        task.setAll((LinkedHashMap<String, Object>) list);
        task.save();
        return true;
    }

    public boolean isTaskExists(String taskname)
    {
        String dir = "";
        Map<String, Object> tasks = new LinkedHashMap<>();
        try
        {
            dir = (new File(this.plugin.getDataFolder().getPath())).getCanonicalPath() + "/normalTasks";
        }
        catch(IOException r)
        {
            return false;
        }
        taskname = taskname + ".json";
        File file = new File(dir);
        for(int i = 0;i<file.listFiles().length;i++)
        {
            String filename = (new File(dir)).listFiles()[i].getName();
            if(filename.equals(taskname))
            {
                return true;
            }
        }
        return false;
    }

    public Map<String, Object> getTasks()
    {
        String dir = "";
        Map<String, Object> tasks = new LinkedHashMap<>();
        try
        {
            dir = (new File(this.plugin.getDataFolder().getPath())).getCanonicalPath() + "/normalTasks";
        }
        catch(IOException r)
        {
            return null;
        }
        for(int i = 0;i<(new File(dir)).listFiles().length;i++)
        {
            String filename = (new File(dir)).listFiles()[i].getName();
            String[] listname = filename.split(".");
            tasks.put(filename,this.getTask(listname[0]));
        }
        return tasks;
    }

    public Map<String, Object> getTask(String taskname)
    {
        String dir = "";
        Map<String, Object> tasks = new LinkedHashMap<>();
        try
        {
            dir = (new File(this.plugin.getDataFolder().getPath())).getCanonicalPath() + "/normalTasks";
        }
        catch(IOException r)
        {
            return null;
        }
        taskname = taskname + ".json";
        for(int i = 0;i<(new File(dir)).listFiles().length;i++)
        {
            String filename = (new File(dir)).listFiles()[i].getName();
            if(filename.equals(taskname))
            {
                Config task = new Config(this.plugin.getDataFolder().getPath() + "/normalTasks/" + taskname,Config.JSON);
                return task.getAll();
            }
        }
        return null;
    }

    public void preNormalTask(String taskname, Player p)
    {
        Map<String, Object> taskData = this.getTask(taskname);
        String[] taskline = ((String) taskData.get("任务线程")).split(";");
        Map<Integer, Object> ID = new LinkedHashMap<>();
        for(int i = 0;i< taskline.length;i++)
        {
            String current = this.getF1(taskline[i]);
            String ssd = ".";
            String[] broke = current.split("\\|");
            Map<String, String> taskmap = new LinkedHashMap<>();
            taskmap.put("type",broke[0]);
            String[] brokes = this.array_shift(broke);
            String function = this.implode("|",brokes);
            taskmap.put("function",function);
            ID.put(i,taskmap);
        }
        this.taskLine.put(taskname,ID);
        this.runNormalTask(taskname,p,0);
    }

    public String getF1(String taskline)
    {
        String line = taskline.substring(taskline.indexOf("<")+1,taskline.lastIndexOf(">"));
        return line;
    }

    public String getF2(String taskline)
    {
        String line = taskline.substring(taskline.indexOf("(")+1,taskline.lastIndexOf(")"));
        return line;
    }

    public String[] array_shift(String[] li)
    {
        if(li.length == 1)
        {
            String [] s = new String[0];
            return s;
        }
        ArrayList<String> my = new ArrayList<>();
        for(int sss = 1; sss < li.length;sss++)
        {
            my.add(li[sss]);
        }
        String[] list = my.toArray(new String[0]);
        return list;
    }

    public String implode(String chars, String[] list)
    {
        String finals = "";
        for(int i = 0; i < list.length; i++)
        {
            if(i != (list.length-1))
                finals = finals + list[i] + chars;
            else
                finals = finals + list[i];
        }
        return finals;
    }

    public boolean runNormalTask(String taskname, Player p,int ID)
    {
        NormalTaskAPI t = new NormalTaskAPI(p,this);
        Map<Integer, Object> taskInside = (Map<Integer, Object>) this.taskLine.get(taskname);
        while(taskInside.containsKey(ID))
        {
            Map<String, String> currentMap = (Map<String,String>) taskInside.get(ID);
            switch(currentMap.get("type"))
            {

                case "delay":
                case "延迟":
                    this.plugin.getServer().getScheduler().scheduleDelayedTask(new DelayedTask(this.plugin,p,taskname,ID+1),Integer.parseInt(currentMap.get("function"))*20);
                    return true;
                default:
                    String r = defaultFunction(t,currentMap);
                    switch(r) {
                        case "true":
                            break;
                        case "false":
                            plugin.getLogger().warning("在运行任务 " + taskname + " 时出现了错误！");
                            break;
                        case "end":
                            ID = 10000;
                        default:
                            String[] pp = r.split("-");
                            if (pp.length == 2) {
                                ID = Integer.parseInt(pp[1]) - 2;
                            } else {
                                break;
                            }
                    }
            }
            ID++;
        }
        return true;
    }

    public String defaultFunction(NormalTaskAPI t, Map<String, String> currentMap)
    {
        switch(currentMap.get("type"))
        {
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
                if(t.player == null){
                    return "false";
                }
                switch(curDat[0]){
                    case "允许飞行":
                        t.player.getAdventureSettings().setCanFly(true);
                        return "true";
                    case "取消飞行":
                        t.player.getAdventureSettings().setFlying(false);
                        t.player.getAdventureSettings().setCanFly(false);
                        return "true";
                    case "设置血量":
                    case "sethealth":
                        t.player.setHealth(Float.parseFloat(executeReturnData(curDat[1],t.player)));
                        return "true";
                    case "加血":
                    case "addhealth":
                        t.player.setHealth(t.player.getHealth() + Float.parseFloat(executeReturnData(curDat[1],t.player)));
                        return "true";
                    default:
                        return "false";
                }
            default:
                return "false";
        }
    }

    public String executeReturnData(String line, Player p)
    {
        if(!line.substring(0,1).equals("("))
        {
            return line;
        }
        String m1 = this.getF2(line);
        String[] mtype = m1.split(":");
        String[] templist = this.array_shift(mtype);
        String lis = this.implode(":",templist);
        switch(mtype[0])
        {
            case "玩家":
                if(p == null)
                {
                    return "(error:null_player)";
                }
                String r = this.checkCirculate(lis,p);
                String backString = (r.equals("none") ? lis : r);
                switch(backString)
                {
                    case "名字":
                        return p.getName();
                    case "小写名字":
                        return p.getName().toLowerCase();
                    case "手持":
                        String hand = "";
                        Item item = p.getInventory().getItemInHand();
                        hand = item.getId() + "-" + item.getDamage() + "-" + item.getCount();
                        return hand;
                    case "whale":
                        return "哈哈大帅哥！";
                    case "cc":
                        return "It's Whale's secret.";
                    default:
                        return "";
                }
            case "时间戳":
                return Long.toString(System.currentTimeMillis());
            default:
                return "";
        }
    }

    public String checkCirculate(String line, Player p)
    {
        if(line.substring(0,1).equals("("))
        {
            return this.executeReturnData(line,p);
        }
        else
            return "none";
    }

    public void sendMsgPacket(String msg, Player p, int boy)
    {
        TextPacket pk = new TextPacket();
        pk.message = msg;
        switch(boy)
        {
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
}
