package cc.crazywhale.WTask;

import cc.crazywhale.WTask.Commands.MainCommand;
import cc.crazywhale.WTask.Commands.NormalTaskCommand;
import cn.nukkit.level.generator.Normal;
import cn.nukkit.plugin.PluginBase;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by whale on 2017/7/22.
 */
public class WTask extends PluginBase {

    public Config setting;
    public WTaskAPI api;
    public Config command;
    public Config msg;
    public Config playerPerm;
    public Config mod;
    public Config daily;
    public Config customCommand;
    private MainCommand mainCommand;
    private NormalTaskCommand normalTaskCommand;
    private String economyType;
    public static WTask obj;

    public Map<String, Map<String, String>> privateTempData;
    public Map<String, String> publicTempData;

    Map<String, Object> taskData;
    Map<String, ArrayList<Map<String, String>>>normalTaskList;

    public void onLoad(){
        obj = this;
        initializeData();
    }

    private void initializeData(){
        privateTempData = new LinkedHashMap<>();
        publicTempData = new LinkedHashMap<>();
        taskData = new LinkedHashMap<>();
        normalTaskList = new LinkedHashMap<>();
    }

    public static WTask getInstance(){
        return obj;
    }

    public void onEnable(){
        File taskPath = new File(getDataFolder(),"tasks/");
        if(!taskPath.exists()){
            boolean r = taskPath.mkdirs();
            if(!r){
                getLogger().warning("文件夹创建异常！");
            }
        }
        this.makeConfig();
        this.registerSettings();
        this.taskData = new LinkedHashMap<>();
        boolean r = this.api.loadTasks();
        if(!r){
            getLogger().critical("任务解析出错！请检查任务内容后重新/wtask reload");
        }
        //saveResource("default.cc");
        if(this.getServer().getPluginManager().getPlugin("EconomyAPI") != null)
        {
            this.economyType = "EconomyAPI";
        }
        else if(this.getServer().getPluginManager().getPlugin("Money") != null)
            this.economyType = "Money";
        else
            this.economyType = "null";
        this.getServer().getLogger().info("成功启动WTask v1.0_alpha for Nukkit！");
    }

    private void makeConfig()
    {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("Config-Version",1);
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
        playerPerm = new Config(getDataFolder().getPath() + "/permissions.yml",Config.YAML);
        saveResource("messages.json");
        msg = new Config(getDataFolder().getPath() + "/messages.json",Config.JSON);
        saveResource("mods.json");
        mod = new Config(getDataFolder().getPath() + "/mods.json",Config.JSON);
        customCommand = new Config(getDataFolder().getPath() + "customCommands.json",Config.JSON);
    }

    private void registerSettings()
    {
        this.api = new WTaskAPI(this);
        Map<String, Object> desc = (Map<String, Object>) this.command.get("MainCommand");
        String maincmd = (String) desc.get("command");
        this.mainCommand = new MainCommand(this,maincmd);
        String normalcmd = (String) ((Map<String, Object>) this.command.get("NormalTaskCommand")).get("command");
        this.normalTaskCommand = new NormalTaskCommand(this,normalcmd);
        this.getServer().getCommandMap().register("WTask", this.mainCommand);
        this.getServer().getCommandMap().register("WTask",this.normalTaskCommand);
    }

    /////////////////////API part
    public Map<String, Object> getCmdInfo(String cmd)
    {
        return (Map<String, Object>) this.command.get(cmd);
    }

    public String getEconomyType() {
        return economyType;
    }

    public static Map<String, String> getStringMap(Object p){
        return (Map<String, String>) p;
    }

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
}
