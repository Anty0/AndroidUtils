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
