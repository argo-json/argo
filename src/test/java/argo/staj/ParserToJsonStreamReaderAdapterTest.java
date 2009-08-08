package argo.staj;

import org.junit.Test;

import java.io.IOException;
import java.io.Reader;

public final class ParserToJsonStreamReaderAdapterTest {

    @Test( expected = JsonStreamException.class )
    public void handlesIoExceptionDuringParsing() throws Exception {
        new StajParser(new Reader() {
            public int read(char[] cbuf, int off, int len) throws IOException {
                throw new IOException("An IOException");
            }
            public void close() throws IOException { }
        }).next();
    }

    @Test( expected = MyTestRuntimeException.class )
    public void handlesRuntimeExceptionDuringParsing() throws Exception {
        new StajParser(new Reader() {
            public int read(char[] cbuf, int off, int len) throws IOException {
                throw new MyTestRuntimeException();
            }
            public void close() throws IOException { }
        }).next();
    }

    private static final class MyTestRuntimeException extends RuntimeException { }

}
