package argo.token;

import net.sf.json.JSONObject;
import org.apache.commons.io.FileUtils;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.Reader;
import java.io.StringReader;

public final class LongJsonExampleTest {

    private Reader[] jsonReaders = new Reader[10000];
    private static final JsonListener BLACK_HOLE_JSON_LISTENER = new JsonListener() {
        public void startDocument() throws JsonListenerException { }

        public void endDocument() throws JsonListenerException { }

        public void startArray() throws JsonListenerException { }

        public void endArray() throws JsonListenerException { }

        public void startObject() throws JsonListenerException { }

        public void endObject() throws JsonListenerException { }

        public void startField(String name) throws JsonListenerException { }

        public void endField() throws JsonListenerException { }

        public void stringValue(String value) throws JsonListenerException { }

        public void numberValue(String value) throws JsonListenerException { }

        public void trueValue() throws JsonListenerException { }

        public void falseValue() throws JsonListenerException { }

        public void nullValue() throws JsonListenerException { }
    };

    @Before
    public void getJson() throws Exception {
        final File longJsonExample = new File(this.getClass().getResource("LongJsonExample.json").getFile());
        final String json = FileUtils.readFileToString(longJsonExample);
        for (int i = 0; i < jsonReaders.length; i++) {
            jsonReaders[i] = new StringReader(json);
        }
    }

    @Test
    public void testJsonLib() throws Exception {
        for (final Reader reader : jsonReaders) {
            JSONObject.toBean(JSONObject.fromObject(reader));
        }
    }

    @Test
    public void testArgo() throws Exception {
        final JsonParser jsonParser = new JsonParser();
        for (final Reader reader : jsonReaders) {
            jsonParser.parse(reader, BLACK_HOLE_JSON_LISTENER);
        }
    }
}
