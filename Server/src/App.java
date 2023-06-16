package src;

/**
 * @BelongsProject: EasyChat
 * @FileName: APP
 * @Author: Akashi
 * @Version: 1.0
 * @Description: 主类
 */
public class App {
    public static void main(String[] args) {
        printWelcome();
        Server server = new Server(55000);
        Logger.print("\033[0mServer start..");
        server.start();
    }

    private static void printWelcome() {
        System.out.println("+------------ EasyChat Server version 1.0 -----------+");
        System.out.println("|\033[34;1m     _____                _____ _           _       \033[0m|");
        System.out.println("|\033[34;1m    |  ___|              /  __ \\ |         | |      \033[0m|");
        System.out.println("|\033[34;1m    | |__  __ _ ___ _   _| /  \\/ |__   __ _| |_     \033[0m|");
        System.out.println("|\033[34;1m    |  __|/ _` / __| | | | |   | '_ \\ / _` | __|    \033[0m|");
        System.out.println("|\033[34;1m    | |__| (_| \\__ \\ |_| | \\__/\\ | | | (_| | |_     \033[0m|");
        System.out.println("|\033[34;1m    \\____/\\__,_|___/\\__, |\\____/_| |_|\\__,_|\\__|    \033[0m|");
        System.out.println("|\033[34;1m                     __/ |                          \033[0m|");
        System.out.println("|\033[34;1m                    |___/                           \033[0m|");
        System.out.println("+----------------------------------------------------+");
        System.out.println("| github: \033[4;33mhttps://github.com/AkashiNeko/EasyChat.git\033[0m |");
        System.out.println("+----------------------------------------------------+");
    }
}
