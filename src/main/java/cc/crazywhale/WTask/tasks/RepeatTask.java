package cc.crazywhale.WTask.tasks;

import cc.crazywhale.WTask.RepeatTaskAPI;
import cc.crazywhale.WTask.WTask;
import cn.nukkit.scheduler.PluginTask;

import java.util.ArrayList;
import java.util.Map;

public class RepeatTask extends PluginTask<WTask> {

    WTask plugin;
    String taskname;

    public RepeatTask(WTask owner, String taskname) {
        super(owner);
        this.plugin = owner;
        this.taskname = taskname;
    }

    @Override
    public void onRun(int i) {
        runRepeatTask(taskname, 0, false);
    }

    public void runRepeatTask(String taskname, int ID, boolean delayStep){
        RepeatTaskAPI t = new RepeatTaskAPI(null, plugin.api, taskname);
        if(!delayStep) ID = 0;
        ArrayList<Map<String, String>> taskInside = plugin.repeatTaskList.get(taskname);
        while (ID < taskInside.size()) {
            Map<String, String> currentMap = taskInside.get(ID);
            switch (currentMap.get("type")) {
                case "广播消息":
                    t.broadcastMessage(currentMap.get("function"));
                    break;
                case "广播tip":
                case "广播提示":
                    t.broadcastTip(currentMap.get("function"));
                    break;
                case "广播底部":
                case "广播popup":
                    t.broadcastPopup(currentMap.get("function"));
                    break;
                case "广播标题":
                case "广播title":
                    t.broadcastTitle(currentMap.get("function"));
                    break;
                default:
                    String r = plugin.api.defaultFunction(t, currentMap);
                    String[] newr = r.split(":");
                    switch (newr[0]) {
                        case "true":
                            break;
                        case "false":
                            plugin.getServer().getLogger().warning("在运行任务：" + taskname + " 第 " + (ID + 1) + " 号时出现了错误！\n错误信息：" + newr[1]);
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
}
