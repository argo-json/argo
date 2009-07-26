package argo.dom;

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
        final JsonValue expected = new JsonObject(new HashMap<JsonString, JsonValue>() {{
            put(new JsonString("Hello"), new JsonString("World"));
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
        final JsonValue expected = new JsonObject(new HashMap<JsonString, JsonValue>() {{
            put(new JsonString("Gigawatts"), new JsonNumber("1.21"));
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
        final JsonValue expected = new JsonArray(Arrays.asList((JsonValue) new JsonString("Hello"), new JsonString("World")));
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
        final JsonValue expected = new JsonArray(Arrays.asList((JsonValue) new JsonNumber("1.21"), new JsonNumber("42")));
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
        final JsonValue expected = new JsonArray(Arrays.asList(
                new JsonObject(new HashMap<JsonString, JsonValue>() {{
                    put(new JsonString("anObject"), new JsonString("objectValue"));
                }})
                , JsonConstants.NULL
                , JsonConstants.TRUE
                , JsonConstants.FALSE
        ));
        assertThat(jsonListenerToJdomAdapter.getDocument(), equalTo(expected));
    }
}
