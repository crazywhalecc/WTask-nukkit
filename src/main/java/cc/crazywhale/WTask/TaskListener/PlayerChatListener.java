package cc.crazywhale.WTask.TaskListener;

import cc.crazywhale.WTask.ActTaskAPI;
import cc.crazywhale.WTask.WTask;
import cc.crazywhale.WTask.WTaskAPI;
import cc.crazywhale.WTask.interfaces.TaskListener;
import cc.crazywhale.WTask.tasks.DelayedActTask;
import cn.nukkit.event.EventHandler;
import cn.nukkit.event.Listener;
import cn.nukkit.event.player.PlayerChatEvent;

import java.util.ArrayList;
import java.util.Map;

public class PlayerChatListener extends TaskListenerBase implements TaskListener, Listener {

    WTaskAPI api;
    WTask plugin;
    ArrayList<Map<String, String>> task;
    String tn;

    public PlayerChatListener(WTaskAPI api, ArrayList<Map<String, String>> task, String tn){
        this.api = api;
        this.task = task;
        this.tn = tn;
        this.plugin = api.plugin;
        this.getPluginManager().registerEvents(this,plugin);
    }

    /**
     *
     */
    public void reload(){
        this.task = api.prepareTask(tn);
    }

    public void runActTask(PlayerChatEvent event, int ID){
        ActTaskAPI t = new ActTaskAPI(event, event.getPlayer(), api);
        if(ID == 0){
            t.writePrivateData("msg|" + event.getMessage());
            t.writePrivateData("length|" + event.getMessage());
        }
        while(ID < task.size()){
            Map<String, String> currentMap = task.get(ID);
            switch(currentMap.get("type")){
                case "cancel":
                case "取消":
                    event.setCancelled(true);
                    break;
                case "延迟":
                case "delay":
                    this.plugin.getServer().getScheduler().scheduleDelayedTask(new DelayedActTask(this,event,ID+1),Integer.parseInt(currentMap.get("function")) * 20);
                    return;
                case "设置消息":
                    event.setMessage(api.msgs(api.executeReturnData(currentMap.get("function"),t.player),t.player));
                    break;
                case "检查消息":
                case "checkmsg":
                    String result = t.checkMessage(currentMap.get("function"));
                    String[] full = result.split(":");
                    switch(full[0]) {
                        case "true":
                            break;
                        case "false":
                            plugin.getLogger().warning("在运行任务 " + tn + " 第 " + String.valueOf(ID + 1) + " 号时出现了错误！\n错误信息：" + full[1]);
                            break;
                        case "end":
                            ID = 10000;
                            break;
                        default:
                            String[] pp = full[0].split("-");
                            if (pp.length == 2) {
                                ID = Integer.parseInt(pp[1]) - 2;
                            } else {
                                break;
                            }
                    }
                default:
                    String result2 = api.defaultFunction(t,currentMap);
                    String[] full2 = result2.split(":");
                    switch(full2[0]) {
                        case "true":
                            break;
                        case "false":
                            plugin.getLogger().warning("在运行任务 " + tn + " 第 " + String.valueOf(ID + 1) + " 号时出现了错误！\n错误信息：" + full2[1]);
                            break;
                        case "end":
                            ID = 10000;
                            break;
                        default:
                            String[] pp = full2[0].split("-");
                            if (pp.length == 2) {
                                ID = Integer.parseInt(pp[1]) - 2;
                            } else {
                                break;
                            }
                    }
            }
            ID++;
        }
    }

    @EventHandler
    public void onChat(PlayerChatEvent event){
        runActTask(event, 0);
    }
}
