package boyd.utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import boyd.tasks.Deadline;
import boyd.tasks.Event;
import boyd.tasks.Task;
import boyd.tasks.ToDo;

public class Storage {

    public List<Task> load(String filePath) {
        List<Task> taskList = new ArrayList<>();
        File file = new File(filePath);
        if (!file.exists()) {
            return taskList; // start empty
        }
        try (Scanner scanner = new Scanner(file)) {
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                if (line.isBlank()) {
                    continue;
                }
                Task task = dataStringToTask(line);
                taskList.add(task);
            }
        } catch (FileNotFoundException e) {
            System.out.println("Error loading file: " + e.getMessage());
        }
        return taskList;
    }

    public void save(List<? extends Task> tasks) {
        try {
            File saveFile = new File("./data/boyd.txt");
            File dir = saveFile.getParentFile();
            if (dir != null) {
                dir.mkdirs();
            }
            try (FileWriter writer = new FileWriter(saveFile, false)) {
                for (Task t : tasks) {
                    writer.write(t.toDataString());
                    writer.write(System.lineSeparator());
                }
            }
        } catch (IOException e) {
            System.out.println("Failed to save tasks: " + e.getMessage());
        }
    }

    private Task dataStringToTask(String line) {
        if (line == null || line.isBlank()) {
            throw new RuntimeException("Empty line in save file");
        }

        // Example: "T | 1 | desc | extra | extra"
        String[] parts = line.split("\\s*\\|\\s*");
        if (parts.length < 3) {
            throw new RuntimeException("Bad line (need at least 3 fields): " + line);
        }

        String type = parts[0];
        boolean done = parseDone(parts[1]);   // "0"/"1"
        String desc = parts[2];

        Task task;
        switch (type) {
        case "T": // ToDo
            task = new ToDo(desc);
            break;

        case "D": { // Deadline: "yyyy-MM-dd HH:mm" OR "yyyy-MM-dd"
            if (parts.length < 4) {
                throw new RuntimeException("Deadline missing due date: " + line);
            }
            String[] dateTime = parts[3].trim().split("\\s+", 2);
            String date = dateTime[0];
            String time = (dateTime.length == 2) ? dateTime[1] : "00:00";
            task = new Deadline(desc, date, time);
            break;
        }

        case "E": {
            if (parts.length < 4) {
                throw new RuntimeException("Event missing start/end: " + line);
            }
            // Split on " - " (with spaces) so we don't break on date hyphens.
            String[] range = parts[3].trim().split("\\s+-\\s+", 2);
            if (range.length < 2) {
                throw new RuntimeException("Event start/end should be 'from - to': " + line);
            }
            String from = range[0].trim();
            String to = range[1].trim();
            task = new Event(desc, from, to);
            break;
        }

        default:
            throw new RuntimeException("Unknown task type '" + type + "' in line: " + line);
        }

        if (done) {
            task.markAsDone();
        }
        return task;
    }

    private boolean parseDone(String s) {
        String v = s.trim();
        if (v.equals("1")) {
            return true;
        }
        if (v.equals("0")) {
            return false;
        }
        throw new RuntimeException("Done flag must be 0 or 1, got: " + s);
    }
}
