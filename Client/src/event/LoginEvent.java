package src.event;
import src.networker.Sender;

import javax.swing.*;

/**
 * @BelongsProject: EasyChat
 * @FileName: LoginEvent
 * @Author: Akashi
 * @Version: 1.0
 * @Description: 用户登录事件
 */
public class LoginEvent {
    String username;
    String passwordMD5;

    public LoginEvent(String username, String passwordMD5) {
        this.username = username;
        this.passwordMD5 = passwordMD5;
    }

    public int login(){
        Sender sender = new Sender();
        String ret = sender.send("login:" + passwordMD5 + ":" + username);

        // 用户不存在
        if (ret.equals("error:not_exist")) {
            JOptionPane.showMessageDialog(null, "用户不存在，请注册", "用户不存在", JOptionPane.ERROR_MESSAGE);
            return -1;
        }

        // 密码错误
        if (ret.equals("error:wrong_password")) {
            JOptionPane.showMessageDialog(null, "密码错误", "密码错误", JOptionPane.ERROR_MESSAGE);
            return -1;
        }

        // 未知错误
        if (ret.equals("error:unknown")) {
            JOptionPane.showMessageDialog(null, "未知错误", "未知错误", JOptionPane.ERROR_MESSAGE);
            return -1;
        }

        String[] args = ret.split(":");
        if (args.length != 2 || !args[0].equals("true")) {
            return -1;
        }
        ECConfigs.setUserConfig("username", username);
        ECConfigs.setUserConfig("password", passwordMD5);
        return Integer.parseInt(args[1]);
    }
}
