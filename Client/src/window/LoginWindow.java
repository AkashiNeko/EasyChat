package src.window;
import src.event.ECConfigs;
import src.event.LoginEvent;
import src.event.Md5Hash;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * @BelongsProject: EasyChat
 * @FileName: LoginWindow
 * @Author: Akashi
 * @Version: 1.0
 * @Description: 用户登录窗口
 */

public class LoginWindow {
    public final JFrame loginFrame;
    public final JTextField usernameLoginField;
    public final JPasswordField passwordLoginField;
    public LoginWindow() {
        // 登录窗口
        loginFrame = new JFrame("EasyChat");
        loginFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        loginFrame.setSize(470, 420);
        loginFrame.setResizable(false);
        loginFrame.setLocationRelativeTo(null);
        Image windowIcon = new ImageIcon("images/logo.ico").getImage();
        loginFrame.setIconImage(windowIcon);

        // 登录窗口
        JPanel loginPanel = new JPanel();
        loginPanel.setLayout(null);
        loginPanel.setBackground(new Color(25, 25, 25));

        // logo
        JLabel imageLabel = new JLabel();
        loginPanel.add(imageLabel);
        imageLabel.setBounds(30, 10, 500, 150);
        ImageIcon icon = new ImageIcon("images/login.png");
        Image image = icon.getImage();
        icon.setImage(image.getScaledInstance(400, 120, Image.SCALE_DEFAULT));
        imageLabel.setIcon(icon);

        // 用户名标签
        JLabel usernameLoginLabel = new JLabel("账号");
        usernameLoginLabel.setFont(new Font("Microsoft YaHei", Font.PLAIN, 20));
        usernameLoginLabel.setForeground(Color.WHITE);
        usernameLoginLabel.setBounds(50, 170, 60, 35);
        loginPanel.add(usernameLoginLabel);

        // 用户名输入框
        usernameLoginField = new JTextField(30);
        TextFieldSetting.setMaxCharLimit(usernameLoginField, 20);
        TextFieldSetting.setLimitedChars(usernameLoginField);
        usernameLoginField.setFont(new Font("Courier New", Font.PLAIN, 20));
        usernameLoginField.setBounds(110, 170, 300, 38);
        usernameLoginField.setForeground(Color.WHITE);
        usernameLoginField.setBackground(new Color(100,100,100));
        usernameLoginField.getCaret().setBlinkRate(300);
        usernameLoginField.setMargin(new Insets(0, 5, 0, 5));

        loginPanel.add(usernameLoginField);

        // 密码标签
        JLabel passwordLoginLabel = new JLabel("密码");
        passwordLoginLabel.setFont(new Font("Microsoft YaHei", Font.PLAIN, 20));
        passwordLoginLabel.setForeground(new Color(255, 255, 255));
        passwordLoginLabel.setBounds(50, 230, 60, 35);
        loginPanel.add(passwordLoginLabel);

        // 密码输入框
        passwordLoginField = new JPasswordField(30);
        passwordLoginField.setFont(new Font("Courier New", Font.PLAIN, 20));
        passwordLoginField.setBounds(110, 230, 300, 38);
        passwordLoginField.setForeground(Color.WHITE);
        passwordLoginField.setBackground(new Color(100,100,100));
        passwordLoginField.getCaret().setBlinkRate(300);
        passwordLoginField.setMargin(new Insets(0, 5, 0, 5));
        loginPanel.add(passwordLoginField);

        usernameLoginField.addActionListener(usernameLoginFieldAction -> {
            if (!usernameLoginField.getText().isEmpty())
                passwordLoginField.requestFocus();
        });

        // 登录按钮
        JButton loginButton = new JButton("登录");
        loginButton.setFont(new Font("Microsoft YaHei", Font.PLAIN, 20));
        loginButton.setBounds(50, 300, 160, 42);
        loginButton.setForeground(Color.WHITE);
        loginButton.setBackground(Color.BLACK);
        loginButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                loginButton.setBackground(Color.GRAY);
            }
            @Override
            public void mouseExited(MouseEvent e) {
                loginButton.setBackground(Color.BLACK);
            }
        });
        loginButton.addActionListener(loginButtonPushed -> login());
        loginPanel.add(loginButton);

        passwordLoginField.addActionListener(usernameLoginFieldAction -> {
            if (passwordLoginField.getPassword().length != 0)
                login();
        });

        // 注册
        JButton registerButton = new JButton("注册");
        registerButton.setFont(new Font("Microsoft YaHei", Font.PLAIN, 20));
        registerButton.setBounds(250, 300, 160, 42);
        registerButton.setForeground(Color.WHITE);
        registerButton.setBackground(Color.BLACK);
        registerButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                registerButton.setBackground(Color.GRAY);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                registerButton.setBackground(Color.BLACK);
            }
        });
        registerButton.addActionListener(e -> {
            // 关闭登录窗口，打开注册窗口
            loginFrame.dispose();
            new RegisterWindow(this);
        });
        loginPanel.add(registerButton);
        loginFrame.add(loginPanel);
        loginFrame.setVisible(true);

        String localUsername = ECConfigs.getConfig("username");
        String localPasswordMD5 = ECConfigs.getConfig("password");
        if (localUsername != null && localPasswordMD5 != null) {
            usernameLoginField.setText(localUsername);
            passwordLoginField.setText("@local_password");
        }
    }

    private void login() {
        // TODO: 处理登录逻辑
        // 用户名判空
        String username = usernameLoginField.getText();
        if (username.isEmpty()) {
            JOptionPane.showMessageDialog(null, "请输入用户名", "用户名为空", JOptionPane.ERROR_MESSAGE);
            return;
        }
        // 用户名过长
        if (username.length() > 20) {
            JOptionPane.showMessageDialog(null, "用户名过长", "用户名过长", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // 用户名为纯数字
        if (username.matches("\\d+")) {
            JOptionPane.showMessageDialog(null, "用户名不能为纯数字", "用户名错误", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // 密码判空
        String password = new String(passwordLoginField.getPassword());
        if (password.isEmpty()) {
            JOptionPane.showMessageDialog(null, "请输入密码", "密码为空", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String passwordMD5;
        if (password.equals("@local_password")) {
            passwordMD5 = ECConfigs.getConfig("password");
        } else {
            passwordMD5 = Md5Hash.getMd5(password);
        }
        // 登录
        LoginEvent loginEvent = new LoginEvent(username, passwordMD5);
        int port = loginEvent.login();
        if (port != -1) {
            loginFrame.dispose();
            MainWindow window = new MainWindow(port, username, passwordMD5);
            window.setVisible(true);
        }
    }
}
