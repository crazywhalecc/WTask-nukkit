package cc.crazywhale.WTask;

import cc.crazywhale.WTask.interfaces.ActTaskBase;
import cn.nukkit.Player;
import cn.nukkit.event.Event;
import cn.nukkit.event.block.BlockBreakEvent;
import cn.nukkit.event.block.BlockEvent;
import cn.nukkit.event.player.PlayerChatEvent;
import cn.nukkit.event.player.PlayerCommandPreprocessEvent;
import cn.nukkit.event.player.PlayerDeathEvent;
import cn.nukkit.event.player.PlayerDropItemEvent;
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

    public PlayerChatEvent getPlayerChatEvent(){
        return (PlayerChatEvent) event;
    }

    public BlockBreakEvent getBlockBreakEvent(){
        return (BlockBreakEvent) event;
    }

    public PlayerCommandPreprocessEvent getPlayerCommandPreprocessEvent(){
        return (PlayerCommandPreprocessEvent) event;
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

    public String checkMessage(String it){
        if(!(this.event instanceof PlayerChatEvent)){
            return "false:传入的事件类型错误！";
        }
        String[] its = it.split("\\|");
        if(its.length < 4){
            return "false:传入的参数不足！";
        }
        switch(its[0]){
            case "比较消息":
                String msg = getPlayerChatEvent().getMessage();
                if(msg.equals(api.executeReturnData(its[1],player))){
                    return doSubCommand2(its[2]);
                }
                else{
                    return doSubCommand2(its[3]);
                }
            case "存在关键词":
                String msg2 = getPlayerChatEvent().getMessage();
                if(msg2.contains(api.executeReturnData(its[1], player))){
                    return doSubCommand2(its[2]);
                }
                else{
                    return doSubCommand(its[3]);
                }

        }
        return "false:未知类型的分参数功能！";
    }

    public String checkCommand(String it){
        if(!(this.event instanceof PlayerCommandPreprocessEvent)){
            return "false:传入的事件类型错误！";
        }
        String[] its = it.split("\\|");
        if(its.length < 4){
            return "false:传入的参数不足！";
        }
        switch(its[0]){
            case "比较主指令":
                String msg = getPlayerCommandPreprocessEvent().getMessage();
                if(msg.substring(1,msg.indexOf(" ")).equals(api.executeReturnData(its[1],player))){
                    return doSubCommand2(its[2]);
                }
                else{
                    return doSubCommand2(its[3]);
                }
            case "比较全指令":
                String msg2 = getPlayerChatEvent().getMessage();
                if(msg2.substring(1).equals(api.executeReturnData(its[1], player))){
                    return doSubCommand2(its[2]);
                }
                else{
                    return doSubCommand(its[3]);
                }
        }
        return "false:未知类型的分参数功能！";
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

    public void setKeepInv(String it){
        if(event instanceof PlayerDeathEvent){
            if(Integer.parseInt(it) == 0){
                ((PlayerDeathEvent) event).setKeepInventory(false);
            }
            else{
                ((PlayerDeathEvent) event).setKeepInventory(true);
            }
        }
    }

    public void setKeepExp(String it){
        if(event instanceof PlayerDeathEvent){
            if(Integer.parseInt(it) == 0){
                ((PlayerDeathEvent) event).setKeepExperience(false);
            }
            else{
                ((PlayerDeathEvent) event).setKeepExperience(true);
            }
        }
    }

    public String checkDropItem(String it){
        if(!(event instanceof PlayerDropItemEvent)){
            return "false:传入了未知类型的事件！";
        }
        String[] its = it.split("\\|");
        if(its.length < 3){
            return "false:传入的参数不足！";
        }
    return "";
    }
}
