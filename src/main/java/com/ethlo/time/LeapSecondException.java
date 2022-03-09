package com.ethlo.time;

/*-
 * #%L
 * Internet Time Utility
 * %%
 * Copyright (C) 2017 - 2019 Morten Haraldsen (ethlo)
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

import java.time.OffsetDateTime;

public class LeapSecondException extends RuntimeException
{
    private final int secondsInMinute;
    private final boolean isVerifiedValidLeapYearMonth;
    private final OffsetDateTime nearestDateTime;

    public LeapSecondException(OffsetDateTime nearestDateTime, int secondsInMinute, final boolean isVerifiedValidLeapYearMonth)
    {
        this.nearestDateTime = nearestDateTime;
        this.secondsInMinute = secondsInMinute;
        this.isVerifiedValidLeapYearMonth = isVerifiedValidLeapYearMonth;
    }

    public int getSecondsInMinute()
    {
        return secondsInMinute;
    }

    public OffsetDateTime getNearestDateTime()
    {
        return nearestDateTime;
    }

    public boolean isVerifiedValidLeapYearMonth()
    {
        return isVerifiedValidLeapYearMonth;
    }
}
