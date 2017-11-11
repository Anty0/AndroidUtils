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

import android.util.Log;

import eu.codetopic.java.utils.log.base.LogLine;
import eu.codetopic.java.utils.log.base.LogTarget;
import eu.codetopic.java.utils.log.base.Priority;

public class AndroidLogTarget implements LogTarget {

    private static boolean SPLIT_TEXT = true;

    public static void setSplitTextToPartitions(boolean splitText) {
        SPLIT_TEXT = splitText;
    }

    @Override
    public void println(LogLine logLine) {
        String message = logLine.getMsgWithTr();
        if (SPLIT_TEXT && message.length() > 4000) {
            for (int i = 0, len = message.length(); i < len; i += 4000) {
                int end = i + 4000;
                if (end > len) end = len;
                printLn(logLine, message.substring(i, end));
            }
        } else {
            printLn(logLine, message);
        }
    }

    private void printLn(LogLine logLine, String message) {
        //noinspection WrongConstant
        android.util.Log.println(getPriorityId(logLine.getPriority()),
                logLine.getTag(), message);
    }

    private int getPriorityId(Priority priority) {
        switch (priority) {
            case ASSERT:
                return Log.ASSERT;
            case DEBUG:
                return Log.DEBUG;
            case ERROR:
                return Log.ERROR;
            case INFO:
                return Log.INFO;
            case VERBOSE:
                return Log.VERBOSE;
            case WARN:
                return Log.WARN;
        }
        return Log.ERROR;
    }
}
