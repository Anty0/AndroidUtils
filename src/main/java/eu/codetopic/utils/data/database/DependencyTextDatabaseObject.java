/*
 * utils
 * Copyright (C)   2017  anty
 *
 * This program is free  software: you can redistribute it and/or modify
 * it under the terms  of the GNU General Public License as published by
 * the Free Software  Foundation, either version 3 of the License, or
 * (at your option) any  later version.
 *
 * This program is distributed in the hope that it  will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied  warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.   See the
 * GNU General Public License for more details.
 *
 * You  should have received a copy of the GNU General Public License
 * along  with this program.  If not, see <http://www.gnu.org/licenses/>.
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
