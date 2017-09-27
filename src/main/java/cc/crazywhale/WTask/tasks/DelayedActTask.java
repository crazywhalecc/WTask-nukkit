package cc.crazywhale.WTask.tasks;

import cc.crazywhale.WTask.TaskListener.*;
import cc.crazywhale.WTask.WTask;
import cn.nukkit.event.block.BlockBreakEvent;
import cn.nukkit.event.block.BlockPlaceEvent;
import cn.nukkit.event.entity.EntityDamageByEntityEvent;
import cn.nukkit.event.player.*;
import cn.nukkit.scheduler.PluginTask;

public class DelayedActTask extends PluginTask<WTask> {

    private TaskListenerBase listener;
    private Object event;
    private int ID;

    public DelayedActTask(TaskListenerBase listener, Object event,int ID){
        super(WTask.getInstance());
        this.listener = listener;
        this.event = event;
        this.ID = ID;
    }

    public void onRun(int time){
        if(this.listener instanceof BlockBreakListener){
            this.listener.runActTask((BlockBreakEvent) event, ID);
        }
        else if(this.listener instanceof BlockPlaceListener){
            this.listener.runActTask((BlockPlaceEvent) event, ID);
        }
        else if(this.listener instanceof EntityDamageListener){
            this.listener.runActTask((EntityDamageByEntityEvent) event, ID);
        }
        else if(this.listener instanceof PlayerChatListener){
            this.listener.runActTask((PlayerChatEvent) event, ID);
        }
        else if(this.listener instanceof PlayerCommandActivateListener){
            this.listener.runActTask((PlayerCommandPreprocessEvent) event, ID);
        }
        else if(this.listener instanceof PlayerDeathListener){
            this.listener.runActTask((PlayerDeathEvent) event, ID);
        }
        else if(this.listener instanceof PlayerDropItemListener){
            this.listener.runActTask((PlayerDropItemEvent) event, ID);
        }
        else if(this.listener instanceof PlayerInteractListener){
            this.listener.runActTask((PlayerInteractEvent) event, ID);
        }
        else if(this.listener instanceof PlayerJoinListener){
            this.listener.runActTask((PlayerInteractEvent) event, ID);
        }
    }
}
