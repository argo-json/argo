package argo.format;

import argo.jdom.JsonNode;
import argo.jdom.JsonObject;
import argo.jdom.JsonString;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;
import org.junit.Test;

import java.util.HashMap;

public final class CompactJsonFormatTest {
    @Test
    public void formatsAJsonValue() throws Exception {
        assertThat(new CompactJsonFormat().format(new JsonObject(new HashMap<JsonString, JsonNode>(){{
            put(new JsonString("Hello"), new JsonString("World"));
        }})), equalTo("{\"Hello\":\"World\"}"));
    }
}
