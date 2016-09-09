package eu.codetopic.utils;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import com.android.internal.util.Predicate;

import java.io.BufferedReader;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.LinkedList;
import java.util.List;

public final class IOUtils {

    private static final int DEFAULT_BUFFER_SIZE_IN_BYTES = 8192;
    private static final Predicate<String> DEFAULT_FILTER = new Predicate<String>() {
        @Override
        public boolean apply(String s) {
            return true;
        }
    };

    private IOUtils() {
    }

    public static void safeClose(@Nullable Closeable closeable) {
        if (closeable == null) return;

        try {
            closeable.close();
        } catch (IOException ignored) {
            // We made out best effort to release this resource. Nothing more we can do.
        }
    }

    @NonNull
    public static String streamToString(@NonNull InputStream input) throws IOException {
        return streamToString(input, DEFAULT_FILTER);
    }

    @NonNull
    public static String streamToString(@NonNull InputStream input, Predicate<String> filter) throws IOException {
        final BufferedReader reader = new BufferedReader(new InputStreamReader(input), DEFAULT_BUFFER_SIZE_IN_BYTES);
        try {
            String line;
            final List<String> buffer = new LinkedList<String>();
            while ((line = reader.readLine()) != null) {
                if (filter.apply(line)) {
                    buffer.add(line);
                }
            }
            return TextUtils.join("\n", buffer);
        } finally {
            safeClose(reader);
        }
    }
}
