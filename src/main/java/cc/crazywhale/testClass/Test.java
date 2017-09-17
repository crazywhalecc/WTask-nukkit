package cc.crazywhale.testClass;

import cc.crazywhale.WTask.NormalTaskAPI;
import cc.crazywhale.WTask.WTask;
import cc.crazywhale.WTask.WTaskAPI;
import cc.crazywhale.WTask.tasks.PressureTask;
import cn.nukkit.Server;

public class Test {
    public Test(){
        WTaskAPI ss = new WTaskAPI(WTask.getInstance());
        NormalTaskAPI t = new NormalTaskAPI(null,ss);
        t.player.getAddress();
        WTaskAPI api = t.api;
        t.sendMessage("ss");
        WTask aaa = ss.plugin;
    }

    public static void pressureTask(WTask plugin){
        Server.getInstance().getLogger().notice("Starting pressure testing......");
        Server.getInstance().getScheduler().scheduleRepeatingTask(new PressureTask(plugin),1);
    }
}
