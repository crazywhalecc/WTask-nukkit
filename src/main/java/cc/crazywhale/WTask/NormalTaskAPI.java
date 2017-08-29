package cc.crazywhale.WTask;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.command.ConsoleCommandSender;
import cn.nukkit.entity.data.Skin;
import cn.nukkit.item.Item;
import cn.nukkit.level.Level;
import cn.nukkit.level.Position;
import cn.nukkit.level.sound.*;
import cn.nukkit.potion.Effect;
import me.onebone.economyapi.EconomyAPI;
import money.Money;

import java.io.File;
import java.math.BigDecimal;
import java.util.*;

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
            return "false:玩家不存在！";
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
            return "false:玩家不存在！";
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
            return "false:玩家不存在！";
        }
        this.api.sendMsgPacket(msg,p,2);
        return "true";
    }

    public String writePrivateData(String it)
    {
        if(this.player == null)
            return "false:玩家不存在！";
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
            return "false:玩家不存在！";
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
                return "false:未安装任何经济核心！";
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
                return "false:未安装任何经济核心！";
        }
    }

    public String deletePrivateData(String it){
        if(this.player == null){
            return "false:玩家不存在！";
        }
        if(it.equals("*all")){
            this.api.plugin.privateTempData.remove(this.player.getName().toLowerCase());
        }
        else{
            if(this.api.plugin.privateTempData.get(this.player.getName().toLowerCase()) == null)
                return "false:内部错误";
            Map<String, String> map = WTask.getStringMap(this.api.plugin.privateTempData.get(this.player.getName().toLowerCase()));
            map.remove(this.api.executeReturnData(it,this.player));
        }
        return "true";
    }

    public String runCommand(String it){
        if(this.player == null){
            return "false:玩家不存在！";
        }
        it = api.executeReturnData(it,player);
        if(api.plugin.getServer().dispatchCommand(this.player, it.replace("%p",this.player.getName()))){
            return "true";
        }
        else{
            return "false:指令无法执行";
        }
    }

    public String runConsoleCommand(String it){
        it = api.executeReturnData(it,player);
        if(api.plugin.getServer().dispatchCommand(new ConsoleCommandSender(), it.replace("%p",this.player.getName()))){
            return "true";
        }
        else{
            return "false:指令无法执行";
        }
    }

    public String checkFinish(String it,String tn, Player p){
        if(p == null){
            return "false:未检测到玩家！";
        }
        String[] its = it.split("\\|");
        boolean status = false;
        Map<String, Object> list = (Map<String, Object>)this.api.plugin.daily.get("普通任务");
        if(!list.containsKey(tn)){
            list = new LinkedHashMap<>();
        }
        else{
            list = (Map<String, Object>) list.get(tn);
        }
        String name = p.getName().toLowerCase();
        String mode = this.api.mode.get(tn);
        String[] modes = mode.split(":");
        switch(modes[0]){
            case "false":
                break;
            case "once":
                if(list.containsKey(name)){
                    status = true;
                    break;
                }
                else{
                    break;
                }
            case "multi-day":
                if(list.containsKey(name)){
                    int day = Calendar.getInstance().get(Calendar.DAY_OF_MONTH);
                    int pastday = (Integer) ((Map<String, Object>) list.get(name)).get("date");
                    if(day != pastday){
                        break;
                    }
                    else{
                        int times = (Integer) ((Map<String, Object>) list.get(name)).get("times");
                        if(times >= Integer.parseInt(modes[1])){
                            status = true;
                            break;
                        }
                        else{
                            break;
                        }
                    }
                }
                else{
                    break;
                }
            case "single-day":
                if(list.containsKey(name)){
                    Long currentTime = (new Date()).getTime();
                    Long finishTime = (Long) ((Map<String, Object>) list.get(name)).get("date");
                    Long upgrade = ((Long) ((Map<String, Object>) list.get(name)).get("times"))*86400;
                    if((upgrade + finishTime) >= currentTime){
                        status = true;
                    }
                    break;
                }
                else{
                    break;
                }
            case "limit-time":
                if(list.containsKey(name)){
                    int times = (Integer) ((Map<String, Object>) list.get(name)).get("times");
                    if(times >= Integer.parseInt(modes[1])){
                        status = true;
                    }
                    break;
                }
                else{
                    break;
                }
            default:
                break;
        }
        if(status){
            return this.doSubCommand(its[0]);
        }
        else{
            return this.doSubCommand(its[1]);
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
            return "false:玩家不存在！";
        }
    }

    public Item executeItem(String itemString){
        String[] split = itemString.split("-");
        return Item.get(Integer.parseInt(split[0]),Integer.parseInt(split[1]),Integer.parseInt(split[2]));
    }

    public String setCustomSkin(String it){
        if(this.player == null){
            return "false:玩家不存在";
        }
        Player real = Server.getInstance().getPlayerExact(api.executeReturnData(it,this.player));
        if(real == null){
            return "false:切换皮肤的原始玩家不在线！";
        }
        Skin skin = real.getSkin();
        this.player.setSkin(skin);
        return "true";
    }

    public void setPermission(int perm){
        if(this.player == null){
            return;
        }
        api.plugin.playerPerm.set(player.getName().toLowerCase(),perm);
        api.plugin.playerPerm.save();
    }

    public String addEffect(String it){
        String[] its = it.split("\\|");
        if(player == null){
            return "false::玩家不存在！";
        }
        int id = api.strtoint(api.executeReturnData(its[0],player));
        int sec = api.strtoint(api.executeReturnData(its[1],player));
        int level = api.strtoint(api.executeReturnData(its[2],player));
        boolean particle = api.executeReturnData(its[3], player).equals("效果开");
        Effect effect = Effect.getEffect(id);
        effect.setVisible(particle);
        effect.setAmplifier(level);
        effect.setDuration(sec);
        player.addEffect(effect);
        return "true";
    }

    public String makeSound(String it){
        if(player == null){
            return "false:玩家不存在！";
        }
        Player p = this.player;
        Level l = player.getLevel();
        String[] its = it.split(",");
        for(String pick : its){
            switch(pick){
                case "1":
                    l.addSound(new AnvilFallSound(player));
                    break;
                case "2":
                    l.addSound(new AnvilUseSound(player));
                    break;
                case "4":
                    l.addSound(new BlazeShootSound(player));
                    break;
                case "5":
                    l.addSound(new ButtonClickSound(player));
                    break;
                case "6":
                    l.addSound(new DoorBumpSound(player));
                    break;
                case "7":
                    l.addSound(new DoorCrashSound(player));
                    break;
                case "8":
                    l.addSound(new DoorSound(player));
                    break;
                case "9":
                    l.addSound(new EndermanTeleportSound(player));
                    break;
                case "10":
                    l.addSound(new ExplodeSound(player));
                    break;
                case "11":
                    l.addSound(new ExperienceOrbSound(p));
                    break;
                case "12":
                    l.addSound(new FizzSound(p));
                    break;
                case "13":
                    l.addSound(new GhastShootSound(p));
                    break;
                case "14":
                    l.addSound(new GhastSound(p));
                    break;
                case "15":
                    l.addSound(new LaunchSound(p));
                    break;
                case "16":
                    api.plugin.getLogger().warning("请使用乐谱来播放音符盒声音！");
                    break;
                case "17":
                    l.addSound(new PopSound(p));
                    break;
                case "18":
                    api.plugin.getLogger().warning("此声音在nk端不再支持！");
                    break;
                case "19":
                    l.addSound(new SplashSound(p));
                    break;
                case "20":
                    l.addSound(new TNTPrimeSound(p));
                    break;
                case "21":
                    l.addSound(new ClickSound(p));
                    break;
                case "22":
                    l.addSound(new PistonInSound(p));
                    break;
                case "23":
                    l.addSound(new PistonOutSound(p));
                    break;
            }
        }
        return "true";
    }

    public String setNameTag(String it){
        it = api.executeReturnData(it,this.player);
        this.player.setNameTag(api.msgs(it,player));
        return "true";
    }

    public String manageConfig(String it) {
        String[] its = it.split("|");
        switch(its[0]){
            case "创建":
                String filename = api.executeReturnData(its[1],this.player);
                api.plugin.customConfig.put(filename,new Config(this.api.plugin.getDataFolder().getPath() + "CustomConfig/" + filename + ".yml",Config.YAML));
                return "true";
            case "写入":
                String filename2 = api.executeReturnData(its[1],this.player);
                File file = new File(this.api.plugin.getDataFolder().getPath() + "CustomConfig/",filename2+".yml");
                if(!file.exists()){
                    return "false:文件不存在！";
                }
                else{
                    String itemName = api.executeReturnData(its[2],player);
                    String inside = api.executeReturnData(its[3],player);
                    Config cfg;
                    if(!api.plugin.customConfig.containsKey(filename2)){
                        cfg = new Config(this.api.plugin.getDataFolder().getPath() + "CustomConfig/" + filename2 + ".yml",Config.YAML);
                    }
                    else{
                        cfg = api.plugin.customConfig.get(filename2);
                    }
                    cfg.set(itemName,inside);
                    cfg.save();
                    return "true";
                }
            case "是否存在文件":
                String filename3 = api.executeReturnData(its[1],this.player);
                File file2 = new File(this.api.plugin.getDataFolder().getPath() + "CustomConfig/",filename3 + ".yml");
                if(file2.exists()){
                    return this.doSubCommand(its[2]);
                }
                else{
                    return this.doSubCommand(its[3]);
                }
            case "是否存在":
                String filename4 = api.executeReturnData(its[1],this.player);
                File file3 = new File(this.api.plugin.getDataFolder().getPath() + "CustomConfig/",filename4 + ".yml");
                if(!file3.exists()){
                    return this.doSubCommand(its[4]);
                }
                Config cfg;
                if(!api.plugin.customConfig.containsKey(filename4)){
                    cfg = new Config(this.api.plugin.getDataFolder().getPath() + "CustomConfig/" + filename4 + ".yml",Config.YAML);
                }
                else{
                    cfg = api.plugin.customConfig.get(filename4);
                }
                if(cfg.exists(its[2])){
                    return this.doSubCommand(its[3]);
                }
                else{
                    return this.doSubCommand(its[4]);
                }
            case "删除":
                String filename5 = api.executeReturnData(its[1],this.player);
                File file5 = new File(this.api.plugin.getDataFolder().getPath() + "CustomConfig/",filename5+".yml");
                if(!file5.exists()){
                    return "false:文件不存在！";
                }
                else{
                    String itemName = api.executeReturnData(its[2],player);
                    String inside = api.executeReturnData(its[3],player);
                    Config cfg2;
                    if(!api.plugin.customConfig.containsKey(filename5)){
                        cfg2 = new Config(this.api.plugin.getDataFolder().getPath() + "CustomConfig/" + filename5 + ".yml",Config.YAML);
                    }
                    else{
                        cfg2 = api.plugin.customConfig.get(filename5);
                    }
                    cfg2.remove(itemName);
                    cfg2.save();
                    return "true";
                }
            default:
                return "false:错误的选项！";
        }
    }

    public String doSubCommand(String cmdd){
        String[] multiTask = cmdd.split(",");
        for(String multi : multiTask){
            String[] cmd = multi.split("\\.");
            switch(cmd[0]){
                case "跳转":
                case "jump":
                    return "jump-" + cmd[1];
                case "消息":
                case "msg":
                    api.sendMsgPacket(cmd[1],this.player,0);
                    break;
                case "提示":
                case "tip":
                    api.sendMsgPacket(cmd[1],player,1);
                    break;
                case "底部":
                case "popup":
                    api.sendMsgPacket(cmd[1],player,2);
                    break;
                case "pass":
                    return "true";
                case "end":
                case "结束":
                    return "end";
            }
        }
        return "true";
    }
}
