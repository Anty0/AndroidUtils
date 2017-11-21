/*
 * Copyright 2017 Jiří Kuchyňka (Anty)
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package eu.codetopic.utils.data.database;

import android.content.Context;
import android.support.annotation.WorkerThread;

import com.j256.ormlite.field.DatabaseField;

import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import eu.codetopic.utils.ui.container.items.multiline.MultilineItem;

public abstract class DependencyTextDatabaseObject extends DependencyDatabaseObject implements MultilineItem {

    protected static final DateFormat TIME_FORMAT = new SimpleDateFormat("HH:mm:ss", Locale.ENGLISH);
    protected static final DateFormat DATE_FORMAT = new SimpleDateFormat("dd.MM.yyyy", Locale.ENGLISH);

    static {
        TimeZone timeZone = new TimeZone() {
            @Override
            public int getOffset(int era, int year, int month, int day, int dayOfWeek, int timeOfDayMillis) {
                return 0;
            }

            @Override
            public int getRawOffset() {
                return -1;
            }

            @Override
            public boolean inDaylightTime(Date time) {
                return false;
            }

            @Override
            public void setRawOffset(int offsetMillis) {

            }

            @Override
            public boolean useDaylightTime() {
                return false;
            }
        };
        TIME_FORMAT.setTimeZone(timeZone);
        DATE_FORMAT.setTimeZone(timeZone);
    }

    @DatabaseField
    private String text = "";


    @WorkerThread
    public void updateText(Context context) throws SQLException {
        text = getText(context);
    }

    @WorkerThread
    protected abstract String getText(Context context) throws SQLException;

    @Override
    public CharSequence getText(Context context, int position) {
        return text;
    }

}
