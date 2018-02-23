package cc.crazywhale.WTask;

import cc.crazywhale.WTask.interfaces.TaskBase;
import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.command.ConsoleCommandSender;
import cn.nukkit.entity.data.Skin;
import cn.nukkit.item.Item;
import cn.nukkit.level.Explosion;
import cn.nukkit.level.Level;
import cn.nukkit.level.Position;
import cn.nukkit.level.Sound;
import cn.nukkit.math.Vector3;
import cn.nukkit.potion.Effect;
import me.onebone.economyapi.EconomyAPI;
import money.Money;

import java.io.File;
import java.math.BigDecimal;
import java.util.*;

/**
 * Created by whale on 2017/7/23.
 */
public class NormalTaskAPI implements TaskBase {

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

    @SuppressWarnings("unchecked")
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
        Level l = player.getLevel();
        String[] its = it.split(",");
        for(String pick : its){
            switch(pick){
                case "1":
                    l.addSound(player, Sound.RANDOM_ANVIL_LAND);
                    break;
                case "2":
                    l.addSound(player, Sound.RANDOM_ANVIL_USE);
                    break;
                case "4":
                    l.addSound(player, Sound.MOB_BLAZE_SHOOT);
                    break;
                case "5":
                    l.addSound(player, Sound.RANDOM_CLICK);
                    break;
                case "6":
                    l.addSound(player, Sound.RANDOM_DOOR_OPEN);
                    break;
                case "7":
                    l.addSound(player, Sound.RANDOM_DOOR_CLOSE);
                    break;
                case "8":
                    api.plugin.getLogger().warning("此声音在nk端不再支持！");
                    break;
                case "9":
                    l.addSound(player, Sound.MOB_SHULKER_TELEPORT);
                    break;
                case "10":
                    l.addSound(player, Sound.RANDOM_EXPLODE);
                    break;
                case "11":
                    l.addSound(player, Sound.RANDOM_ORB);
                    break;
                case "12":
                    l.addSound(player, Sound.RANDOM_FIZZ);
                    break;
                case "13":
                    l.addSound(player, Sound.MOB_GHAST_FIREBALL);
                    break;
                case "14":
                    l.addSound(player, Sound.MOB_GHAST_SCREAM);
                    break;
                case "15":
                    l.addSound(player, Sound.FIREWORK_LAUNCH);
                    break;
                case "16":
                    api.plugin.getLogger().warning("请使用乐谱来播放音符盒声音！");
                    break;
                case "17":
                    l.addSound(player, Sound.RANDOM_POP);
                    break;
                case "18":
                    api.plugin.getLogger().warning("此声音在nk端不再支持！");
                    break;
                case "19":
                    l.addSound(player, Sound.RANDOM_SPLASH);
                    break;
                case "20":
                    l.addSound(player, Sound.AMBIENT_WEATHER_LIGHTNING_IMPACT);
                    break;
                case "21":
                    l.addSound(player, Sound.RANDOM_CLICK);
                    break;
                case "22":
                    l.addSound(player, Sound.TILE_PISTON_IN);
                    break;
                case "23":
                    l.addSound(player, Sound.TILE_PISTON_OUT);
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
        String[] its = it.split("\\|");
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

    public String manageTemp(String it){
        String[] its = it.split("\\|");
        its[1] = api.executeReturnData(its[1],this.player);
        switch(its[0]){
            case "是否存在私有":
                if(this.player == null){
                    return "false:玩家不存在！";
                }
                if(!this.api.plugin.privateTempData.containsKey(this.player.getName().toLowerCase())){
                    return doSubCommand(its[3]);
                }
                else if(this.api.plugin.privateTempData.get(this.player.getName().toLowerCase()).containsKey(its[1])){
                    return doSubCommand(its[2]);
                }
                else{
                    return doSubCommand(its[3]);
                }
            case "是否存在公有":
                if(this.player == null){
                    return "false:玩家不存在！";
                }
                its[1] = api.executeReturnData(its[1],this.player);
                if(this.api.plugin.publicTempData.containsKey(its[1])){
                    return doSubCommand(its[2]);
                }
                else{
                    return doSubCommand(its[3]);
                }
            default:
                return "false:传入参数错误！";
        }
    }

    public String compareText(String it){
        String[] its = it.split("\\|");
        its[0] = api.executeReturnData(its[0],player);
        its[1] = api.executeReturnData(its[1],player);
        if(its.length < 4){
            return "false:比较字符串功能传入参数不足，期望数量：4";
        }
        if(its[0].equals(its[1])){
            return doSubCommand(its[2]);
        }
        else{
            return doSubCommand(its[3]);
        }
    }

    public String makeExplosion(String it){
        String[] its = it.split("\\|");
        its[0] = api.executeReturnData(its[0],player);
        its[1] = api.executeReturnData(its[1],player);
        Position pos = this.executePosition(its[0]);
        Explosion boom = new Explosion(pos,Double.parseDouble(its[1]),null);
        if(boom.explodeA()) boom.explodeB();
        return "true";
    }

    public String checkInventory(String it){
        String[] its = it.split("\\|");
        its[0] = api.executeReturnData(its[0],player);
        String[] items = its[0].split(",");
        boolean res = false;
        for(String item : items){
            String[] itemdd = item.split("-");
            int cnt = 0;
            for(Map.Entry<Integer, Item> entry : player.getInventory().getContents().entrySet()){
                if(entry.getValue().getId() == Integer.parseInt(itemdd[0]) && entry.getValue().getDamage() == Integer.parseInt(itemdd[1])){
                    cnt += entry.getValue().getCount();
                }
            }
            if(cnt >= Integer.parseInt(itemdd[2])){
                res = true;
            }
            else{
                res = false;
                break;
            }
        }
        if(res){
            return this.doSubCommand(its[1],its[0]);
        }
        else{
            return this.doSubCommand(its[2]);
        }
    }

    public String checkItemInHand(String it){
        String[] its = it.split("\\|");
        if(player == null){
            return "false:玩家不存在！";
        }
        boolean result;
        Item item = player.getInventory().getItemInHand();
        its[1] = api.executeReturnData(its[1],player);
        String all = String.valueOf(item.getId()) + "-" + item.getDamage() + "-" + item.getCount();
        switch(its[0]){
            case "检查all":
                result = its[1].equals(all);
                break;
            case "检查id和特殊值":
                result = its[1].equals(String.valueOf(item.getId()) + "-" + item.getDamage());
                break;
            case "检查id":
                result = its[1].equals(String.valueOf(item.getId()));
                break;
            case "检查特殊值":
                result = its[1].equals(String.valueOf(item.getDamage()));
                break;
            case "检查数量":
                result = its[1].equals(String.valueOf(item.getCount()));
                break;
            default:
                return "false:传入了未知子方法！";
        }
        if(result){
            return doSubCommand(its[2],all);
        }
        else{
            return doSubCommand(its[3]);
        }
    }

    public String checkMoney(String it){
        String[] its = it.split("\\|");
        its[0] = api.executeReturnData(its[0],this.player);
        if(this.player == null) return "false:玩家不存在！";
        int money;
        switch(api.plugin.getEconomyType()){
            case "EconomyAPI":
                money = (new BigDecimal(EconomyAPI.getInstance().myMoney(player))).intValue();
                break;
            case "Money":
                money = (new BigDecimal(Money.getInstance().getMoney(player))).intValue();
                break;
            default:
                return "false:未安装任何经济核心！";
        }
        if(money >= Integer.parseInt(its[0])){
            return doSubCommand(its[1],its[0]);
        }
        else{
            return doSubCommand(its[2]);
        }
    }

    public String doSubCommand(String cmdd){
        return doSubCommand(cmdd,"");
    }

    public String doSubCommand(String cmdd, String extraData){
        String[] multiTask = cmdd.split(",");
        for(String multi : multiTask){
            String[] cmd = multi.split("\\.");
            switch(cmd[0]){
                case "cancel":
                case "取消":
                    break;
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
                case "delitem":
                    if(extraData.equals("")){
                        return "false:非checkitem方式禁止使用delitem功能！";
                    }
                    String[] s = extraData.split(",");
                    for(String item : s){
                        String[] pli = item.split("-");
                        String r = this.removeItem(this.player,new Item(Integer.parseInt(pli[0]),Integer.parseInt(pli[1]),Integer.parseInt(pli[2])));
                        if(!r.equals("true")){
                            return r;
                        }
                    }
                    break;
                case "delmoney":
                    if(extraData.equals("")){
                        return "false:非delmoney方式禁止使用delmoney功能！";
                    }
                    switch(api.plugin.getEconomyType()){
                        case "EconomyAPI":
                            EconomyAPI.getInstance().reduceMoney(this.player,Double.parseDouble(extraData));
                            break;
                        case "Money":
                            Money.getInstance().reduceMoney(this.player,Float.parseFloat(extraData));
                            break;
                        default:
                            return "false:未安装任何经济核心！";
                    }
                    break;
            }
        }
        return "true";
    }

    public String removeItem(Player p, Item item){
        int getCount = item.getCount();
        if(getCount <= 0){
            return "false:传入数量出错！";
        }
        for(int index = 0; index < p.getInventory().getSize(); index++){
            Item setItem = p.getInventory().getItem(index);
            if(setItem.getId() == item.getId() && setItem.getDamage() == item.getDamage()){
                if(getCount >= setItem.getCount()){
                    getCount -= setItem.getCount();
                    p.getInventory().setItem(index, Item.get(Item.AIR,0,1));
                }
                else{
                    p.getInventory().setItem(index, Item.get(item.getId(),item.getDamage(),setItem.getCount() - getCount));
                }
            }
        }
        return "true";
    }

    public String checkCount(String it) {
        String[] its = it.split("\\|");
        String fuhao = api.executeCompare(its[0]);
        if(fuhao.equals("")){
            return "false:未找到任何符号！";
        }
        String[] number = its[0].split(fuhao);
        number[0] = api.executeReturnData(number[0],this.player);
        number[1] = api.executeReturnData(number[1],this.player);
        boolean result = api.calCompare(number,fuhao);
        if(result){
            return doSubCommand(its[1]);
        }
        else{
            return doSubCommand(its[2]);
        }
    }

    public String checkCountDouble(String it){
        String[] its = it.split("\\|");
        String fuhao = api.executeCompare(its[0]);
        if(fuhao.equals("")){
            return "false:未找到任何符号！";
        }
        String[] number = its[0].split(fuhao);
        number[0] = api.executeReturnData(number[0],this.player);
        number[1] = api.executeReturnData(number[1],this.player);
        boolean result = api.calCompare(number,fuhao,true);
        if(result){
            return doSubCommand(its[1]);
        }
        else{
            return doSubCommand(its[2]);
        }
    }

    public String checkGm(String it){
        String[] its = it.split("\\|");
        its[0] = api.executeReturnData(its[0],this.player);
        if(this.player == null) return "false:玩家不存在！";
        if(this.player.getGamemode() == Integer.parseInt(its[0])){
            return doSubCommand(its[1]);
        }
        else{
            return doSubCommand(its[2]);
        }
    }

    public String dropItem(String it){
        String[] its = it.split("\\|");
        its[0] = api.executeReturnData(its[0],this.player);
        its[1] = api.executeReturnData(its[1],this.player);
        String[] levelSs = its[0].split(":");
        Level level = Server.getInstance().getLevelByName(levelSs[3]);
        if(level == null){
            return "false:世界"+levelSs[3] + "不存在！";
        }
        return dropItems(executePosition(its[0]),its[1].split(","),level);
    }

    public String dropItems(Vector3 pos, String[] items, Level level){
        for(String item : items){
            String each = api.executeReturnData(item, this.player);
            String[] sp = each.split("-");
            if(sp.length == 4){
                if(!ItemPercent(sp[3])){
                    continue;
                }
            }
            level.dropItem(pos, Item.get(Integer.parseInt(sp[0]),Integer.parseInt(sp[1]),Integer.parseInt(sp[2])));
        }
        return "true";
    }

    public boolean ItemPercent(String it){
        int rdm = (new Random()).nextInt(100)+1;
        return Integer.parseInt(it) >= rdm;
    }

    public String calculatePercentTask(String it){
        String[] its = it.split("\\|");
        its[0] = api.executeReturnData(its[0],this.player);
        if(ItemPercent(its[0])){
            return doSubCommand(its[1]);
        }
        else{
            return doSubCommand(its[2]);
        }
    }
}
