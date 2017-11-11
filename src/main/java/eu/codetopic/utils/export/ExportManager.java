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

package eu.codetopic.utils.export;

import android.support.annotation.Nullable;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;

import eu.codetopic.java.utils.log.Log;
import eu.codetopic.utils.AndroidUtils;
import eu.codetopic.utils.thread.progress.ProgressReporter;

public class ExportManager {

    private static final String LOG_TAG = "ExportManager";

    private File file;
    private String[] columns;
    private ArrayList<String[]> data = new ArrayList<>();

    public ExportManager(File file) {
        this.file = file;
    }

    public File getFile() {
        return file;
    }

    public void setFile(File file) {
        this.file = file;
    }

    public String[] getColumns() {
        return columns;
    }

    public void setColumns(String... columns) {
        this.columns = columns;
    }

    public ArrayList<String[]> getData() {
        return data;
    }

    public void setData(ArrayList<String[]> data) {
        this.data = data;
    }

    public void addData(String... data) {
        this.data.add(data);
    }

    public void saveFile(@Nullable ProgressReporter reporter) throws IOException {
        if (!AndroidUtils.isStorageWritable()) {
            Log.e(LOG_TAG, "saveFile - Storage is not writable");
            throw new IOException("Storage is not writable");
        }
        OutputStreamWriter writer = null;
        try {
            writer = new OutputStreamWriter(new FileOutputStream(file.getAbsoluteFile()));
            if (reporter != null) {
                reporter.startShowingProgress();
                reporter.reportProgress(0);
                reporter.setMaxProgress(data.size());
            }

            writer.write(getColumnString());
            for (String[] list : data) {
                writer.write("\n");
                writer.write(getDataString(list));
                if (reporter != null) reporter.stepProgress(1);
            }
        } finally {
            if (reporter != null) reporter.stopShowingProgress();
            try {
                if (writer != null) writer.close();
            } catch (Throwable ignored) {
            }
        }
    }

    private String prepareValue(String value) {
        value = value.replace("\"", "\"\"");
        return value;
    }

    private String getColumnString() {
        StringBuilder builder = new StringBuilder();
        for (String column : columns) {
            builder.append("\"");
            builder.append(prepareValue(column));
            builder.append("\",");
        }
        builder.deleteCharAt(builder.length() - 1);
        return builder.toString();
    }

    private String getDataString(String[] list) {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < columns.length; i++) {
            builder.append("\"");
            if (list.length - 1 >= i) {
                builder.append(prepareValue(list[i]));
            } else {
                builder.append("");
            }
            builder.append("\",");
        }
        builder.deleteCharAt(builder.length() - 1);
        return builder.toString();
    }
}
