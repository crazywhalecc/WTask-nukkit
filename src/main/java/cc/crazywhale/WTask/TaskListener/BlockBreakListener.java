package cc.crazywhale.WTask.TaskListener;

import cc.crazywhale.WTask.WTask;
import cc.crazywhale.WTask.WTaskAPI;
import cn.nukkit.event.Listener;

import java.util.ArrayList;
import java.util.Map;

public class BlockBreakListener implements TaskListener, Listener {

    WTaskAPI api;
    WTask plugin;
    ArrayList<Map<String, String>> task;
    String tn;

    public BlockBreakListener(WTaskAPI api, ArrayList<Map<String, String>> task, String tn){
        this.api = api;
        this.plugin = api.plugin;
        this.task = task;
        this.tn = tn;
        this.plugin.getServer().getPluginManager().registerEvents(this,plugin);
    }

    public void reload(){

    }
}
