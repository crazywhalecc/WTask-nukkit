package cc.crazywhale.WTask.TaskListener;

import cc.crazywhale.WTask.ActTaskAPI;
import cc.crazywhale.WTask.WTask;
import cc.crazywhale.WTask.WTaskAPI;
import cc.crazywhale.WTask.interfaces.TaskListener;
import cc.crazywhale.WTask.tasks.DelayedActTask;
import cn.nukkit.Player;
import cn.nukkit.event.EventHandler;
import cn.nukkit.event.Listener;
import cn.nukkit.event.block.BlockBreakEvent;
import cn.nukkit.event.entity.EntityTeleportEvent;
import cn.nukkit.event.player.PlayerInteractEvent;
import cn.nukkit.event.player.PlayerMoveEvent;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Map;

public class PlayerTeleportListener extends TaskListenerBase implements Listener, TaskListener {

    WTaskAPI api;
    WTask plugin;
    ArrayList<Map<String, String>> task;
    String tn;

    public PlayerTeleportListener(WTaskAPI api, ArrayList<Map<String, String>> task, String tn){
        this.api = api;
        this.plugin = api.plugin;
        this.task = task;
        this.tn = tn;
        this.plugin.getServer().getPluginManager().registerEvents(this, this.plugin);
    }

    public void reload(){
        this.task = api.prepareTask(tn);
    }

    public void runActTask(EntityTeleportEvent event, int ID){
        if(!(event.getEntity() instanceof Player))
            return;
        ActTaskAPI t = new ActTaskAPI(event, (Player) event.getEntity(),api);
        t.writePrivateData("pos|"+(new BigDecimal(event.getTo().x)).intValue()+":"+(new BigDecimal(event.getTo().y)).intValue()+":"+(new BigDecimal(event.getTo().z)).intValue()+":"+event.getTo().level.getFolderName());
        t.writePrivateData("level|"+event.getTo().level.getFolderName());
        while(ID < task.size()){
            Map<String, String> currentMap = task.get(ID);
            switch(currentMap.get("type")){
                case "取消":
                case "cancel":
                    event.setCancelled(true);
                    break;
                case "清空临时缓存":
                    t.deletePrivateData("pos");
                    t.deletePrivateData("level");
                    break;
                case "checktarget":
                case "检查目标位置":
                    String result = t.checkTarget(currentMap.get("function"));
                    String[] news = result.split(":");
                    switch (news[0]) {
                        case "true":
                            break;
                        case "false":
                            plugin.getLogger().warning("在运行任务：" + tn + " 第 " + (ID + 1) + " 号时出现了错误！\n错误信息：" + news[1]);
                            break;
                        case "end":
                            ID = 10000;
                            break;
                        default:
                            String[] pp = news[0].split("-");
                            if (pp.length == 2) {
                                ID = Integer.parseInt(pp[1]) - 2;
                            } else {
                                break;
                            }
                    }
                    break;
                default:
                    String r = api.defaultFunction(t, currentMap);
                    String[] newr = r.split(":");
                    switch (newr[0]) {
                        case "true":
                            break;
                        case "false":
                            plugin.getLogger().warning("在运行任务：" + tn + " 第 " + (ID + 1) + " 号时出现了错误！\n错误信息：" + newr[1]);
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
    }

    @EventHandler
    public void onBreak(EntityTeleportEvent event){
        runActTask(event,0);
    }
}
