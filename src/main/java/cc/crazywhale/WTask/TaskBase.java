package cc.crazywhale.WTask;

import cn.nukkit.Player;
import cn.nukkit.item.Item;
import cn.nukkit.level.Level;
import cn.nukkit.level.Position;
import cn.nukkit.math.Vector3;

interface TaskBase {
    String sendTip(String it);
    String sendPopup(String it);
    String sendMessageTo(String it);
    String sendTitle(String it);
    String sendTipTo(String it);
    String sendPopupTo(String it);
    String writePrivateData(String it);
    String writePublicData(String it);
    String teleport(String it);
    Position executePosition(String it);
    String addMoney(String it);
    String reduceMoney(String it);
    String deletePrivateData(String it);
    String runCommand(String it);
    String runConsoleCommand(String it);
    String addItem(String it);
    Item executeItem(String it);
    String setCustomSkin(String it);
    void setPermission(int perm);
    String addEffect(String it);
    String checkFinish(String it,String tn, Player p);
    String makeSound(String it);
    String setNameTag(String it);
    String manageConfig(String it);
    String makeExplosion(String it);
    String checkInventory(String it);
    String doSubCommand(String it);
    String doSubCommand(String it, String extraData);
    String removeItem(Player p, Item item);
    String checkItemInHand(String it);
    String checkMoney(String it);
    String checkCount(String it);
    String checkCountDouble(String it);
    String checkGm(String it);
    String dropItem(String it);
    String dropItems(Vector3 pos, String[] items, Level level);
    boolean ItemPercent(String it);
    String calculatePercentTask(String it);
}
