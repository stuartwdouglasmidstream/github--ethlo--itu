package com.ethlo.time;

/*-
 * #%L
 * Internet Time Utility
 * %%
 * Copyright (C) 2017 - 2022 Morten Haraldsen (ethlo)
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

import java.time.DateTimeException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.YearMonth;
import java.util.Optional;

/**
 * Holder class for parsed data. The {@link #getField()} contains the last found field, like MONTH, MINUTE, SECOND.
 */
public class DateTime
{
    private final Field field;
    private final int year;
    private final int month;
    private final int day;
    private final int hour;
    private final int minute;
    private final int second;
    private final int nano;
    private final TimezoneOffset offset;

    public DateTime(final Field field, final int year, final int month, final int day, final int hour, final int minute, final int second, final int nano, final TimezoneOffset offset)
    {
        this.field = field;
        this.year = year;
        this.month = assertSize(month, 1, 12, Field.MONTH);
        this.day = assertSize(day, 1, 31, Field.DAY);
        this.hour = assertSize(hour, 0, 23, Field.HOUR);
        this.minute = assertSize(minute, 0, 59, Field.MINUTE);
        this.second = assertSize(second, 0, 60, Field.SECOND);
        this.nano = assertSize(nano, 0, 999_999_999, Field.NANO);
        this.offset = offset;
    }

    public static DateTime of(int year, int month, int day, int hour, int minute, int second, int nanos, TimezoneOffset offset)
    {
        return new DateTime(Field.SECOND, year, month, day, hour, minute, second, nanos, offset);
    }

    public static DateTime ofYear(int year)
    {
        return new DateTime(Field.YEAR, year, 0, 0, 0, 0, 0, 0, null);
    }

    public static DateTime ofYearMonth(int years, int months)
    {
        return new DateTime(Field.MONTH, years, months, 0, 0, 0, 0, 0, null);
    }

    public static DateTime ofDate(int years, int months, int days)
    {
        return new DateTime(Field.DAY, years, months, days, 0, 0, 0, 0, null);
    }

    public static DateTime of(int years, int months, int days, int hours, int minute, TimezoneOffset offset)
    {
        return new DateTime(Field.MINUTE, years, months, days, hours, minute, 0, 0, offset);
    }

    public static DateTime of(OffsetDateTime dateTime)
    {
        return DateTime.of(dateTime.getYear(), dateTime.getMonthValue(), dateTime.getDayOfMonth(), dateTime.getHour(), dateTime.getMinute(), dateTime.getSecond(), dateTime.getNano(), TimezoneOffset.of(dateTime.getOffset()));
    }

    private int assertSize(int value, int min, int max, Field field)
    {
        if (value > max)
        {
            throw new DateTimeException("Field " + field.name() + " out of bounds. Expected " + min + "-" + max + ", got " + value);
        }
        return value;
    }

    public boolean includesGranularity(Field field)
    {
        return field.ordinal() <= this.field.ordinal();
    }

    public int getYear()
    {
        return year;
    }

    public int getMonth()
    {
        return month;
    }

    public int getDayOfMonth()
    {
        return day;
    }

    public int getHour()
    {
        return hour;
    }

    public int getMinute()
    {
        return minute;
    }

    public int getSecond()
    {
        return second;
    }

    public long getNano()
    {
        return nano;
    }

    /**
     * Returns the time offset, if available
     *
     * @return the time offset, if available
     */
    public Optional<TimezoneOffset> getOffset()
    {
        return Optional.ofNullable(offset);
    }

    /**
     * Creates a {@link YearMonth} discarding any higher resolution fields
     *
     * @return the {@link YearMonth}
     */
    public YearMonth toYearMonth()
    {
        if (field.ordinal() >= Field.MONTH.ordinal())
        {
            return YearMonth.of(year, month);
        }
        throw new DateTimeException("Missing field for date-time, found only " + field.name().toLowerCase());
    }

    /**
     * Creates a {@link LocalDateTime} discarding any timezone information
     *
     * @return the {@link LocalDateTime}
     */
    public LocalDateTime toLocalDatetime()
    {
        if (field.ordinal() >= Field.MINUTE.ordinal())
        {
            return LocalDateTime.of(year, month, day, hour, minute, second, nano);
        }
        throw new DateTimeException("Missing field for date-time, found only " + field.name().toLowerCase());
    }

    /**
     * Creates an {@link OffsetDateTime}
     *
     * @return the {@link OffsetDateTime}
     */
    public OffsetDateTime toOffsetDatetime()
    {
        if (field.ordinal() >= Field.MINUTE.ordinal() && offset != null)
        {
            return OffsetDateTime.of(year, month, day, hour, minute, second, nano, offset.asJavaTimeOffset());
        }
        else if (offset == null)
        {
            throw new DateTimeException("No zone offset information found");
        }
        throw new DateTimeException("Missing field for date-time, found only " + field.name().toLowerCase());
    }

    /**
     * Creates a {@link LocalDate}, discarding any higher resolution fields
     *
     * @return the {@link LocalDate}
     */
    public LocalDate toLocalDate()
    {
        if (field.ordinal() >= Field.DAY.ordinal())
        {
            return LocalDate.of(year, month, day);
        }
        throw new DateTimeException("Missing field day for LocalDate, found only " + field.name().toLowerCase());
    }

    /**
     * Returns the minimum field found during parsing
     *
     * @return The minimum field found
     */
    public Field getField()
    {
        return field;
    }

    public DateTime assertMinGranularity(Field field)
    {
        if (!includesGranularity(field))
        {
            throw new DateTimeException("No " + field.name() + " field found");
        }
        return this;
    }
}
