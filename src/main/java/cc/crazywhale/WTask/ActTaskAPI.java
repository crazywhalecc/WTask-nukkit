package cc.crazywhale.WTask;

import cc.crazywhale.WTask.interfaces.ActTaskBase;
import cn.nukkit.Player;
import cn.nukkit.event.Event;
import cn.nukkit.event.block.BlockBreakEvent;
import cn.nukkit.event.block.BlockEvent;
import cn.nukkit.item.Item;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ActTaskAPI extends NormalTaskAPI implements ActTaskBase {

    private Object event;
    public WTask plugin;

    public ActTaskAPI(Object event, Player player, WTaskAPI api){
        super(player,api);
        this.event = event;
        this.plugin = api.plugin;
    }

    public boolean isBlockEvent(){
        return event instanceof BlockEvent;
    }

    public BlockEvent getBlockEvent(){
        return (BlockEvent) event;
    }

    public BlockBreakEvent getBlockBreakEvent(){
        return (BlockBreakEvent) event;
    }

    public Event getEvent(){
        return (Event) event;
    }

    public String checkBlock(String it){
        if(!(this.event instanceof BlockEvent)){
            return "false:传入的事件类型出错！";
        }
        String[] its = it.split("\\|");
        if(its.length < 4){
            return "false:检查方块功能中分参数未完整填写！";
        }
        String type = api.executeReturnData(its[0],player);
        String scale = api.executeReturnData(its[1],player);
        switch(type){
            case "id对比":
            case "ID对比":
            case "ID 对比":
                String[] sc = scale.split("-");
                if(Integer.parseInt(sc[0]) == this.getBlockEvent().getBlock().getId() && Integer.parseInt(sc[1]) == this.getBlockEvent().getBlock().getDamage()){
                    return doSubCommand2(its[2]);
                }
                else{
                    return doSubCommand2(its[3]);
                }
        }
        return "true";
    }

    public String setDropItems(String it){
        if(!this.isBlockEvent()){
            return "false:传入的事件类型错误！";
        }
        String[] its = it.split("\\|");
        ArrayList<Item> itemq = new ArrayList<>();
        for(String iss : its){
            Item plot = executeItem(this.api.executeReturnData(iss,player));
            if(plot == null){
                return "false:未知错误！";
            }
            itemq.add(plot);
        }
        Item[] list = itemq.toArray(new Item[itemq.size()]);
        this.getBlockBreakEvent().setDrops(list);
        return "true";
    }

    public String doSubCommand2(String it){
        String[] its = it.split(",");
        List<String> s = Arrays.asList(its);
        if(s.contains("cancel") || s.contains("取消")){
            this.getEvent().setCancelled();
        }
        return doSubCommand(it);
    }
}
