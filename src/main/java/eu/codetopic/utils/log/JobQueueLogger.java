/*
 * ApplicationPurkynka
 * Copyright (C)  2017  anty
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package eu.codetopic.utils.log;

import com.birbit.android.jobqueue.log.CustomLogger;

import java.util.Locale;

import eu.codetopic.java.utils.log.Log;

public final class JobQueueLogger implements CustomLogger {

    private static final String LOG_TAG = "JobQueue";

    @Override
    public boolean isDebugEnabled() {
        return Log.isInDebugMode();
    }

    @Override
    public void e(Throwable t, String text, Object... args) {
        Log.e(LOG_TAG, String.format(Locale.ENGLISH, text, args), t);
    }

    @Override
    public void e(String text, Object... args) {
        Log.e(LOG_TAG, String.format(Locale.ENGLISH, text, args));
    }

    @Override
    public void d(String text, Object... args) {
        Log.d(LOG_TAG, String.format(Locale.ENGLISH, text, args));
    }

    @Override
    public void v(String text, Object... args) {
        Log.v(LOG_TAG, String.format(Locale.ENGLISH, text, args));
    }
}