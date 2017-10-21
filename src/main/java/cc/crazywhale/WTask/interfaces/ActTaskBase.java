package cc.crazywhale.WTask.interfaces;

import cn.nukkit.event.Event;
import cn.nukkit.event.block.BlockBreakEvent;
import cn.nukkit.event.block.BlockEvent;
import cn.nukkit.event.player.PlayerChatEvent;

public interface ActTaskBase {
    boolean isBlockEvent();
    String checkBlock(String it);
    BlockEvent getBlockEvent();
    BlockBreakEvent getBlockBreakEvent();
    PlayerChatEvent getPlayerChatEvent();
    Event getEvent();
    String checkMessage(String it);
    String setDropItems(String it);
    String doSubCommand2(String it);
}
