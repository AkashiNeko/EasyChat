package src;
import java.io.*;
import java.util.Properties;
import java.util.Scanner;

/**
 * @BelongsProject: EasyChat
 * @FileName: DatabaseConfig
 * @Author: Akashi
 * @Version: 1.0
 * @Description: 数据库配置
 */

public class DatabaseConfig {
    private static String host = "";
    private static String port = "";
    private static String database = "";
    private static String username = "";
    private static String password = "";
    private static String useSSL = "";

    public DatabaseConfig(String filePath) {
        Properties prop = new Properties();
        FileInputStream input = null;
        try {
            input = new FileInputStream(filePath);
            // load a properties file
            prop.load(input);
            // get the property value and print it out
            host = prop.getProperty("host");
            port = prop.getProperty("port");
            database = prop.getProperty("database");
            username = prop.getProperty("username");
            password = prop.getProperty("password");
            useSSL = prop.getProperty("usessl");
        } catch (IOException ex) {
            Logger.println("File open failed: " + ex.getMessage());
            inputFromUser();
            try {
                this.save(filePath);
                Logger.println("config save successful");
            }
            catch (IOException e) {
                Logger.println("fail to save: " + e.getMessage());
                System.exit(-1);
            }
        } finally {
            if (input != null) {
                try {
                    input.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static void inputFromUser() {
        Console console = System.console();
        if (console == null) {
            Logger.println("No console available");
            System.exit(1);
        }
        Logger.print("Do you want to input the database configuration? (Y/N): \033[33m");
        Scanner scanner = new Scanner(System.in);
        // yes/no
        String check = scanner.nextLine().toLowerCase();
        if (check.startsWith("n")) {
            Logger.println("\033[31mserver exit.\033[0m");
            System.exit(0);
        }
        // host
        Logger.print("host: \033[33m");
        host = scanner.nextLine();
        if (host.isEmpty()) {
            Logger.println("\033[32muse localhost (127.0.0.1)");
            host = "localhost";
        }
        // port
        Logger.print("port: \033[33m");
        port = scanner.nextLine();
        if (port.isEmpty()) {
            Logger.println("\033[32muse default port 3306");
            port = "3306";
        }
        // database
        database = "";
        while (database.isEmpty()) {
            Logger.print("database: \033[33m");
            database = scanner.nextLine();
            if (database.isEmpty())
                Logger.println("\033[31mdatabase name is empty, please retry..");
        }
        // username
        Logger.print("username: \033[33m");
        username = scanner.nextLine();
        if (username.isEmpty()) {
            Logger.println("\033[32muse default user \"root\"");
            username = "root";
        }
        // password
        Logger.print("\033[35mpassword: \033[0m");
        char[] passwordArray = console.readPassword();
        password = new String(passwordArray);
        if (password.isEmpty())
            Logger.println("\033[33mnotice: password is empty");
        // useSSL
        Logger.print("useSSL (false/true): \033[33m");
        useSSL = scanner.nextLine();
        if (!useSSL.equals("true"))
            useSSL = "false";
        Logger.println("\033[32mset useSSL=" + useSSL);
        scanner.close();
    }

    public static String getURL() {
        String url = "jdbc:mysql://%s:%s/%s?useSSL=%s";
        return String.format(url, host, port, database, useSSL);
    }

    public static String getUsername() {
        return username;
    }

    public static String getPassword() {
        return password;
    }

    // 将类中的所有成员属性写入到文件server.properties中的方法
    public void save(String filePath) throws IOException {
        Properties prop = new Properties();
        OutputStream out = new FileOutputStream(filePath);
        
        prop.setProperty("host", host);
        prop.setProperty("port", port);
        prop.setProperty("database", database);
        prop.setProperty("username", username);
        prop.setProperty("password", password);
        prop.setProperty("usessl", useSSL);
        
        prop.store(out, "Server Configuration");
        out.close();
    }
}
