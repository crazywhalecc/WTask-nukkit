package cc.crazywhale.WTask.interfaces;

import cn.nukkit.event.Event;
import cn.nukkit.event.block.BlockBreakEvent;
import cn.nukkit.event.block.BlockEvent;

public interface ActTaskBase {
    boolean isBlockEvent();
    String checkBlock(String it);
    BlockEvent getBlockEvent();
    BlockBreakEvent getBlockBreakEvent();
    Event getEvent();
    String setDropItems(String it);
    String doSubCommand2(String it);
}
