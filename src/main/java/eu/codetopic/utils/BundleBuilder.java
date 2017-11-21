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

package eu.codetopic.utils;

import android.annotation.TargetApi;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Parcelable;
import android.os.PersistableBundle;
import android.util.Size;
import android.util.SizeF;
import android.util.SparseArray;

import java.io.Serializable;
import java.util.ArrayList;

public class BundleBuilder {

    private static final String LOG_TAG = "BundleBuilder";

    private final Bundle bundle;

    public BundleBuilder() {
        bundle = new Bundle();
    }

    public BundleBuilder(ClassLoader loader) {
        bundle = new Bundle(loader);
    }

    public BundleBuilder(int capacity) {
        bundle = new Bundle(capacity);
    }

    private BundleBuilder(Void editMark, Bundle toEdit) {
        bundle = toEdit;
    }

    public BundleBuilder(Bundle source) {
        bundle = new Bundle(source);
    }

    @TargetApi(21)
    public BundleBuilder(PersistableBundle source) {
        bundle = new Bundle(source);
    }

    public static BundleBuilder edit(Bundle toEdit) {
        return new BundleBuilder(null, toEdit);
    }

    public Bundle build() {
        return bundle;
    }

    public BundleBuilder setClassLoader(ClassLoader loader) {
        bundle.setClassLoader(loader);
        return this;
    }

    public BundleBuilder clear() {
        bundle.clear();
        return this;
    }

    public BundleBuilder remove(String key) {
        bundle.remove(key);
        return this;
    }

    public BundleBuilder putAll(Bundle bundle) {
        this.bundle.putAll(bundle);
        return this;
    }

    public BundleBuilder putByte(String key, byte value) {
        bundle.putByte(key, value);
        return this;
    }

    public BundleBuilder putChar(String key, char value) {
        bundle.putChar(key, value);
        return this;
    }

    public BundleBuilder putShort(String key, short value) {
        bundle.putShort(key, value);
        return this;
    }

    public BundleBuilder putFloat(String key, float value) {
        bundle.putFloat(key, value);
        return this;
    }

    public BundleBuilder putCharSequence(String key, CharSequence value) {
        bundle.putCharSequence(key, value);
        return this;
    }

    public BundleBuilder putParcelable(String key, Parcelable value) {
        bundle.putParcelable(key, value);
        return this;
    }

    @TargetApi(21)
    public BundleBuilder putSize(String key, Size value) {
        bundle.putSize(key, value);
        return this;
    }

    @TargetApi(21)
    public BundleBuilder putSizeF(String key, SizeF value) {
        bundle.putSizeF(key, value);
        return this;
    }

    public BundleBuilder putParcelableArray(String key, Parcelable[] value) {
        bundle.putParcelableArray(key, value);
        return this;
    }

    public BundleBuilder putParcelableArrayList(String key, ArrayList<? extends Parcelable> value) {
        bundle.putParcelableArrayList(key, value);
        return this;
    }

    public BundleBuilder putSparseParcelableArray(String key, SparseArray<? extends Parcelable> value) {
        bundle.putSparseParcelableArray(key, value);
        return this;
    }

    public BundleBuilder putIntegerArrayList(String key, ArrayList<Integer> value) {
        bundle.putIntegerArrayList(key, value);
        return this;
    }

    public BundleBuilder putStringArrayList(String key, ArrayList<String> value) {
        bundle.putStringArrayList(key, value);
        return this;
    }

    public BundleBuilder putCharSequenceArrayList(String key, ArrayList<CharSequence> value) {
        bundle.putCharSequenceArrayList(key, value);
        return this;
    }

    public BundleBuilder putSerializable(String key, Serializable value) {
        bundle.putSerializable(key, value);
        return this;
    }

    public BundleBuilder putByteArray(String key, byte[] value) {
        bundle.putByteArray(key, value);
        return this;
    }

    public BundleBuilder putShortArray(String key, short[] value) {
        bundle.putShortArray(key, value);
        return this;
    }

    public BundleBuilder putCharArray(String key, char[] value) {
        bundle.putCharArray(key, value);
        return this;
    }

    public BundleBuilder putFloatArray(String key, float[] value) {
        bundle.putFloatArray(key, value);
        return this;
    }

    public BundleBuilder putCharSequenceArray(String key, CharSequence[] value) {
        bundle.putCharSequenceArray(key, value);
        return this;
    }

    public BundleBuilder putBundle(String key, Bundle value) {
        bundle.putBundle(key, value);
        return this;
    }

    @TargetApi(18)
    public BundleBuilder putBinder(String key, IBinder value) {
        bundle.putBinder(key, value);
        return this;
    }

    @Override
    public String toString() {
        return "BundleBuilder{" +
                "bundle=" + bundle +
                '}';
    }

    @TargetApi(21)
    public BundleBuilder putAll(PersistableBundle bundle) {
        this.bundle.putAll(bundle);
        return this;
    }

    public BundleBuilder putBoolean(String key, boolean value) {
        bundle.putBoolean(key, value);
        return this;
    }

    public BundleBuilder putInt(String key, int value) {
        bundle.putInt(key, value);
        return this;
    }

    public BundleBuilder putLong(String key, long value) {
        bundle.putLong(key, value);
        return this;
    }

    public BundleBuilder putDouble(String key, double value) {
        bundle.putDouble(key, value);
        return this;
    }

    public BundleBuilder putString(String key, String value) {
        bundle.putString(key, value);
        return this;
    }

    public BundleBuilder putBooleanArray(String key, boolean[] value) {
        bundle.putBooleanArray(key, value);
        return this;
    }

    public BundleBuilder putIntArray(String key, int[] value) {
        bundle.putIntArray(key, value);
        return this;
    }

    public BundleBuilder putLongArray(String key, long[] value) {
        bundle.putLongArray(key, value);
        return this;
    }

    public BundleBuilder putDoubleArray(String key, double[] value) {
        bundle.putDoubleArray(key, value);
        return this;
    }

    public BundleBuilder putStringArray(String key, String[] value) {
        bundle.putStringArray(key, value);
        return this;
    }
}
