/*
 * Copyright 2012 Mark Slater
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at
 *
 * 	http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.
 */

package argo.staj;

import org.junit.Test;

import java.io.IOException;
import java.io.Reader;

public final class ParserToJsonStreamReaderAdapterTest {

    @Test(expected = JsonStreamException.class)
    public void handlesIoExceptionDuringParsing() throws Exception {
        new StajParser(new Reader() {
            public int read(char[] chars, int offset, int length) throws IOException {
                throw new IOException("An IOException");
            }

            public void close() throws IOException {
            }
        }).next();
    }

    @Test(expected = MyTestRuntimeException.class)
    public void handlesRuntimeExceptionDuringParsing() throws Exception {
        new StajParser(new Reader() {
            public int read(char[] chars, int offset, int length) throws IOException {
                throw new MyTestRuntimeException();
            }

            public void close() throws IOException {
            }
        }).next();
    }

    private static final class MyTestRuntimeException extends RuntimeException {
    }

}
