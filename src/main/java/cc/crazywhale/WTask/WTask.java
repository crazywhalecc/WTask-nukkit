package cc.crazywhale.WTask;

import cc.crazywhale.WTask.Commands.MainCommand;
import cc.crazywhale.WTask.Commands.NormalTaskCommand;
import cn.nukkit.level.generator.Normal;
import cn.nukkit.plugin.PluginBase;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by whale on 2017/7/22.
 */
public class WTask extends PluginBase {

    private Config setting;
    public WTaskAPI api;
    private Config command;
    private MainCommand mainCommand;
    private NormalTaskCommand normalTaskCommand;
    private String economyType;
    public static WTask obj;

    public Map<String, Object> privateTempData;
    public Map<String, String> publicTempData;

    Map<String, Object> taskData;

    public void onLoad(){
        obj = this;
    }

    public static WTask getInstance(){
        return obj;
    }

    public void onEnable(){

        this.makeConfig();
        this.registerSettings();
        this.taskData = new LinkedHashMap<>();
        this.api.loadTasks();
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
        this.setting = new Config(this.getDataFolder().getPath() + "/setting.json",Config.JSON);
        this.setting.setDefault((LinkedHashMap<String, Object>) map);
        this.setting.save();
        this.saveResource("commands.json");
        this.command = new Config(this.getDataFolder().getPath() + "/commands.json",Config.JSON);
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
}
