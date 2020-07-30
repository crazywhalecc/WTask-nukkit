package cc.crazywhale.WTask.TaskListener;

import cc.crazywhale.WTask.EntityDamageAPI;
import cc.crazywhale.WTask.WTask;
import cc.crazywhale.WTask.WTaskAPI;
import cc.crazywhale.WTask.interfaces.TaskListener;
import cc.crazywhale.WTask.tasks.DelayedActTask;
import cn.nukkit.Player;
import cn.nukkit.event.EventHandler;
import cn.nukkit.event.Listener;
import cn.nukkit.event.entity.EntityDamageByEntityEvent;
import cn.nukkit.event.entity.EntityDamageEvent;

import java.util.ArrayList;
import java.util.Map;

public class EntityDamageListener extends TaskListenerBase implements Listener, TaskListener {

    WTaskAPI api;
    WTask plugin;
    ArrayList<Map<String, String>> task;
    String tn;

    public EntityDamageListener(WTaskAPI api, ArrayList<Map<String, String>> task, String tn){
        this.api = api;
        this.plugin = api.plugin;
        this.task = task;
        this.tn = tn;
        this.getPluginManager().registerEvents(this,plugin);
    }

    public void reload(){
        this.task = api.prepareTask(tn);
    }

    public void runActTask(EntityDamageByEntityEvent event, int ID){
        if(!(event.getDamager() instanceof Player))
            return;
        if(!(event.getEntity() instanceof Player))
            return;
        EntityDamageAPI t = new EntityDamageAPI(event, api);
        t.writePrivateData("damager|" + event.getDamager().getName());
        t.writePrivateData("victim|" + event.getEntity().getName());
        t.getVictimAPI().writePrivateData("damager|" + event.getDamager().getName());
        t.getVictimAPI().writePrivateData("victim|" + event.getEntity().getName());
        while(ID < task.size()){
            Map<String, String> currentMap = task.get(ID);
            switch(currentMap.get("type")){
                case "cancel":
                case "取消":
                    event.setCancelled(true);
                    break;
                case "设置伤害":
                    event.setDamage(Float.parseFloat(api.executeReturnData(currentMap.get("function"),t.player)));
                    break;
                case "增加伤害":
                    event.setDamage(event.getDamage() + Float.parseFloat(api.executeReturnData(currentMap.get("function"),t.player)));
                    break;
                case "设置击退":
                    event.setKnockBack(Float.parseFloat(api.executeReturnData(currentMap.get("function"),t.player)));
                    break;
                case "转换对象":
                    t = t.getVictimAPI();
                    break;
                case "延迟":
                case "delay":
                    this.plugin.getServer().getScheduler().scheduleDelayedTask(new DelayedActTask(this,event,ID+1),Integer.parseInt(api.executeReturnData(currentMap.get("function"),t.player)) * 20);
                    return;
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
        }
    }

    @EventHandler
    public void onDamage(EntityDamageEvent event){
        if(event instanceof EntityDamageByEntityEvent){
            runActTask(event, 0);
        }
    }
}
