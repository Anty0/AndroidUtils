package com.codetopic.utils.export;

import android.os.Environment;
import android.support.annotation.Nullable;

import com.codetopic.utils.Log;
import com.codetopic.utils.Objects;
import com.codetopic.utils.thread.progress.ProgressReporter;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;

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
        if (!isStorageWritable()) {
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

    private boolean isStorageWritable() {
        return Objects.equals(Environment.MEDIA_MOUNTED, Environment.getExternalStorageState());
    }
}
