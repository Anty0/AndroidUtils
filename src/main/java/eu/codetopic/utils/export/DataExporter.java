package eu.codetopic.utils.export;

import android.support.annotation.Nullable;
import android.support.annotation.WorkerThread;

import eu.codetopic.utils.thread.progress.ProgressReporter;

public interface DataExporter {

    @WorkerThread
    void export(ExportManager exportManager, @Nullable ProgressReporter reporter) throws Throwable;
}
