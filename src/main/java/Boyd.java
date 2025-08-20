import java.util.Scanner;

public class Boyd {
    public static void main(String[] args) {
        /*String logo = " ____        _        \n"
                + "|  _ \\ _   _| | _____ \n"
                + "| | | | | | | |/ / _ \\\n"
                + "| |_| | |_| |   <  __/\n"
                + "|____/ \\__,_|_|\\_\\___|\n";
        System.out.println("Hello from\n" + logo);*/
        greet();
        echo();
    }
    public static void greet() {
        String chatbotName = "Boyd";
        String line = "____________________________________________________________";
        System.out.println(line);
        System.out.println("Hello! I'm " + chatbotName + "!\nWhat can I do for you?");
        System.out.println(line);
    }
    public static void bye() {
        String line = "____________________________________________________________";
        System.out.println(line);
        System.out.println("Bye. Hope to see you again soon!");
        System.out.println(line);
    }

    public static void echo() {
        String chatbotName = "Boyd";
        String line = "____________________________________________________________";
        Scanner scanner = new Scanner(System.in);
        String command = "";
        while (true) {
            command = scanner.nextLine();
            if (command.equalsIgnoreCase("bye")) {
                break;
            }
            System.out.println(line);
            System.out.println(command);
            System.out.println(line);
        }
        bye();
        }
}