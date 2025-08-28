package boyd.utils;

import org.junit.jupiter.api.Test;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

class FakeStorage extends Storage {
    int saves = 0;
    FakeStorage() {
        super();
    }
    @Override public void save(java.util.List<? extends boyd.tasks.Task> t){
        saves++;
    }
}

class TaskListTest {
    @Test
    void add_callsSave_andIncreasesSize() {
        FakeStorage fs = new FakeStorage();
        boyd.utils.TaskList list = new boyd.utils.TaskList(java.util.List.of(), fs);

        list.add(new boyd.tasks.ToDo("x"));
        assertEquals(1, list.size());
        assertEquals(1, fs.saves);
    }

    @Test
    void remove_outOfRange_throws() {
        FakeStorage fs = new FakeStorage();
        TaskList list = new TaskList(List.of(), fs);
        var ex = assertThrows(boyd.exceptions.BoydException.class, () -> list.remove(1));
        assertTrue(ex.getMessage().contains("Invalid item number!"));
    }
}
