package cc.crazywhale.WTask.tasks;

import cc.crazywhale.WTask.WTask;
import cn.nukkit.scheduler.PluginTask;

public class PressureTask extends PluginTask<WTask> {

    public PressureTask(WTask plugin){

        super(plugin);
        plugin.getServer().getLogger().info("开始激动人心的压力测试咯");
    }

    public void onRun(int time){/*
        int t = 0;
        for(int i = 0;i< 200;i++){
            if(i == 199){
                if(t < 2){
                    t++;
                    i=0;
                }
            }
        }*/
    }
}
