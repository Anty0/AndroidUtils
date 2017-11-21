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

package eu.codetopic.utils.reflect.field;

import android.support.annotation.Nullable;
import android.support.test.runner.AndroidJUnit4;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import eu.codetopic.java.utils.log.Log;
import eu.codetopic.java.utils.reflect.field.FieldsFilter;
import eu.codetopic.java.utils.reflect.field.FieldsSearch;
import eu.codetopic.java.utils.reflect.field.SimpleFieldsFilter;

@RunWith(AndroidJUnit4.class)
public class FieldsSearchTest {

    private static final String LOG_TAG = "FieldsSearchTest";

    @Before
    public void setUp() throws Exception {

    }

    @Test
    public void testGetAllFields() throws Exception {
        TestClass1 fieldsClass1 = new TestClass1();
        fieldsClass1.somePublicObject2.somePublicStr = "hello";
        fieldsClass1.somePublicObject2.somePublicInt = 0;
        fieldsClass1.somePublicObject2.somePublicChar = 'a';

        testWith(TestClass1.class, fieldsClass1);
        testWith(new SimpleFieldsFilter(TestClass1.class)
                        .addClassesToFind(Object.class)
                        .addClassesToDeepSearch(TestClass1.class, TestClass2.class, TestClass3.class),
                fieldsClass1);

        /*TestClass2 fieldsClass2 = new TestClass2();

        testWith(TestClass1.class, fieldsClass2);
        testWith(new SimpleFieldsFilter(TestClass1.class)
                        .addClassesToFind(Object.class)
                        .addClassesToDeepSearch(TestClass1.class, TestClass2.class, TestClass3.class),
                fieldsClass2);*/

    }

    private void testWith(Class<?> clazz) {
        testWith(clazz, null);
    }

    private void testWith(Class<?> clazz, @Nullable Object fieldsObject) {
        testWith(new SimpleFieldsFilter(clazz).addClassesToFind(Object.class), fieldsObject);
    }

    private void testWith(FieldsFilter filter) {
        testWith(filter, null);
    }

    private void testWith(FieldsFilter filter, @Nullable Object fieldsObject) {
        Log.d(LOG_TAG, "testGetAllFields - fields of " + filter.getStartClass().getName() + ":\n" +
                FieldsSearch.getFields(filter).hierarchyToString(fieldsObject));
    }

    @After
    public void tearDown() throws Exception {

    }

    public static class TestClass1 extends TestClass3<Long> {
        public final TestClass2 somePublicObject2 = new TestClass2();
        private String somePrivateStr = "somePrivateStr";
        private int somePrivateInt = -1;
        private char somePrivateChar = 'r';

        public TestClass1() {
            someElementGenericObject = -150L;
            someTypeGenericObject = "hello";
        }
    }

    public static class TestClass2 {
        public String somePublicStr = "somePublicStr";
        public int somePublicInt = 1;
        public char somePublicChar = 'w';
    }

    public static class TestClass3<E> extends TestClass4<String> {
        public E someElementGenericObject;
    }

    public static class TestClass4<T> extends TestClass5<T> {

    }

    public static class TestClass5<T> {
        public T someTypeGenericObject;
    }
}