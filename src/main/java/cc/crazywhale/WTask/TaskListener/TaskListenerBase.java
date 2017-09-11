package cc.crazywhale.WTask.TaskListener;

import cn.nukkit.Server;
import cn.nukkit.event.Event;
import cn.nukkit.event.block.BlockBreakEvent;
import cn.nukkit.plugin.PluginManager;

public abstract class TaskListenerBase {
    public Server getServer(){
        return Server.getInstance();
    }

    public void runActTask(Event event, int id) {
    }

    public PluginManager getPluginManager(){
        return Server.getInstance().getPluginManager();
    }
}
