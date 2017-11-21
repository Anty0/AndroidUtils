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