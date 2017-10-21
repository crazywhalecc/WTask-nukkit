package cc.crazywhale.WTask;

import cc.crazywhale.WTask.interfaces.EntityDamageBase;
import cn.nukkit.Player;
import cn.nukkit.event.entity.EntityDamageByEntityEvent;

public class EntityDamageAPI extends NormalTaskAPI implements EntityDamageBase {

    public EntityDamageByEntityEvent event;
    public EntityDamageAPI victimAPI = null;

    public EntityDamageAPI(EntityDamageByEntityEvent event, WTaskAPI api){
        super((Player) event.getDamager(),api);
        this.event = event;
        this.api = api;
    }

    public EntityDamageAPI(EntityDamageByEntityEvent event, WTaskAPI api, Player player){
        super(player,api);
        this.event = event;
        this.api = api;
    }

    public boolean isEntityDamageEvent(){
        return event != null;
    }

    public EntityDamageAPI getVictimAPI(){
        if(victimAPI == null){
            this.victimAPI = new EntityDamageAPI(event,api,(Player) event.getEntity());
        }
        return this.victimAPI;
    }

}
