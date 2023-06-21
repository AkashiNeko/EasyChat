package src.window;
import src.event.RegisterEvent;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * @BelongsProject: EasyChat
 * @FileName: RegisterWindow
 * @Author: Akashi
 * @Version: 1.1
 * @Description: 用户注册窗口
 */
public class RegisterWindow {
    JTextField usernameRegisterField = new JTextField(20);
    JPasswordField passwordRegisterField = new JPasswordField(20);
    JTextField emailRegisterField = new JTextField(20);
    JFrame registerFrame = new JFrame("注册");
    LoginWindow loginWindow;
    public RegisterWindow(LoginWindow loginWindow) {
        this.loginWindow = loginWindow;
        // 注册窗口
        registerFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        registerFrame.setSize(470, 480);
        registerFrame.setLocationRelativeTo(null);

        JPanel registerPanel = new JPanel();
        registerPanel.setLayout(null);
        registerPanel.setLayout(null);
        registerPanel.setBackground(new Color(25, 25, 25));

        // logo
        JLabel imageLabel = new JLabel();
        registerPanel.add(imageLabel);
        imageLabel.setBounds(30, 10, 500, 150);
        ImageIcon icon = new ImageIcon("images/register.png");
        Image image = icon.getImage();
        icon.setImage(image.getScaledInstance(400, 120, Image.SCALE_DEFAULT));
        imageLabel.setIcon(icon);

        // 用户名标签
        JLabel usernameRegisterLabel = new JLabel("用户名");
        usernameRegisterLabel.setFont(new Font("Microsoft YaHei", Font.PLAIN, 20));
        usernameRegisterLabel.setForeground(new Color(255, 255, 255));
        usernameRegisterLabel.setBounds(50, 170, 60, 35);
        registerPanel.add(usernameRegisterLabel);

        // 用户名输入框
        TextFieldSetting.setMaxCharLimit(usernameRegisterField, 20);
        TextFieldSetting.setLimitedChars(usernameRegisterField);
        usernameRegisterField.setFont(new Font("Courier New", Font.PLAIN, 20));
        usernameRegisterField.setBounds(120, 170, 290, 38);
        usernameRegisterField.setForeground(Color.WHITE);
        usernameRegisterField.setBackground(new Color(100,100,100));
        usernameRegisterField.getCaret().setBlinkRate(300);
        usernameRegisterField.setMargin(new Insets(0, 5, 0, 5));
        registerPanel.add(usernameRegisterField);

        // 密码标签
        JLabel passwordRegisterLabel = new JLabel("密   码");
        passwordRegisterLabel.setFont(new Font("Microsoft YaHei", Font.PLAIN, 20));
        passwordRegisterLabel.setForeground(new Color(255, 255, 255));
        passwordRegisterLabel.setBounds(50, 230, 60, 35);
        registerPanel.add(passwordRegisterLabel);

        // 密码输入框
        TextFieldSetting.setMaxCharLimit(passwordRegisterField, 40);
        TextFieldSetting.setLimitedChars(passwordRegisterField);
        passwordRegisterField.setFont(new Font("Courier New", Font.PLAIN, 20));
        passwordRegisterField.setBounds(120, 230, 290, 38);
        passwordRegisterField.setForeground(Color.WHITE);
        passwordRegisterField.setBackground(new Color(100,100,100));
        passwordRegisterField.getCaret().setBlinkRate(300);
        passwordRegisterField.setMargin(new Insets(0, 5, 0, 5));
        registerPanel.add(passwordRegisterField);

        // 邮箱标签
        JLabel emailRegisterLabel = new JLabel("邮   箱");
        emailRegisterLabel.setFont(new Font("Microsoft YaHei", Font.PLAIN, 20));
        emailRegisterLabel.setForeground(new Color(255, 255, 255));
        emailRegisterLabel.setBounds(50, 290, 60, 35);
        registerPanel.add(emailRegisterLabel);

        // 邮箱输入框
        TextFieldSetting.setMaxCharLimit(emailRegisterField, 30);
        emailRegisterField.setFont(new Font("Courier New", Font.PLAIN, 20));
        emailRegisterField.setBounds(120, 290, 290, 38);
        emailRegisterField.setForeground(Color.WHITE);
        emailRegisterField.setBackground(new Color(100,100,100));
        emailRegisterField.getCaret().setBlinkRate(300);
        emailRegisterField.setMargin(new Insets(0, 5, 0, 5));
        registerPanel.add(emailRegisterField);

        // 确定按钮
        JButton registerConfirmButton = new JButton("确定");
        registerConfirmButton.setFont(new Font("Microsoft YaHei", Font.PLAIN, 20));
        registerConfirmButton.setBounds(50, 360, 160, 42);
        registerConfirmButton.setForeground(Color.WHITE);
        registerConfirmButton.setBackground(Color.BLACK);
        registerConfirmButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                registerConfirmButton.setBackground(Color.GRAY);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                registerConfirmButton.setBackground(Color.BLACK);
            }
        });
        registerConfirmButton.addActionListener(e -> register());
        registerPanel.add(registerConfirmButton);

        // 取消按钮
        JButton registerCancelButton = new JButton("取消");
        registerCancelButton.setFont(new Font("Microsoft YaHei", Font.PLAIN, 20));
        registerCancelButton.setBounds(250, 360, 160, 42);
        registerCancelButton.setForeground(Color.WHITE);
        registerCancelButton.setBackground(Color.BLACK);
        registerCancelButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                registerCancelButton.setBackground(Color.GRAY);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                registerCancelButton.setBackground(Color.BLACK);
            }
        });
        registerCancelButton.addActionListener(e -> {
            // 关闭注册窗口，重新打开登录窗口
            registerFrame.dispose();
            loginWindow.loginFrame.setVisible(true);
        });
        registerPanel.add(registerCancelButton);
        registerFrame.add(registerPanel);
        registerFrame.setVisible(true);
    }

    private void register() {
        // TODO: 处理注册逻辑

        // 用户名判空
        String username = usernameRegisterField.getText();
        if (username.isEmpty()) {
            JOptionPane.showMessageDialog(null, "请输入用户名", "用户名为空", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // 用户名过长
        if (username.length() > 20) {
            JOptionPane.showMessageDialog(null, "用户名过长", "用户名过长", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // 纯数字
        if (username.matches("\\d+")) {
            JOptionPane.showMessageDialog(null, "用户名不能为纯数字", "用户名错误", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // 密码判空
        char[] password = passwordRegisterField.getPassword();
        if (password.length == 0) {
            JOptionPane.showMessageDialog(null, "请输入密码", "密码为空", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // 密码太短
        if (password.length < 8) {
            JOptionPane.showMessageDialog(null, "密码太短，请重新输入", "密码太短", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // 邮箱判空
        String email = emailRegisterField.getText();
        if (email.isEmpty()) {
            JOptionPane.showMessageDialog(null, "请输入邮箱", "邮箱为空", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // 注册
        RegisterEvent event = new RegisterEvent(username, new String(password), email);
        boolean success = event.register();
        if (success) {
            // 注册成功后关闭注册窗口，重新打开登录窗口，自动填充用户名和密码
            JOptionPane.showMessageDialog(null, username + " 注册成功", "注册成功", JOptionPane.ERROR_MESSAGE);
            registerFrame.dispose();
            loginWindow.usernameLoginField.setText(usernameRegisterField.getText());
            loginWindow.passwordLoginField.setText(new String(passwordRegisterField.getPassword()));
            loginWindow.loginFrame.setVisible(true);
        }
    }
}
