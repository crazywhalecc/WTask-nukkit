package cc.crazywhale.WTask;

import cn.nukkit.Player;

import java.util.Map;
import java.util.UUID;

public class RepeatTaskAPI extends NormalTaskAPI {

    WTask plugin;
    String currentTask;
    Player player;

    public RepeatTaskAPI(Player p, WTaskAPI api, String taskname) {
        super(p, api);
        this.plugin = api.plugin;
        this.currentTask = taskname;
        this.player = p;
    }

    public String broadcastMessage(String it){
        String its = api.executeReturnData(it, null);
        for(Map.Entry<UUID, Player> p : plugin.getServer().getOnlinePlayers().entrySet()){
            p.getValue().sendMessage(its);
        }
        return "true";
    }

    public String broadcastTip(String it){
        String its = api.executeReturnData(it, null);
        for(Map.Entry<UUID, Player> p : plugin.getServer().getOnlinePlayers().entrySet()){
            p.getValue().sendTip(its);
        }
        return "true";
    }

    @SuppressWarnings("unused")
    public String broadcastPopup(String it){
        String its = api.executeReturnData(it, null);
        for(Map.Entry<UUID, Player> p : plugin.getServer().getOnlinePlayers().entrySet()){
            p.getValue().sendPopup(its);
        }
        return "true";
    }

    public String broadcastTitle(String it){
        String its = api.executeReturnData(it, null);
        for(Map.Entry<UUID, Player> p : plugin.getServer().getOnlinePlayers().entrySet()){
            p.getValue().sendTitle(its);
        }
        return "true";
    }
}
