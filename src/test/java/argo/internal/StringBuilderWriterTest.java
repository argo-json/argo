/*
 *  Copyright 2024 Mark Slater
 *
 *  Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at
 *
 *  	http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.
 */

package argo.internal;

import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertThrows;

class StringBuilderWriterTest {

    @Test
    void writesStringToBuilder() {
        final StringBuilderWriter stringBuilderWriter = new StringBuilderWriter();
        try {
            stringBuilderWriter.write("a");
        } finally {
            stringBuilderWriter.close();
        }
        assertThat(stringBuilderWriter.toString(), equalTo("a"));
    }

    @Test
    void writesCharacterToBuilder() {
        final StringBuilderWriter stringBuilderWriter = new StringBuilderWriter();
        try {
            stringBuilderWriter.write('a');
        } finally {
            stringBuilderWriter.close();
        }
        assertThat(stringBuilderWriter.toString(), equalTo("a"));
    }

    @Test
    void writesSubstringsImmediately() {
        try (StringBuilderWriter stringBuilderWriter = new StringBuilderWriter()) {
            stringBuilderWriter.write("a");
            assertThat(stringBuilderWriter.toString(), equalTo("a"));
            stringBuilderWriter.write("b");
            assertThat(stringBuilderWriter.toString(), equalTo("ab"));
        }
    }

    @Test
    void writesCharactersImmediately() {
        try (StringBuilderWriter stringBuilderWriter = new StringBuilderWriter()) {
            stringBuilderWriter.write('a');
            assertThat(stringBuilderWriter.toString(), equalTo("a"));
            stringBuilderWriter.write('b');
            assertThat(stringBuilderWriter.toString(), equalTo("ab"));
        }
    }

    @Test
    void rejectsNullString() {
        try (StringBuilderWriter stringBuilderWriter = new StringBuilderWriter()) {
            assertThrows(NullPointerException.class, () -> stringBuilderWriter.write((String) null));
        }
    }

    @Test
    void rejectsNullCharArray() {
        try (StringBuilderWriter stringBuilderWriter = new StringBuilderWriter()) {
            assertThrows(NullPointerException.class, () -> stringBuilderWriter.write((char[]) null));
        }
    }

    @Test
    void rejectsNullCharArrayWithOffsetAndLength() {
        try (StringBuilderWriter stringBuilderWriter = new StringBuilderWriter()) {
            assertThrows(NullPointerException.class, () -> stringBuilderWriter.write((char[]) null, 0, 1));
        }
    }

    @Test
    void rejectsNullStringWithOffsetAndLength() {
        try (StringBuilderWriter stringBuilderWriter = new StringBuilderWriter()) {
            assertThrows(NullPointerException.class, () -> stringBuilderWriter.write((String) null, 0, 1));
        }
    }

    @Test
    void writesACharArray() {
        final StringBuilderWriter stringBuilderWriter = new StringBuilderWriter();
        try {
            stringBuilderWriter.write(new char[] {'a', 'b', 'c'});
        } finally {
            stringBuilderWriter.close();
        }
        assertThat(stringBuilderWriter.toString(), equalTo("abc"));
    }

    @Test
    void writesACharArrayWithDefaultOffsetAndLength() {
        final StringBuilderWriter stringBuilderWriter = new StringBuilderWriter();
        try {
            stringBuilderWriter.write(new char[] {'a', 'b', 'c'}, 0, 3);
        } finally {
            stringBuilderWriter.close();
        }
        assertThat(stringBuilderWriter.toString(), equalTo("abc"));
    }

    @Test
    void writesACharArrayWithOffset() {
        final StringBuilderWriter stringBuilderWriter = new StringBuilderWriter();
        try {
            stringBuilderWriter.write(new char[] {'a', 'b', 'c'}, 1, 2);
        } finally {
            stringBuilderWriter.close();
        }
        assertThat(stringBuilderWriter.toString(), equalTo("bc"));
    }

    @Test
    void writesACharArrayWithLength() {
        final StringBuilderWriter stringBuilderWriter = new StringBuilderWriter();
        try {
            stringBuilderWriter.write(new char[] {'a', 'b', 'c'}, 0, 2);
        } finally {
            stringBuilderWriter.close();
        }
        assertThat(stringBuilderWriter.toString(), equalTo("ab"));
    }

    @Test
    void writesACharArrayWithOffsetAndLength() {
        final StringBuilderWriter stringBuilderWriter = new StringBuilderWriter();
        try {
            stringBuilderWriter.write(new char[] {'a', 'b', 'c'}, 1, 1);
        } finally {
            stringBuilderWriter.close();
        }
        assertThat(stringBuilderWriter.toString(), equalTo("b"));
    }

    @Test
    void attemptingToWriteACharArrayWithOffsetAndLengthWhereOffsetIsNegativeThrowsIndexOutOfBoundsException() {
        try (StringBuilderWriter stringBuilderWriter = new StringBuilderWriter()) {
            assertThrows(IndexOutOfBoundsException.class, () -> stringBuilderWriter.write(new char[3], -1, 0));
        }
    }

    @Test
    void attemptingToWriteACharArrayWithOffsetAndLengthWhereOffsetIsGreaterThanArrayLengthThrowsIndexOutOfBoundsException() {
        try (StringBuilderWriter stringBuilderWriter = new StringBuilderWriter()) {
            assertThrows(IndexOutOfBoundsException.class, () -> stringBuilderWriter.write(new char[3], 4, 0));
        }
    }

    @Test
    void attemptingToWriteACharArrayWithOffsetAndLengthWhereLengthIsLessThanZeroThrowsIndexOutOfBoundsException() {
        try (StringBuilderWriter stringBuilderWriter = new StringBuilderWriter()) {
            assertThrows(IndexOutOfBoundsException.class, () -> stringBuilderWriter.write(new char[3], 0, -1));
        }
    }

    @Test
    void attemptingToWriteACharArrayWithOffsetAndLengthWhereOffsetPlusLengthIsGreaterThanArrayLengthThrowsIndexOutOfBoundsException() {
        try (StringBuilderWriter stringBuilderWriter = new StringBuilderWriter()) {
            assertThrows(IndexOutOfBoundsException.class, () -> stringBuilderWriter.write(new char[3], 1, 3));
        }
    }

    @Test
    void writesAStringWithDefaultOffsetAndLength() {
        final StringBuilderWriter stringBuilderWriter = new StringBuilderWriter();
        try {
            stringBuilderWriter.write("abc", 0, 3);
        } finally {
            stringBuilderWriter.close();
        }
        assertThat(stringBuilderWriter.toString(), equalTo("abc"));
    }

    @Test
    void writesAStringWithOffset() {
        final StringBuilderWriter stringBuilderWriter = new StringBuilderWriter();
        try {
            stringBuilderWriter.write("abc", 1, 2);
        } finally {
            stringBuilderWriter.close();
        }
        assertThat(stringBuilderWriter.toString(), equalTo("bc"));
    }

    @Test
    void writesAStringWithLength() {
        final StringBuilderWriter stringBuilderWriter = new StringBuilderWriter();
        try {
            stringBuilderWriter.write("abc", 0, 2);
        } finally {
            stringBuilderWriter.close();
        }
        assertThat(stringBuilderWriter.toString(), equalTo("ab"));
    }

    @Test
    void writesAStringWithOffsetAndLength() {
        final StringBuilderWriter stringBuilderWriter = new StringBuilderWriter();
        try {
            stringBuilderWriter.write("abc", 1, 1);
        } finally {
            stringBuilderWriter.close();
        }
        assertThat(stringBuilderWriter.toString(), equalTo("b"));
    }

    @Test
    void attemptingToWriteAStringWithOffsetAndLengthWhereOffsetIsNegativeThrowsIndexOutOfBoundsException() {
        try (StringBuilderWriter stringBuilderWriter = new StringBuilderWriter()) {
            assertThrows(IndexOutOfBoundsException.class, () -> stringBuilderWriter.write("abc", -1, 0));
        }
    }

    @Test
    void attemptingToWriteAStringWithOffsetAndLengthWhereOffsetIsGreaterThanArrayLengthThrowsIndexOutOfBoundsException() {
        try (StringBuilderWriter stringBuilderWriter = new StringBuilderWriter()) {
            assertThrows(IndexOutOfBoundsException.class, () -> stringBuilderWriter.write("abc", 4, 0));
        }
    }

    @Test
    void attemptingToWriteAStringWithOffsetAndLengthWhereLengthIsLessThanZeroThrowsIndexOutOfBoundsException() {
        try (StringBuilderWriter stringBuilderWriter = new StringBuilderWriter()) {
            assertThrows(IndexOutOfBoundsException.class, () -> stringBuilderWriter.write("abc", 0, -1));
        }
    }

    @Test
    void attemptingToWriteAStringWithOffsetAndLengthWhereOffsetPlusLengthIsGreaterThanArrayLengthThrowsIndexOutOfBoundsException() {
        try (StringBuilderWriter stringBuilderWriter = new StringBuilderWriter()) {
            assertThrows(IndexOutOfBoundsException.class, () -> stringBuilderWriter.write("abc", 1, 3));
        }
    }

    @Test
    @SuppressWarnings("PMD.CloseResource")
    void afterClosingCanCallCloseAgain() {
        StringBuilderWriter stringBuilderWriter = new StringBuilderWriter();
        stringBuilderWriter.write("a");
        stringBuilderWriter.close();
        stringBuilderWriter.close();
    }

    @Test
    @SuppressWarnings("PMD.CloseResource")
    void afterClosingWritingASubstringDoesNotThrowException() {
        StringBuilderWriter stringBuilderWriter = new StringBuilderWriter();
        stringBuilderWriter.write("a");
        stringBuilderWriter.close();
        stringBuilderWriter.write("b");
        assertThat(stringBuilderWriter.toString(), equalTo("ab"));
    }

    @Test
    @SuppressWarnings("PMD.CloseResource")
    void afterClosingWritingACharacterDoesNotThrowException() {
        StringBuilderWriter stringBuilderWriter = new StringBuilderWriter();
        stringBuilderWriter.write('a');
        stringBuilderWriter.close();
        stringBuilderWriter.write('b');
        assertThat(stringBuilderWriter.toString(), equalTo("ab"));
    }

    @Test
    @SuppressWarnings("PMD.CloseResource")
    void afterClosingWritingACharArrayDoesNotThrowException() {
        StringBuilderWriter stringBuilderWriter = new StringBuilderWriter();
        stringBuilderWriter.write(new char[] {'a', 'b', 'c'});
        stringBuilderWriter.close();
        stringBuilderWriter.write(new char[] {'d', 'e', 'f'});
        assertThat(stringBuilderWriter.toString(), equalTo("abcdef"));
    }

    @Test
    @SuppressWarnings("PMD.CloseResource")
    void afterClosingWritingACharArrayWithOffsetAndLengthDoesNotThrowException() {
        StringBuilderWriter stringBuilderWriter = new StringBuilderWriter();
        stringBuilderWriter.write(new char[] {'a', 'b', 'c'}, 0, 3);
        stringBuilderWriter.close();
        stringBuilderWriter.write(new char[] {'d', 'e', 'f'}, 0, 3);
        assertThat(stringBuilderWriter.toString(), equalTo("abcdef"));
    }

    @Test
    @SuppressWarnings("PMD.CloseResource")
    void afterClosingWritingAStringWithOffsetAndLengthDoesNotThrowException() {
        StringBuilderWriter stringBuilderWriter = new StringBuilderWriter();
        stringBuilderWriter.write("abc", 0, 3);
        stringBuilderWriter.close();
        stringBuilderWriter.write("def", 0, 3);
        assertThat(stringBuilderWriter.toString(), equalTo("abcdef"));
    }

    @Test
    @SuppressWarnings("PMD.CloseResource")
    void afterClosingFlushDoesNotThrowException() {
        StringBuilderWriter stringBuilderWriter = new StringBuilderWriter();
        stringBuilderWriter.write('a');
        stringBuilderWriter.close();
        stringBuilderWriter.flush();
        assertThat(stringBuilderWriter.toString(), equalTo("a"));
    }

}