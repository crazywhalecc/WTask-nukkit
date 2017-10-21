package cc.crazywhale.WTask.TaskListener;

import cc.crazywhale.WTask.ActTaskAPI;
import cc.crazywhale.WTask.WTask;
import cc.crazywhale.WTask.WTaskAPI;
import cc.crazywhale.WTask.interfaces.TaskListener;
import cc.crazywhale.WTask.tasks.DelayedActTask;
import cn.nukkit.event.EventHandler;
import cn.nukkit.event.Listener;
import cn.nukkit.event.block.BlockBreakEvent;
import cn.nukkit.event.player.PlayerInteractEvent;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Map;

public class PlayerInteractListener extends TaskListenerBase implements Listener, TaskListener {

    WTaskAPI api;
    WTask plugin;
    ArrayList<Map<String, String>> task;
    String tn;

    public PlayerInteractListener(WTaskAPI api, ArrayList<Map<String, String>> task, String tn){
        this.api = api;
        this.plugin = api.plugin;
        this.task = task;
        this.tn = tn;
        this.plugin.getServer().getPluginManager().registerEvents(this, this.plugin);
    }

    public void reload(){
        this.task = api.prepareTask(tn);
    }

    public void runActTask(PlayerInteractEvent event, int ID){
        ActTaskAPI t = new ActTaskAPI(event, event.getPlayer(),api);
        t.writePrivateData("id|"+event.getBlock().getId());
        t.writePrivateData("damage|"+event.getBlock().getDamage());
        t.writePrivateData("pos|"+(new BigDecimal(event.getBlock().x)).intValue()+":"+(new BigDecimal(event.getBlock().y)).intValue()+":"+(new BigDecimal(event.getBlock().z)).intValue()+":"+event.getBlock().level.getFolderName());
        t.writePrivateData("level|"+event.getBlock().level.getFolderName());
        while(ID < task.size()){
            Map<String, String> currentMap = task.get(ID);
            switch(currentMap.get("type")){
                case "取消":
                case "cancel":
                    event.setCancelled(true);
                    break;
                case "清空临时缓存":
                    t.deletePrivateData("id");
                    t.deletePrivateData("damage");
                    t.deletePrivateData("pos");
                    t.deletePrivateData("level");
                    break;
                case "延迟":
                case "delay":
                    this.plugin.getServer().getScheduler().scheduleDelayedTask(new DelayedActTask(this,event,ID+1),Integer.parseInt(currentMap.get("function")) * 20);
                    return;
                case "判断方块":
                case "checkblock":
                    String result = t.checkBlock(currentMap.get("function"));
                    String[] full = result.split(":");
                    switch(full[0]){
                        case "true":
                            break;
                        case "false":
                            plugin.getLogger().warning("在运行任务 "+tn+ " 第 " + String.valueOf(ID+1) + " 号时出现了错误！\n错误信息：" + full[1]);
                            break;
                        case "end":
                            ID = 10000;
                            break;
                        default:
                            String[] pp = full[0].split("-");
                            if(pp.length == 2){
                                ID = Integer.parseInt(pp[1]) - 2;
                            }
                            else{
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
    public void onBreak(PlayerInteractEvent event){
        runActTask(event,0);
    }
}
