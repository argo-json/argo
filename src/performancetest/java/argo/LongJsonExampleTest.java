package argo;

import argo.jax.JaxParser;
import argo.jax.JsonListener;
import argo.jdom.JdomParser;
import argo.staj.StajParser;
import net.sf.json.JSONObject;
import org.apache.commons.io.FileUtils;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.Reader;
import java.io.StringReader;

public final class LongJsonExampleTest {

    private static final JsonListener BLACK_HOLE_JSON_LISTENER = new JsonListener() {
        public void startDocument() { }

        public void endDocument() { }

        public void startArray() { }

        public void endArray() { }

        public void startObject() { }

        public void endObject() { }

        public void startField(String name) { }

        public void endField() { }

        public void stringValue(String value) { }

        public void numberValue(String value) { }

        public void trueValue() { }

        public void falseValue() { }

        public void nullValue() { }
    };

    private final Reader[] jsonReaders = new Reader[10000];

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
    public void testArgoJax() throws Exception {
        final JaxParser jaxParser = new JaxParser();
        for (final Reader reader : jsonReaders) {
            jaxParser.parse(reader, BLACK_HOLE_JSON_LISTENER);
        }
    }

    @Test
    public void testArgoStaj() throws Exception {
        for (final Reader reader : jsonReaders) {
            StajParser stajParser = null;
            try {
                stajParser = new StajParser(reader);
                while(stajParser.hasNext()) {
                    stajParser.next();
                }
            } finally {
                if (stajParser != null) {
                    stajParser.close();
                }
            }
        }
    }

    @Test
    public void testArgoJdom() throws Exception {
        final JdomParser jdomParser = new JdomParser();
        for (final Reader reader : jsonReaders) {
            jdomParser.parse(reader);
        }
    }
}
