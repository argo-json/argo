package argo.jdom;

import static argo.jdom.JsonNodeFactory.*;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;
import org.junit.Test;

import java.util.Arrays;
import java.util.HashMap;

public final class JsonListenerToJdomAdapterTest {

    @Test
    public void simpleStringObjectHappyCase() throws Exception {
        final JsonListenerToJdomAdapter jsonListenerToJdomAdapter = new JsonListenerToJdomAdapter();
        jsonListenerToJdomAdapter.startDocument();
        jsonListenerToJdomAdapter.startObject();
        jsonListenerToJdomAdapter.startField("Hello");
        jsonListenerToJdomAdapter.stringValue("World");
        jsonListenerToJdomAdapter.endField();
        jsonListenerToJdomAdapter.endObject();
        jsonListenerToJdomAdapter.endDocument();
        final JsonNode expected = aJsonObject(new HashMap<String, JsonNode>() {{
            put("Hello", aJsonString("World"));
        }});
        assertThat(jsonListenerToJdomAdapter.getDocument(), equalTo(expected));
    }

    @Test
    public void simpleNumberObjectHappyCase() throws Exception {
        final JsonListenerToJdomAdapter jsonListenerToJdomAdapter = new JsonListenerToJdomAdapter();
        jsonListenerToJdomAdapter.startDocument();
        jsonListenerToJdomAdapter.startObject();
        jsonListenerToJdomAdapter.startField("Gigawatts");
        jsonListenerToJdomAdapter.numberValue("1.21");
        jsonListenerToJdomAdapter.endField();
        jsonListenerToJdomAdapter.endObject();
        jsonListenerToJdomAdapter.endDocument();
        final JsonNode expected = aJsonObject(new HashMap<String, JsonNode>() {{
            put("Gigawatts", JsonNodeFactory.aJsonNumber("1.21"));
        }});
        assertThat(jsonListenerToJdomAdapter.getDocument(), equalTo(expected));
    }

    @Test
    public void simpleStringArrayHappyCase() throws Exception {
        final JsonListenerToJdomAdapter jsonListenerToJdomAdapter = new JsonListenerToJdomAdapter();
        jsonListenerToJdomAdapter.startDocument();
        jsonListenerToJdomAdapter.startArray();
        jsonListenerToJdomAdapter.stringValue("Hello");
        jsonListenerToJdomAdapter.stringValue("World");
        jsonListenerToJdomAdapter.endArray();
        jsonListenerToJdomAdapter.endDocument();
        final JsonNode expected = JsonNodeFactory.aJsonArray(Arrays.asList(aJsonString("Hello"), aJsonString("World")));
        assertThat(jsonListenerToJdomAdapter.getDocument(), equalTo(expected));
    }

    @Test
    public void simpleNumberArrayHappyCase() throws Exception {
        final JsonListenerToJdomAdapter jsonListenerToJdomAdapter = new JsonListenerToJdomAdapter();
        jsonListenerToJdomAdapter.startDocument();
        jsonListenerToJdomAdapter.startArray();
        jsonListenerToJdomAdapter.numberValue("1.21");
        jsonListenerToJdomAdapter.numberValue("42");
        jsonListenerToJdomAdapter.endArray();
        jsonListenerToJdomAdapter.endDocument();
        final JsonNode expected = JsonNodeFactory.aJsonArray(Arrays.asList(JsonNodeFactory.aJsonNumber("1.21"), JsonNodeFactory.aJsonNumber("42")));
        assertThat(jsonListenerToJdomAdapter.getDocument(), equalTo(expected));
    }

    @Test
    public void objectArrayHappyCase() throws Exception {
        final JsonListenerToJdomAdapter jsonListenerToJdomAdapter = new JsonListenerToJdomAdapter();
        jsonListenerToJdomAdapter.startDocument();
        jsonListenerToJdomAdapter.startArray();
        jsonListenerToJdomAdapter.startObject();
        jsonListenerToJdomAdapter.startField("anObject");
        jsonListenerToJdomAdapter.stringValue("objectValue");
        jsonListenerToJdomAdapter.endField();
        jsonListenerToJdomAdapter.endObject();
        jsonListenerToJdomAdapter.nullValue();
        jsonListenerToJdomAdapter.trueValue();
        jsonListenerToJdomAdapter.falseValue();
        jsonListenerToJdomAdapter.endArray();
        jsonListenerToJdomAdapter.endDocument();
        final JsonNode expected = JsonNodeFactory.aJsonArray(Arrays.asList(
                aJsonObject(new HashMap<String, JsonNode>() {{
                    put("anObject", aJsonString("objectValue"));
                }})
                , aNull()
                , aTrue()
                , aFalse()
        ));
        assertThat(jsonListenerToJdomAdapter.getDocument(), equalTo(expected));
    }
}
