package src.chat;
import javax.swing.*;

/**
 * @BelongsProject: EasyChat
 * @FileName: FriendInfo
 * @Author: Akashi
 * @Version: 1.0
 * @Description: 好友信息，用于friendMap中通过好友用户名查找好友按钮、输入框和聊天框内容
 */

public class FriendInfo {
    public JButton button;
    public String inputBuffer;
    public Records records;

    public FriendInfo(JButton button) {
        this.button = button;
        this.inputBuffer = "";
        this.records = new Records();
    }

    public JButton getButton() {
        return button;
    }

    public String getInputBuffer() {
        return inputBuffer;
    }

    public Records getRecords() {
        return records;
    }
}
