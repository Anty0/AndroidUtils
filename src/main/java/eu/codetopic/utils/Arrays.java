package eu.codetopic.utils;

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

    public static boolean contains(int[] objects, int o) {
        for (int obj : objects) {
            if (o == obj) return true;
        }
        return false;
    }

    public static boolean contains(Object[] objects, Object o) {
        for (Object obj : objects) {
            if (Objects.equals(o, obj)) return true;
        }
        return false;
    }

    public static int indexOf(int[] objects, int o) {
        for (int i = 0; i < objects.length; i++) {
            if (o == objects[i]) return i;
        }
        return -1;
    }

    public static int indexOf(Object[] objects, Object o) {
        for (int i = 0; i < objects.length; i++) {
            if (Objects.equals(o, objects[i])) return i;
        }
        return -1;
    }
}
