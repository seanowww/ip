package boyd.utils;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.Test;

import boyd.tasks.ToDo;

class FakeStorage extends Storage {
    private int saves = 0;

    FakeStorage() {
        super();
    }

    @Override
    public void save(List<? extends boyd.tasks.Task> tasks) { // space before {
        saves++;
    }

    public int getSaves() {
        return saves;
    }
}

class TaskListTest {
    @Test
    void add_callsSave_andIncreasesSize() {
        FakeStorage fs = new FakeStorage();
        TaskList list = new TaskList(List.of(), fs);

        list.add(new ToDo("x"));
        assertEquals(1, list.size());
        assertEquals(1, fs.getSaves());
    }

    @Test
    void remove_outOfRange_throws() {
        FakeStorage fs = new FakeStorage();
        TaskList list = new TaskList(List.of(), fs);
        var ex = assertThrows(boyd.exceptions.BoydException.class, () -> list.remove(1));
        assertTrue(ex.getMessage().contains("Invalid item number!"));
    }
}
