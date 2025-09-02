package boyd.utils;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import boyd.exceptions.BoydException;
import boyd.tasks.Deadline;
import boyd.tasks.Task;


class ParserTest {

    @Test
    void parseDeadlineDateTimeOk() {
        Task t = Parser.parseTask("deadline return book /by 2019-12-02 18:00");
        assertTrue(t instanceof Deadline);
        assertEquals("D | 0 | return book | 2019-12-02 18:00", t.toDataString());
    }

    @Test
    void parseDeadlineMissingByThrows() {
        BoydException ex = assertThrows(BoydException.class, ()
                -> Parser.parseTask("deadline return book"));
        assertTrue(ex.getMessage().contains("/by"));
    }

    @Test
    void parseEventMissingToThrows() {
        assertThrows(BoydException.class, ()
                -> Parser.parseTask("event meeting /from 2025-10-20 10:20"));
    }
}
