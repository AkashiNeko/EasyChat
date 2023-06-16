package src.event;
import src.networker.Sender;

import javax.swing.*;

/**
 * @BelongsProject: EasyChat
 * @FileName: RegisterEvent
 * @Author: Akashi
 * @Version: 1.0
 * @Description: 用户注册事件
 */
public class RegisterEvent {
    String username;
    String password;
    String email;

    public RegisterEvent(String username, String password, String email) {
        this.username = username;
        this.password = password;
        this.email = email;
    }

    public boolean register() {
        String passwordMD5 = Md5Hash.getMd5(password);
        Sender sender = new Sender();
        String ret = sender.send("register:" + passwordMD5 + ":" + username + ":" + email);

        // 用户名已存在
        if (ret.equals("error:username_exist")) {
            JOptionPane.showMessageDialog(null, "用户名已存在", "用户名已存在", JOptionPane.ERROR_MESSAGE);
            return false;
        }

        // 未知错误
        if (ret.equals("error:unknown")) {
            JOptionPane.showMessageDialog(null, "未知错误", "未知错误", JOptionPane.ERROR_MESSAGE);
            return false;
        }

        return ret.equals("true");
    }
}
