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

/**
 * Persists and restores {@link Task} data from a simple line-based text file.
 *
 * <p><strong>File location:</strong> {@code ./data/boyd.txt}</p>
 *
 * <p><strong>File format (one task per line):</strong></p>
 * <pre>
 * T | &lt;done&gt; | &lt;description&gt;
 * D | &lt;done&gt; | &lt;description&gt; | &lt;yyyy-MM-dd HH:mm&gt;
 * E | &lt;done&gt; | &lt;description&gt; | &lt;from&gt; - &lt;to&gt;
 * </pre>
 * where {@code <done>} is {@code 0} (not done) or {@code 1} (done).
 *
 * <p><strong>Notes:</strong> whitespace around {@code |} is ignored; the save
 * operation overwrites the file.</p>
 */
public class Storage {

    /**
     * Reads tasks from the given file path.
     *
     * <p>If the file does not exist, an empty list is returned. Blank lines are
     * ignored. Malformed lines throw a {@link RuntimeException}.</p>
     *
     * @param filePath path to the text file (e.g., {@code ./data/boyd.txt})
     * @return list of tasks reconstructed from the file; never {@code null}
     * @throws IllegalArgumentException if {@code filePath} is {@code null} or blank
     */
    public List<Task> load(String filePath) {
        if (filePath == null || filePath.isBlank()) {
            throw new IllegalArgumentException("filePath must be non-null and non-blank");
        }

        List<Task> taskList = new ArrayList<>();
        File file = new File(filePath);

        if (!file.exists()) {
            return taskList;
        }

        try (Scanner scanner = new Scanner(file)) {
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                if (line.isBlank()) {
                    continue;
                }
                Task task = dataStringToTask(line);
                assert task != null : "Parser must not return null";
                taskList.add(task);
            }
        } catch (FileNotFoundException e) {
            // Unlikely given exists() check, but environment could race
            throw new RuntimeException("File disappeared during load: " + filePath, e);
        }

        // Post-conditions: list contains no nulls
        for (Task t : taskList) {
            assert t != null : "taskList must not contain null elements";
        }
        return taskList;
    }

    /**
     * Saves all tasks to {@code ./data/boyd.txt}, creating the {@code ./data} folder
     * if needed. Each task is written via {@link Task#toDataString()} followed by
     * the platform line separator. The file is <em>overwritten</em> on each call.
     *
     * @param tasks tasks to persist (order preserved)
     * @throws IllegalArgumentException if {@code tasks} is {@code null} or contains {@code null}
     */
    public void save(List<? extends Task> tasks) {
        if (tasks == null) {
            throw new IllegalArgumentException("tasks must not be null");
        }
        for (Task t : tasks) {
            if (t == null) {
                throw new IllegalArgumentException("tasks must not contain null elements");
            }
        }

        File saveFile = new File("./data/boyd.txt");
        File dir = saveFile.getParentFile();

        try {
            if (dir != null && !dir.exists() && !dir.mkdirs()) {
                throw new IOException("Could not create data directory: " + dir);
            }

            try (FileWriter writer = new FileWriter(saveFile, false)) {
                for (Task t : tasks) {
                    String line = t.toDataString();
                    // Internal invariant: serialization must be non-blank
                    assert line != null && !line.isBlank()
                            : "Task.toDataString() must return non-blank content";
                    writer.write(line);
                    writer.write(System.lineSeparator());
                }
            }

            // Best-effort postcondition: file should exist after a successful write
            assert saveFile.exists() : "Save file should exist after save()";
        } catch (IOException e) {
            // Caller can decide how to surface this (UI/log); keep message specific
            throw new RuntimeException("Failed to save tasks to " + saveFile.getPath(), e);
        }
    }

    /**
     * Parses one stored line into a {@link Task}.
     *
     * <p>Throws {@link RuntimeException} for malformed external data. Uses assertions
     * for parser invariants.</p>
     *
     * @param line one line from the save file
     * @return a reconstructed {@link Task}
     * @throws RuntimeException if the line is malformed
     */
    private Task dataStringToTask(String line) {
        if (line == null || line.isBlank()) {
            throw new RuntimeException("Empty line in save file");
        }

        // Example: "T | 1 | desc | extra | extra"
        String[] parts = line.split("\\s*\\|\\s*");
        if (parts.length < 3) {
            throw new RuntimeException("Bad line (need at least 3 fields): " + line);
        }
        // Parser invariant after length check: first three tokens present
        assert parts[0] != null && parts[1] != null && parts[2] != null
                : "First three fields must be present";

        String type = parts[0];
        boolean done = parseDone(parts[1]);
        String desc = parts[2];

        Task task;
        switch (type) {
        case "T":
            task = new ToDo(desc);
            break;
        case "D": {
            if (parts.length < 4) {
                throw new RuntimeException("Deadline missing due date: " + line);
            }
            String[] dateTime = parts[3].trim().split("\\s+", 2);
            String date = dateTime[0];
            String time = (dateTime.length == 2) ? dateTime[1] : "00:00";
            assert !date.isBlank() && !time.isBlank()
                    : "Deadline date/time tokens must be non-blank";
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
            assert !from.isBlank() && !to.isBlank()
                    : "Event 'from' and 'to' tokens must be non-blank";
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

    /**
     * Parses the done flag from the file format.
     *
     * @param s {@code "0"} for not done, {@code "1"} for done (whitespace allowed)
     * @return {@code true} if done, {@code false} if not done
     * @throws RuntimeException if the flag is not {@code "0"} or {@code "1"}
     */
    private boolean parseDone(String s) {
        assert s != null : "parseDone must be called with a non-null token";
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
