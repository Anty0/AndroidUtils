package eu.codetopic.utils;

import java.lang.reflect.Array;

/**
 * Created by anty on 15.6.15.
 *
 * @author anty
 */
public class Arrays {

    public static int[] add(int[] ints, int i) {
        int[] newInts = new int[ints.length + 1];
        System.arraycopy(ints, 0, newInts, 0, ints.length);
        newInts[ints.length] = i;
        return newInts;
    }

    public static long[] add(long[] longs, long l) {
        long[] newInts = new long[longs.length + 1];
        System.arraycopy(longs, 0, newInts, 0, longs.length);
        newInts[longs.length] = l;
        return newInts;
    }

    public static String[] add(String[] strings, String s) {
        String[] newStrings = new String[strings.length + 1];
        System.arraycopy(strings, 0, newStrings, 0, strings.length);
        newStrings[strings.length] = s;
        return newStrings;
    }

    public static int[] remove(int[] ints, int ii) {
        int index = indexOf(ints, ii);
        if (index == -1) return ints;
        int[] newInts = new int[ints.length - 1];
        for (int i = 0; i < ints.length; i++) {
            if (i == index) continue;
            if (i > index) newInts[i - 1] = ints[i];
            else newInts[i] = ints[i];
        }
        return newInts;
    }

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

    public static <T> T[] remove(T[] objects, T o) {
        int index = indexOf(objects, o);
        return index == -1 ? objects : remove(objects, index);
    }

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

    public static boolean contains(int[] ints, int i) {
        for (int ii : ints)
            if (i == ii) return true;
        return false;
    }

    public static boolean contains(long[] longs, long l) {
        for (long ll : longs)
            if (l == ll) return true;
        return false;
    }

    public static boolean contains(Object[] objects, Object o) {
        for (Object obj : objects)
            if (Objects.equals(o, obj)) return true;
        return false;
    }

    public static int indexOf(int[] ints, int ii) {
        for (int i = 0; i < ints.length; i++) {
            if (ii == ints[i]) return i;
        }
        return -1;
    }

    public static int indexOf(long[] longs, long l) {
        for (int i = 0; i < longs.length; i++) {
            if (l == longs[i]) return i;
        }
        return -1;
    }

    public static int indexOf(Object[] objects, Object o) {
        for (int i = 0; i < objects.length; i++) {
            if (Objects.equals(o, objects[i])) return i;
        }
        return -1;
    }

    public static long[] concat(long[] longs1, long[] longs2) {
        long[] longs = java.util.Arrays.copyOf(longs1, longs1.length + longs2.length);
        System.arraycopy(longs2, 0, longs, longs1.length, longs2.length);
        return longs;
    }

    public static <T> T[] concat(T[] objects1, T[] objects2) {
        T[] objects = java.util.Arrays.copyOf(objects1, objects1.length + objects2.length);
        System.arraycopy(objects2, 0, objects, objects1.length, objects2.length);
        return objects;
    }
}
