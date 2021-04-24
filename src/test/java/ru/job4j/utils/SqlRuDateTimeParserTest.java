package ru.job4j.utils;

import org.junit.Before;
import org.junit.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.junit.Assert.*;

public class SqlRuDateTimeParserTest {
    private DateTimeParser parser;

    @Before
    public void initParser() {
        parser = new SqlRuDateTimeParser();
    }

    @Test(expected = IllegalArgumentException.class)
    public void whenEmptyDate() {
        parser.parse("");
    }

    @Test
    public void whenParseToday() {
        LocalDateTime rsl = parser.parse("сегодня, 20:00");
        assertEquals(LocalDate.now().atTime(20, 0), rsl);
    }

    @Test
    public void whenParseYesterday() {
        LocalDateTime rsl = parser.parse("вчера, 20:00");
        LocalDateTime expected = LocalDate.now().minusDays(1).atTime(20, 0);
        assertEquals(expected, rsl);
    }

    @Test
    public void whenParseDateTime() {
        LocalDateTime rsl = parser.parse("12 сен 08, 19:42");
        LocalDateTime expected = LocalDateTime.of(2008, 9, 12, 19, 42);
        assertEquals(expected, rsl);
    }
}