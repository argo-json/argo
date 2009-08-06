package argo.format;

import argo.jdom.JsonNode;
import argo.jdom.JsonNodeFactory;
import static argo.jdom.JsonNodeFactory.aJsonStringNode;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;
import org.junit.Test;

import java.util.HashMap;

public final class CompactJsonFormatTest {
    @Test
    public void formatsAJsonValue() throws Exception {
        assertThat(new CompactJsonFormat().format(JsonNodeFactory.aJsonObject(new HashMap<String, JsonNode>(){{
            put("Hello", aJsonStringNode("World"));
        }})), equalTo("{\"Hello\":\"World\"}"));
    }
}
