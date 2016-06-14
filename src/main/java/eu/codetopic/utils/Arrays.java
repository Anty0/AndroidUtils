package eu.codetopic.utils;

import android.support.annotation.CheckResult;

import java.lang.reflect.Array;

public class Arrays {

    /**
     * Add int to array of ints.
     *
     * @param ints array to modify
     * @param i    int to add
     * @return new array with specified int
     */
    @CheckResult
    public static int[] add(int[] ints, int i) {
        int[] newInts = new int[ints.length + 1];
        System.arraycopy(ints, 0, newInts, 0, ints.length);
        newInts[ints.length] = i;
        return newInts;
    }

    /**
     * Add long to array of longs.
     *
     * @param longs array to modify
     * @param l     long to add
     * @return new array with specified long
     */
    @CheckResult
    public static long[] add(long[] longs, long l) {
        long[] newInts = new long[longs.length + 1];
        System.arraycopy(longs, 0, newInts, 0, longs.length);
        newInts[longs.length] = l;
        return newInts;
    }

    /**
     * Add String to array of Strings.
     *
     * @param strings array to modify
     * @param s       String to add
     * @return new array with specified String
     */
    @CheckResult
    public static String[] add(String[] strings, String s) {
        String[] newStrings = new String[strings.length + 1];
        System.arraycopy(strings, 0, newStrings, 0, strings.length);
        newStrings[strings.length] = s;
        return newStrings;
    }

    /**
     * Add object to specified array.
     *
     * @param objects array to modify
     * @param object  object to add
     * @return new array with specified object
     */
    @CheckResult
    public static <T> T[] add(T[] objects, T object) {
        T[] newObjects = java.util.Arrays.copyOf(objects, objects.length + 1);
        newObjects[objects.length] = object;
        return newObjects;
    }

    /**
     * Add object to start (indexOfStart == 0) of specified array.
     *
     * @param objects array to modify
     * @param object  object to add
     * @return new array with specified object
     */
    @CheckResult
    public static <T> T[] addToStart(T[] objects, T object) {
        //noinspection unchecked
        T[] newObjects = (T[]) Array.newInstance(objects.getClass()
                .getComponentType(), objects.length + 1);
        System.arraycopy(objects, 0, newObjects, 1, objects.length);
        newObjects[0] = object;
        return newObjects;
    }

    /**
     * Remove int from array of ints.
     *
     * @param ints array to modify
     * @param i    int to remove
     * @return new array without specified int (if found)
     */
    @CheckResult
    public static int[] remove(int[] ints, int i) {
        int index = indexOf(ints, i);
        if (index == -1) return ints;
        int[] newInts = new int[ints.length - 1];
        for (int j = 0; j < ints.length; j++) {
            if (j == index) continue;
            if (j > index) newInts[j - 1] = ints[j];
            else newInts[j] = ints[j];
        }
        return newInts;
    }

    /**
     * Remove long from array of longs.
     *
     * @param longs array to modify
     * @param l     long to remove
     * @return new array without specified long (if found)
     */
    @CheckResult
    public static long[] remove(long[] longs, long l) {
        int index = indexOf(longs, l);
        if (index == -1) return longs;
        long[] newInts = new long[longs.length - 1];
        for (int i = 0; i < longs.length; i++) {
            if (i == index) continue;
            if (i > index) newInts[i - 1] = longs[i];
            else newInts[i] = longs[i];
        }
        return newInts;
    }

    /**
     * Remove String from array of Strings.
     *
     * @param strings array to modify
     * @param s       String to remove
     * @return new array without specified String (if found)
     */
    @CheckResult
    public static String[] remove(String[] strings, String s) {
        int index = indexOf(strings, s);
        if (index == -1) return strings;
        String[] newStrings = new String[strings.length - 1];
        for (int i = 0; i < strings.length; i++) {
            if (i == index) continue;
            if (i > index) newStrings[i - 1] = strings[i];
            else newStrings[i] = strings[i];
        }
        return newStrings;
    }

    /**
     * Remove all null values from T[].
     *
     * @param objects array to modify
     * @param <T>     type of array to modify
     * @return new array without null values
     */
    @CheckResult
    public static <T> T[] removeNulls(T[] objects) {
        int index = indexOf(objects, null);
        while (index != -1)
            objects = remove(objects, index);
        return objects;
    }

    /**
     * Remove object T from T[].
     *
     * @param objects array to modify
     * @param o       T to remove
     * @param <T>     type of array to modify
     * @return new array without specified T (if found)
     */
    @CheckResult
    public static <T> T[] remove(T[] objects, T o) {
        int index = indexOf(objects, o);
        return index == -1 ? objects : remove(objects, index);
    }

    /**
     * Remove object on index from T[].
     *
     * @param objects array to modify
     * @param index   index of object to remove
     * @param <T>     type of array to modify
     * @return new array without object on specified index
     */
    @CheckResult
    public static <T> T[] remove(T[] objects, int index) {
        //noinspection unchecked
        T[] newObjects = (T[]) Array.newInstance(objects.getClass()
                .getComponentType(), objects.length - 1);
        for (int i = 0; i < objects.length; i++) {
            if (i == index) continue;
            newObjects[i > index ? i - 1 : i] = objects[i];
        }
        return newObjects;
    }

    /**
     * Returns true if specified array contains specified int.
     *
     * @param ints array to search
     * @param i    to find
     * @return true if specified array contains specified int
     */
    @CheckResult
    public static boolean contains(int[] ints, int i) {
        for (int j : ints)
            if (i == j) return true;
        return false;
    }

    /**
     * Returns true if specified array contains specified long.
     *
     * @param longs array to search
     * @param l     to find
     * @return true if specified array contains specified long
     */
    @CheckResult
    public static boolean contains(long[] longs, long l) {
        for (long j : longs)
            if (l == j) return true;
        return false;
    }

    /**
     * Returns true if specified array contains specified object.
     *
     * @param objects array to search
     * @param o       to find
     * @return true if specified array contains specified object
     */
    @CheckResult
    public static boolean contains(Object[] objects, Object o) {
        for (Object obj : objects)
            if (Objects.equals(o, obj)) return true;
        return false;
    }

    /**
     * Returns index of specified int in specified array.
     * Returns -1 if specified int is not in specified array.
     *
     * @param ints array to search
     * @param i    to find
     * @return index of specified int in specified array or -1
     */
    @CheckResult
    public static int indexOf(int[] ints, int i) {
        for (int j = 0; j < ints.length; j++) {
            if (i == ints[j]) return j;
        }
        return -1;
    }

    /**
     * Returns index of specified long in specified array.
     * Returns -1 if specified long is not in specified array.
     *
     * @param longs array to search
     * @param l     to find
     * @return index of specified long in specified array or -1
     */
    @CheckResult
    public static int indexOf(long[] longs, long l) {
        for (int i = 0; i < longs.length; i++) {
            if (l == longs[i]) return i;
        }
        return -1;
    }

    /**
     * Returns index of specified object in specified array.
     * Returns -1 if specified object is not in specified array.
     *
     * @param objects array to search
     * @param o       to find
     * @return index of specified object in specified array or -1
     */
    @CheckResult
    public static int indexOf(Object[] objects, Object o) {
        for (int i = 0; i < objects.length; i++) {
            if (Objects.equals(o, objects[i])) return i;
        }
        return -1;
    }

    /**
     * Concatenates first specified array and second specified array.
     *
     * @param longs1 first array to concat
     * @param longs2 second array to concat
     * @return concatenated first specified array and second specified array
     */
    @CheckResult
    public static long[] concat(long[] longs1, long[] longs2) {
        long[] longs = java.util.Arrays.copyOf(longs1, longs1.length + longs2.length);
        System.arraycopy(longs2, 0, longs, longs1.length, longs2.length);
        return longs;
    }

    /**
     * Concatenates first specified array and second specified array.
     *
     * @param objects1 first array to concat
     * @param objects2 second array to concat
     * @param <T>      type of arrays to concatenate
     * @return concatenated first specified array and second specified array
     */
    @CheckResult
    public static <T> T[] concat(T[] objects1, T[] objects2) {
        T[] objects = java.util.Arrays.copyOf(objects1, objects1.length + objects2.length);
        System.arraycopy(objects2, 0, objects, objects1.length, objects2.length);
        return objects;
    }
}
