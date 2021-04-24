package ru.job4j.utils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class SqlRuDateTimeParser implements DateTimeParser {
    private final static String TODAY = "сегодня";
    private final static String YESTERDAY = "вчера";
    private final static String TIME_FORMAT = "HH:mm";
    private final static String DATE_FORMAT = "d MMMM yy, " + TIME_FORMAT;

    private final Map<String, String> months = new HashMap<>();

    public SqlRuDateTimeParser() {
        months.put("янв", "января");
        months.put("фев", "февраля");
        months.put("мар", "марта");
        months.put("апр", "апреля");
        months.put("май", "мая");
        months.put("июн", "июня");
        months.put("июл", "июля");
        months.put("авг", "августа");
        months.put("сен", "сентября");
        months.put("окт", "октября");
        months.put("ноя", "ноября");
        months.put("дек", "декабря");
    }

    @Override
    public LocalDateTime parse(String parse) {
        checkDateString(parse);

        LocalDateTime rsl;

        if (parse.contains(TODAY)) {
            rsl = parseToday(parse);
        } else if (parse.contains(YESTERDAY)) {
            rsl = parseYesterday(parse);
        } else {
            rsl = parseDateTime(prepareFormat(parse), DATE_FORMAT);
        }

        return rsl;
    }

    private String prepareFormat(String parse) {
        for (String key : months.keySet()) {
            if (parse.contains(key)) {
                parse = parse.replace(key, months.get(key));
                break;
            }
        }

        return parse;
    }

    private LocalDateTime parseDateTime(String parse, String format) {
        Locale locale = new Locale("ru" , "RU");
        DateTimeFormatter pattern = DateTimeFormatter.ofPattern(format, locale);

        return LocalDateTime.parse(parse, pattern);
    }

    private LocalDateTime parseToday(String parse) {
        LocalDate today = LocalDate.now();
        DateTimeFormatter f = DateTimeFormatter.ofPattern(TODAY + ", " + TIME_FORMAT);
        LocalTime time = LocalTime.parse(parse, f);

        return today.atTime(time);
    }

    private LocalDateTime parseYesterday(String parse) {
        LocalDate yesterday = LocalDate.now().minusDays(1);
        DateTimeFormatter f = DateTimeFormatter.ofPattern(YESTERDAY + ", " + TIME_FORMAT);
        LocalTime time = LocalTime.parse(parse, f);

        return yesterday.atTime(time);
    }

    private void checkDateString(String parse) {
        if (parse.isEmpty()) {
            throw new IllegalArgumentException("Empty date string");
        }
    }
}
