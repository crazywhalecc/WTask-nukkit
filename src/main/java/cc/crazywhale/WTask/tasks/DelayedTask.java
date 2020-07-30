package cc.crazywhale.WTask.tasks;

import cc.crazywhale.WTask.WTask;
import cc.crazywhale.WTask.WTaskAPI;
import cn.nukkit.Player;
import cn.nukkit.scheduler.PluginTask;

/**
 * Created by whale on 2017/7/23.
 */
public class DelayedTask extends PluginTask<WTask>{

    private WTaskAPI api;
    private String taskname;
    private Player player;
    private int ID;

    public DelayedTask(WTask plugin,Player p,String taskname,int ID)
    {
        super(plugin);
        this.api = plugin.api;
        this.taskname = taskname;
        this.player = p;
        this.ID = ID;
    }

    public void onRun(int time)
    {
        this.api.runNormalTask(this.taskname,this.player,this.ID,true);
    }
}
