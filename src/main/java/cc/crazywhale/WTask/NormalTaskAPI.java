package cc.crazywhale.WTask;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.command.ConsoleCommandSender;
import cn.nukkit.item.Item;
import cn.nukkit.level.Level;
import cn.nukkit.level.Position;
import me.onebone.economyapi.EconomyAPI;
import money.Money;

import java.math.BigDecimal;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Created by whale on 2017/7/23.
 */
public class NormalTaskAPI implements TaskBase{

    public Player player = null;
    public WTaskAPI api = null;

    public NormalTaskAPI(Player p,WTaskAPI api)
    {
        this.player = p;
        this.api = api;
    }

    public String sendMessage(String it)
    {
        it = this.api.executeReturnData(it,this.player);
        this.player.sendMessage(it);
        return "true";
    }

    public String sendTip(String it)
    {
        it = this.api.executeReturnData(it,this.player);
        this.player.sendTip(it);
        return "true";
    }

    public String sendPopup(String it)
    {
        it = this.api.executeReturnData(it,this.player);
        this.player.sendPopup(it);
        return "true";
    }

    public String sendTitle(String it)
    {
        it = this.api.executeReturnData(it,this.player);
        this.player.sendTitle(it);
        return "true";
    }

    public String sendMessageTo(String it)
    {
        String[] lis = it.split("\\|");
        String player = this.api.executeReturnData(lis[0],this.player);
        String msg = this.api.executeReturnData(lis[1],this.player);
        Player p;
        if(player.equals("*all"))
        {
            this.api.plugin.getServer().broadcastMessage(msg);
            return "true";
        }
        p = this.api.plugin.getServer().getPlayerExact(player);
        if(p == null)
        {
            return "false";
        }
        this.api.sendMsgPacket(msg,p,0);
        return "true";
    }

    public String sendTipTo(String it)
    {
        String[] lis = it.split("\\|");
        String player = this.api.executeReturnData(lis[0],this.player);
        String msg = this.api.executeReturnData(lis[1],this.player);
        Player p;
        if(player.equals("*all"))
        {
            for(Map.Entry<UUID,Player> each : this.api.plugin.getServer().getOnlinePlayers().entrySet())
            {
                each.getValue().sendTip(msg);
            }
            return "true";
        }
        p = this.api.plugin.getServer().getPlayerExact(player);
        if(p == null)
        {
            return "false";
        }
        this.api.sendMsgPacket(msg,p,1);
        return "true";
    }

    public String sendPopupTo(String it)
    {
        String[] lis = it.split("\\|");
        String player = this.api.executeReturnData(lis[0],this.player);
        String msg = this.api.executeReturnData(lis[1],this.player);
        Player p;
        if(player.equals("*all"))
        {
            for(Map.Entry<UUID,Player> each : this.api.plugin.getServer().getOnlinePlayers().entrySet())
            {
                each.getValue().sendPopup(msg);
            }
            return "true";
        }
        p = this.api.plugin.getServer().getPlayerExact(player);
        if(p == null)
        {
            return "false";
        }
        this.api.sendMsgPacket(msg,p,2);
        return "true";
    }

    public String writePrivateData(String it)
    {
        if(this.player == null)
            return "false";
        String[] lis = it.split("\\|");
        lis[0] = this.api.executeReturnData(lis[0],this.player);
        lis[1] = this.api.executeReturnData(lis[1],this.player);
        if(!this.api.plugin.privateTempData.containsKey(this.player.getName().toLowerCase())){
            this.api.plugin.privateTempData.put(this.player.getName().toLowerCase(),new LinkedHashMap<String, String>());
        }
        Map<String, String> data = WTask.getStringMap(this.api.plugin.privateTempData.get(this.player.getName().toLowerCase()));
        data.put(lis[0],lis[1]);
        this.api.plugin.privateTempData.put(this.player.getName(),data);
        return "true";
    }

    public String writePublicData(String it)
    {
        String[] lis = it.split("\\|");
        lis[0] = this.api.executeReturnData(lis[0],this.player);
        lis[1] = this.api.executeReturnData(lis[1],this.player);
        this.api.plugin.publicTempData.put(lis[0],lis[1]);
        return "true";
    }

    public String teleport(String it)
    {
        String[] lis = it.split("\\|");
        boolean all = false;
        if(lis[0].substring(0,1).equals("("))
        {
            lis[0] = this.api.executeReturnData(lis[0],this.player);
        }
        else if(lis[0].equals("*all"))
        {
            all = true;
        }
        lis[1] = this.api.executeReturnData(lis[1],this.player);
        Position pos = this.executePosition(lis[1]);
        if(all)
        {
            for(Map.Entry<UUID,Player> each : this.api.plugin.getServer().getOnlinePlayers().entrySet())
            {
                each.getValue().teleport(pos);
            }
            return "true";
        }
        Player p = Server.getInstance().getPlayerExact(lis[0]);
        if(p == null)
        {
            return "false";
        }
        p.teleport(pos);
        return "true";
    }


    public Position executePosition(String pos)
    {
        String[] newPos = pos.split(":");
        Level level = this.api.plugin.getServer().getLevelByName(newPos[3]);
        if(level == null)
            return null;
        return new Position(Double.parseDouble(newPos[0]),Double.parseDouble(newPos[1]),Double.parseDouble(newPos[2]),level);
    }

    public String addMoney(String it)
    {
        it = this.api.executeReturnData(it,this.player);
        switch (this.api.plugin.getEconomyType()) {
            case "Money": {
                BigDecimal c = new BigDecimal(it);
                Money.getInstance().addMoney(this.player, c.floatValue());
                return "true";
            }
            case "EconomyAPI": {
                BigDecimal c = new BigDecimal(it);
                EconomyAPI.getInstance().addMoney(this.player, c.floatValue());
                return "true";
            }
            default:
                return "false";
        }
    }

    public String reduceMoney(String it){
        it = api.executeReturnData(it, player);
        switch (this.api.plugin.getEconomyType()) {
            case "Money": {
                BigDecimal c = new BigDecimal(it);
                Money.getInstance().reduceMoney(this.player, c.floatValue());
                return "true";
            }
            case "EconomyAPI": {
                BigDecimal c = new BigDecimal(it);
                EconomyAPI.getInstance().reduceMoney(this.player, c.floatValue());
                return "true";
            }
            default:
                return "false";
        }
    }

    public String deletePrivateData(String it){
        if(this.player == null){
            return "false";
        }
        if(it.equals("*all")){
            this.api.plugin.privateTempData.remove(this.player.getName().toLowerCase());
        }
        else{
            if(!(this.api.plugin.privateTempData.get(this.player.getName().toLowerCase()) instanceof Map))
                return "false";
            Map<String, String> map = WTask.getStringMap(this.api.plugin.privateTempData.get(this.player.getName().toLowerCase()));
            map.remove(this.api.executeReturnData(it,this.player));
        }
        return "true";
    }

    public String runCommand(String it){
        if(this.player == null){
            return "false";
        }
        it = api.executeReturnData(it,player);
        if(api.plugin.getServer().dispatchCommand(this.player, it.replace("%p",this.player.getName()))){
            return "true";
        }
        else{
            return "false";
        }
    }

    public String runConsoleCommand(String it){
        it = api.executeReturnData(it,player);
        if(api.plugin.getServer().dispatchCommand(new ConsoleCommandSender(), it.replace("%p",this.player.getName()))){
            return "true";
        }
        else{
            return "false";
        }
    }

    public String addItem(String it){
        String[] its = it.split("\\|");
        String[] items = its[1].split(",");
        Player p = api.plugin.getServer().getPlayerExact(api.executeReturnData(its[0],player));
        if(p != null){
            for (String item1 : items) {
                p.getInventory().addItem(this.executeItem(item1));
            }
            return "true";
        }
        else
        {
            return "false";
        }
    }

    public Item executeItem(String itemString){
        String[] split = itemString.split("-");
        return Item.get(Integer.parseInt(split[0]),Integer.parseInt(split[1]),Integer.parseInt(split[2]));
    }
}
