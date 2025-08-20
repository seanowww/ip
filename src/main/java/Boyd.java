import java.util.Scanner;

public class Boyd {
    private static Task[] mem = new Task[100];
    private static int itemCount = 0;
    private static String line = "____________________________________________________________";
    private static String chatbotName = "Boyd";

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
        Scanner scanner = new Scanner(System.in);
        String command = "";
        while (true) {
            command = scanner.nextLine();
            if (command.equalsIgnoreCase("bye")) {
                break;
            }
            if (command.startsWith("mark")) {
                if (command.length() == 6) {
                    int itemNo = Integer.parseInt(command.substring(5)); //convert to integer
                    if (itemNo > itemCount) {
                        System.out.println(line);
                        System.out.println("Invalid item number");
                        System.out.println(line);
                        continue;
                    }
                    if (itemCount == 0) {
                        System.out.println(line);
                        System.out.println("No items yet!");
                        System.out.println(line);
                    }
                    mem[itemNo - 1].markAsDone();
                    System.out.println(line);
                    String message = "Nice! I've marked this task as done:\n" + mem[itemNo - 1].getDescription();
                    System.out.println(message);
                    System.out.println(line);
                } else {
                    System.out.println(line);
                    System.out.println("Command should be of the format: \"mark <number>\"");
                    System.out.println(line);
                }
                continue;
            }
            if (command.equalsIgnoreCase("list")) {
                System.out.println(line);
                if (itemCount == 0) {
                    System.out.println("You haven't added any items!");
                    System.out.println(line);
                    continue;
                }
                for (int i = 0; i < itemCount; i++) {
                    System.out.println((i + 1) + ". " + mem[i].getDescription());
                }
                System.out.println(line);
                continue;
            }
            mem[itemCount] = new Task(command);
            System.out.println(line);
            System.out.println("added: " + command);
            System.out.println(line);
            itemCount++;
        }
        bye();
    }
}