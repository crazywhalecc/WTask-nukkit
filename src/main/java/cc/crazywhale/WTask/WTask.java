package cc.crazywhale.WTask;

import cc.crazywhale.WTask.Commands.MainCommand;
import cc.crazywhale.WTask.Commands.ActNormalTaskCommand;
import cc.crazywhale.WTask.TaskListener.*;
import cc.crazywhale.WTask.interfaces.TaskListener;
import cc.crazywhale.WTask.tasks.PressureTask;
import cn.nukkit.Player;
import cn.nukkit.plugin.PluginBase;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by whale on 2017/7/22.
 */
public class WTask extends PluginBase{

    public Config setting;
    public WTaskAPI api;
    public Config command;
    public Config msg;
    public Config playerPerm;
    public Config mod;
    public Config daily;
    public Config customCommand;
    private String economyType;
    public static WTask obj;

    public Map<String, Map<String, String>> privateTempData;
    public Map<String, String> publicTempData;
    public Map<String, Config> customConfig;
    final int CONFIG_VERSION = 6;

    Map<String, TaskListener> actTaskListener;

    Map<String, Object> taskData;
    Map<String, ArrayList<Map<String, String>>>normalTaskList;

    public void onLoad(){
        obj = this;
        initializeData();

    }

    private void initializeData(){
        new PressureTask(this);
        privateTempData = new LinkedHashMap<>();
        publicTempData = new LinkedHashMap<>();
        taskData = new LinkedHashMap<>();
        normalTaskList = new LinkedHashMap<>();
        actTaskListener = new LinkedHashMap<>();
    }

    public static WTask getInstance(){
        return obj;
    }

    public void onEnable(){
        File taskPath = new File(getDataFolder(),"tasks/");
        if(!taskPath.exists()){ boolean r = taskPath.mkdirs();if(!r){getLogger().warning("文件夹创建异常！");} }
        this.makeConfig();
        this.registerSettings();
        this.updateData((int) this.setting.get("Config-Version"));
        this.taskData = new LinkedHashMap<>();
        boolean r = this.api.loadTasks();
        if(!r){getLogger().critical("任务解析出错！请检查任务内容后重新/wtask reload");}
        initializeEconomy();
        enableActTasks();
        this.getServer().getLogger().info("成功启动WTask v1.0 for Nukkit！");
    }

    private void updateData(int i){
        if(i < CONFIG_VERSION){
            getServer().getLogger().notice("正在更新配置文件...");
        }
        switch(i){
            case 1:
            case 2:
            case 3:
            case 4:
            case 5:
            case 6:
                break;
        }
    }

    private void makeConfig()
    {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("Config-Version",1);
        map.put("op默认权限",255);
        map.put("玩家默认权限",5);
        map.put("普通任务文件格式","cc");
        map.put("普通任务默认权限",1);
        this.setting = new Config(this.getDataFolder().getPath() + "/config.yml",Config.YAML);
        this.setting.setDefault((LinkedHashMap<String, Object>) map);
        this.setting.save();
        this.saveResource("commands.json");
        this.command = new Config(this.getDataFolder().getPath() + "/commands.json",Config.JSON);
        this.daily = new Config(this.getDataFolder().getPath() + "/Finish.json",Config.JSON);
        if(!daily.exists("普通任务")){
            daily.set("普通任务",new LinkedHashMap<String, Object>());
            daily.save();
        }
        playerPerm = new Config(this.getDataFolder().getPath() + "/permissions.yml",Config.YAML);
        saveResource("messages.json");
        msg = new Config(this.getDataFolder().getPath() + "/messages.json",Config.JSON);
        saveResource("mods.json");
        mod = new Config(this.getDataFolder().getPath() + "/mods.json",Config.JSON);
        customCommand = new Config(this.getDataFolder().getPath() + "/customCommands.json",Config.JSON);

    }

    @SuppressWarnings("unchecked")
    private void registerSettings()
    {
        this.api = new WTaskAPI(this);
        Map<String, Object> desc = (Map<String, Object>) this.command.get("MainCommand");
        MainCommand mainCommand = new MainCommand(this, desc);
        Map<String, Object> normalcmd = (Map<String, Object>) this.command.get("ActNormalTaskCommand");
        ActNormalTaskCommand normalTaskCommand = new ActNormalTaskCommand(this, normalcmd);
        this.getServer().getCommandMap().register("WTask", mainCommand);
        this.getServer().getCommandMap().register("WTask", normalTaskCommand);
    }

    /////////////////////API part
    @SuppressWarnings("unchecked")
    public Map<String, Object> getCmdInfo(String cmd)
    {
        return (Map<String, Object>) this.command.get(cmd);
    }

    public String getEconomyType() {
        return economyType;
    }

    @SuppressWarnings("unchecked")
    public static Map<String, String> getStringMap(Object p){
        return (Map<String, String>) p;
    }

    @SuppressWarnings("unchecked")
    public static Map<String, Object> getObjectMap(Object p){
        return (Map<String, Object>) p;
    }

    String implode(String chars, String[] like){
        StringBuilder aa = new StringBuilder();
        for(String sss : like){
            aa.append(chars).append(sss);
        }
        return aa.toString();
    }

    public boolean getPerm(Player p, String perm){
        int permission = Integer.parseInt(perm);
        if(p.isOp()){
            return permission <= this.setting.getInt("op默认权限");
        }
        if(this.playerPerm.exists(p.getName().toLowerCase())){
            return permission <= this.playerPerm.getInt(p.getName().toLowerCase());
        }
        return permission <= this.setting.getInt("玩家默认权限");
    }

    private void initializeEconomy(){
        if(this.getServer().getPluginManager().getPlugin("EconomyAPI") != null)
        {
            this.economyType = "EconomyAPI";
        }
        else if(this.getServer().getPluginManager().getPlugin("Money") != null)
            this.economyType = "Money";
        else
            this.economyType = "null";
    }

    @SuppressWarnings("unchecked")
    private void enableActTasks(){
        for(Map.Entry<String, Object> entry : taskData.entrySet()){
            Map<String, Object> vcc = (Map<String, Object>) entry.getValue();
            if(vcc.get("type").equals("动作任务")){
                if(vcc.get("actActive").equals("false")){
                    continue;
                }
                String ty = (String) vcc.get("actType");
                switch(ty){
                    case "破坏方块":
                        actTaskListener.put(entry.getKey(),new BlockBreakListener(api,api.prepareTask(entry.getKey()),entry.getKey()));
                        this.getServer().getLogger().notice("成功启动 [ "+ty+" ] 动作任务 "+entry.getKey()+" ！");
                        break;
                    case "放置方块":
                        actTaskListener.put(entry.getKey(),new BlockPlaceListener(api,api.prepareTask(entry.getKey()),entry.getKey()));
                        this.getServer().getLogger().notice("成功启动  [ "+ty+" ] 动作任务 "+entry.getKey()+" !");
                        break;
                    case "玩家攻击玩家":
                    case "玩家攻击":
                        actTaskListener.put(entry.getKey(),new EntityDamageListener(api,api.prepareTask(entry.getKey()),entry.getKey()));
                        this.getServer().getLogger().notice("成功启动  [ "+ty+" ] 动作任务 "+entry.getKey()+" !");
                        break;
                    case "玩家聊天":
                        actTaskListener.put(entry.getKey(),new PlayerChatListener(api,api.prepareTask(entry.getKey()),entry.getKey()));
                        this.getServer().getLogger().notice("成功启动 [ "+ty+" ] 动作任务 "+entry.getKey()+" !");
                        break;
                    case "玩家输入指令":
                        actTaskListener.put(entry.getKey(),new PlayerCommandActivateListener(api,api.prepareTask(entry.getKey()),entry.getKey()));
                        this.getServer().getLogger().notice("成功启动 [ "+ty+" ] 动作任务 "+entry.getKey()+" !");
                        break;
                    default:
                        getServer().getLogger().warning("未知类型的动作任务 "+ty+" ！");
                        break;
                }

            }
        }
    }
}
