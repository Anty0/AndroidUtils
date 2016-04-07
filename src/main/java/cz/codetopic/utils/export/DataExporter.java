package cz.codetopic.utils.export;

import android.support.annotation.Nullable;
import android.support.annotation.WorkerThread;

import cz.codetopic.utils.thread.ProgressReporter;

public interface DataExporter {

    @WorkerThread
    void export(ExportManager exportManager, @Nullable ProgressReporter reporter) throws Throwable;
}
