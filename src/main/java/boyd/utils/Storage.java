package boyd.utils;

import boyd.tasks.Deadline;
import boyd.tasks.Event;
import boyd.tasks.Task;
import boyd.tasks.ToDo;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Storage {
    public List<Task> load(String filepath) {
        List<Task> taskList = new ArrayList<>();
        File file = new File(filepath);  // or Paths.get("data","boyd.txt").toFile()
        if (!file.exists()) {
            // No file yet → start empty
            return taskList;
        }
        try (Scanner sc = new Scanner(file)) {
            while (sc.hasNextLine()) {
                String line = sc.nextLine();
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
            // ensure ./data exists
            File saveFile = new File("./data/boyd.Boyd.txt");
            File dir = saveFile.getParentFile();
            if (dir != null) dir.mkdirs();

            // overwrite, not append (second arg = false is default, but keep it explicit)
            try (FileWriter w = new FileWriter(saveFile, /*append*/ false)) {
                for (Task t : tasks) {
                    w.write(t.toDataString());
                    w.write(System.lineSeparator());
                }
            }
        } catch (IOException e) {
            // surface a friendly message or rethrow your own DukeException
            System.out.println("Failed to save tasks: " + e.getMessage());
        }
    }

    /*public void write(String toDataString) {
        try {
            FileWriter myWriter = new FileWriter("./data/boyd.txt");
            myWriter.write(toDataString + "\n");
            myWriter.close();
            //System.out.println("Successfully wrote to the file.");
        } catch (IOException e) {
            System.out.println("An error occurred while writing to file.");
            e.printStackTrace();
        }
    }*/

    private Task dataStringToTask(String line) {
        if (line == null || line.isBlank()) {
            throw new RuntimeException("Empty line in save file");
        }

        // Split like: "T | 1 | desc | extra | extra"
        String[] parts = line.split("\\s*\\|\\s*"); // trims spaces around '|'

        if (parts.length < 3) {
            throw new RuntimeException("Bad line (need at least 3 fields): " + line);
        }

        String type = parts[0];
        boolean done = parseDone(parts[1]);  // 0/1 -> boolean
        String desc = parts[2];

        Task task;
        switch (type) {
            case "T": { // ToDo
                task = new ToDo(desc);
                break;
            }
            case "D": { // Deadline
                if (parts.length < 4) {
                    throw new RuntimeException("Deadline missing due date: " + line);
                }
                String[] dateTime = parts[3].split("\\s+", 2);
                String date = dateTime[0];
                String time = dateTime[1];
                task = new Deadline(desc, date, time);
                break;
            }
            case "E": { // Event
                if (parts.length < 4) {
                    throw new RuntimeException("Event missing start/end: " + line);
                }
                String[] dateTime = parts[3].split("-", 2);
                String from = dateTime[0].trim();
                String to = dateTime[1].trim();
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
        s = s.trim();
        if (s.equals("1")) return true;
        if (s.equals("0")) return false;
        throw new RuntimeException("Done flag must be 0 or 1, got: " + s);
    }

}
