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

    @Before
    public void getJson() throws Exception {
        final File longJsonExample = new File(this.getClass().getResource("LongJsonExample.json").getFile());
        final String json = FileUtils.readFileToString(longJsonExample);
        for (int i = 0; i < jsonReaders.length; i++) {
            jsonReaders[i] = new StringReader(json);
        }
    }

    @Test
    public void testArgo() throws Exception {
        for (final Reader reader : jsonReaders) {
            Tokenizer.json(reader);
        }
    }

    @Test
    public void testJsonLib() throws Exception {
        for (final Reader reader : jsonReaders) {
            JSONObject.toBean(JSONObject.fromObject(reader));
        }
    }
}
