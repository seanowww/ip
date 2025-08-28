package boyd.utils;

import boyd.tasks.*;
import boyd.exceptions.BoydException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ParserTest {
    @Test
    void parse_deadline_dateTime_ok() {
        Task t = Parser.parseTask("deadline return book /by 2019-12-02 18:00");
        assertTrue(t instanceof Deadline);
        assertEquals("D | 0 | return book | 2019-12-02 18:00", t.toDataString());
    }

    @Test
    void parse_deadline_missingBy_throws() {
        var ex = assertThrows(BoydException.class,
                () -> Parser.parseTask("deadline return book"));
        assertTrue(ex.getMessage().contains("/by"));
    }

    @Test
    void parse_event_missingTo_throws() {
        assertThrows(BoydException.class,
                () -> Parser.parseTask("event meeting /from 2025-10-20 10:20"));
    }
}