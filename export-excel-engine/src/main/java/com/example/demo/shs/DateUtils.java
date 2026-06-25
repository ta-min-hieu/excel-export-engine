package com.example.demo.shs;

import org.apache.commons.lang3.StringUtils;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

public class DateUtils {
    public DateUtils() {
    }

    public static Date convertStringToDate(String dateFormatPattern, String value) {
        try {
            if (!isValid(value, dateFormatPattern)) {
                return null;
            } else {
                return StringUtils.isEmpty(dateFormatPattern) ? null : (new SimpleDateFormat(dateFormatPattern)).parse(value);
            }
        } catch (Exception var3) {
            return null;
        }
    }

    public static LocalDate convertStringToLocalDate(String dateFormatPattern, String value) {
        try {
            if (!isValid(value, dateFormatPattern)) {
                return null;
            } else if (StringUtils.isEmpty(dateFormatPattern)) {
                return null;
            } else {
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern(dateFormatPattern);
                return LocalDate.parse(value, formatter);
            }
        } catch (Exception var3) {
            return null;
        }
    }

    public static LocalDateTime convertStringToLocalDateTime(String dateFormatPattern, String value) {
        try {
            if (!isValid(value, dateFormatPattern)) {
                return null;
            } else if (StringUtils.isEmpty(dateFormatPattern)) {
                return null;
            } else {
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern(dateFormatPattern);
                return LocalDateTime.parse(value, formatter);
            }
        } catch (Exception var3) {
            return null;
        }
    }

    public static String convertPatterDate(String sourcePattern, String desPattern, String value) {
        try {
            if (!StringUtils.isEmpty(sourcePattern) && !StringUtils.isEmpty(desPattern)) {
                Date date = (new SimpleDateFormat(sourcePattern)).parse(value);
                DateFormat df = new SimpleDateFormat(desPattern);
                return df.format(date);
            } else {
                return null;
            }
        } catch (Exception var5) {
            return null;
        }
    }

    public static boolean compareDate(Date firstDate, Date secondDate) {
        return firstDate.before(secondDate);
    }

    public static boolean checkIsToday(String dateFormatPattern, String date) {
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern(dateFormatPattern);
            LocalDateTime dateTime = LocalDateTime.parse(date, formatter);
            return dateTime.toLocalDate().equals(LocalDate.now());
        } catch (Exception e) {
            e.printStackTrace();
            return true;
        }
    }

    public static boolean isStartDateBeforeEndDate(String fDate, String tDate, String format) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat(format);
            Date date1 = sdf.parse(fDate);
            Date date2 = sdf.parse(tDate);
            if (date1.after(date2)) {
                return false;
            }
        } catch (ParseException ex) {
            ex.printStackTrace();
        }

        return true;
    }

    public static boolean isInDaysRange(String fDate, String tDate, int daysRange, String fomat) {
        DateTimeFormatter sdf = DateTimeFormatter.ofPattern(fomat);
        LocalDate date1 = LocalDate.parse(fDate, sdf);
        LocalDate date2 = LocalDate.parse(tDate, sdf);
        long daysBetween = ChronoUnit.DAYS.between(date1, date2);
        return daysBetween <= (long)daysRange;
    }

    public static String convertDateToString(String dateFormatPattern, Date value) {
        try {
            if (StringUtils.isEmpty(dateFormatPattern)) {
                return null;
            } else {
                DateFormat df = new SimpleDateFormat(dateFormatPattern);
                return df.format(value);
            }
        } catch (Exception var3) {
            return null;
        }
    }

    public static String convertLocalDateToString(String dateFormatPattern, LocalDate value) {
        try {
            if (StringUtils.isEmpty(dateFormatPattern)) {
                return null;
            } else {
                DateTimeFormatter df = DateTimeFormatter.ofPattern(dateFormatPattern);
                return df.format(value);
            }
        } catch (Exception var3) {
            return null;
        }
    }

    public static String convertLocalDateTimeToString(String dateFormatPattern, LocalDateTime value) {
        try {
            if (StringUtils.isEmpty(dateFormatPattern)) {
                return null;
            } else {
                DateTimeFormatter df = DateTimeFormatter.ofPattern(dateFormatPattern);
                return df.format(value);
            }
        } catch (Exception var3) {
            return null;
        }
    }

    public static long calculateDateString(String fDate, String tDate, String fomat) {
        DateTimeFormatter sdf = DateTimeFormatter.ofPattern(fomat);
        LocalDate date1 = LocalDate.parse(fDate, sdf);
        LocalDate date2 = LocalDate.parse(tDate, sdf);
        return ChronoUnit.DAYS.between(date1, date2);
    }

    public static Date trim(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(14, 0);
        calendar.set(13, 0);
        calendar.set(12, 0);
        calendar.set(10, 0);
        return calendar.getTime();
    }

    public static LocalDateTime getLocalNow() {
        return LocalDateTime.now();
    }

    public static LocalDate getCurrentDate() {
        return LocalDate.now();
    }

    public static String getNumsYear(LocalDate from, LocalDate to) {
        Period period = Period.between(from, to);
        int year = period.getYears();
        int month = period.getMonths();
        String result = "";
        if (year > 0) {
            result = String.format("%.1f năm", (double)year + (double)month / (double)12.0F);
        } else {
            result = String.format("%d tháng", month);
        }

        return result;
    }

    public static long getNumsMonth(LocalDate from, LocalDate to) {
        return ChronoUnit.MONTHS.between(from, to);
    }

    public static boolean isValid(String dateStr, String dateFormat) {
        DateFormat sdf = new SimpleDateFormat(dateFormat);
        sdf.setLenient(false);

        try {
            sdf.parse(dateStr);
            return true;
        } catch (ParseException var4) {
            return false;
        }
    }

    public static LocalDate addDays(LocalDate date, Integer periordNum, String periordType) {
        switch (periordType) {
            case "D" -> date = date.plusDays((long)periordNum);
            case "W" -> date = date.plusWeeks((long)periordNum);
            case "M" -> date = date.plusMonths((long)periordNum);
            case "Y" -> date = date.plusYears((long)periordNum);
        }

        return date;
    }

    public static LocalDate subtractDays(LocalDate date, Integer periordNum, String periordType) {
        switch (periordType) {
            case "D" -> date = date.minusDays((long)periordNum);
            case "W" -> date = date.minusWeeks((long)periordNum);
            case "M" -> date = date.minusMonths((long)periordNum);
            case "Y" -> date = date.minusYears((long)periordNum);
        }

        return date;
    }

    public static List<LocalDate> getDatesBetweenDays(LocalDate startDate, LocalDate endDate) {
        return (List)startDate.datesUntil(endDate).collect(Collectors.toList());
    }
}
