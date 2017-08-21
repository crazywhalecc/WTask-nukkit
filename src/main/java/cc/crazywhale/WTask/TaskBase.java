package cc.crazywhale.WTask;

import cn.nukkit.item.Item;
import cn.nukkit.level.Position;

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
}
