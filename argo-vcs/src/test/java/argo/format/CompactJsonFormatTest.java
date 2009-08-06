package argo.format;

import argo.jdom.JsonNode;
import static argo.jdom.JsonNodeFactory.*;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;
import org.junit.Test;

import java.util.Arrays;
import java.util.HashMap;

public final class CompactJsonFormatTest {
    @Test
    public void formatsAJsonObject() throws Exception {
        assertThat(new CompactJsonFormat().format(aJsonObject(new HashMap<String, JsonNode>(){{
            put("Hello", aJsonString("World"));
            put("Foo", aJsonString("Bar"));
        }})), equalTo("{\"Hello\":\"World\",\"Foo\":\"Bar\"}"));
    }

    @Test
    public void formatsAJsonNumber() throws Exception {
        assertThat(new CompactJsonFormat().format(aJsonObject(new HashMap<String, JsonNode>(){{
            put("S", aJsonNumber("7"));
        }})), equalTo("{\"S\":7}"));
    }

    @Test
    public void formatsAJsonArray() throws Exception {
        assertThat(new CompactJsonFormat().format(aJsonArray(Arrays.asList(
                aJsonNumber("12")
                , aJsonString("tie")
        ))), equalTo("[12,\"tie\"]"));
    }

    @Test
    public void formatsTheJsonConstants() throws Exception {
        assertThat(new CompactJsonFormat().format(aJsonArray(Arrays.asList(
                aNull()
                , aTrue()
                , aFalse()
        ))), equalTo("[null,true,false]"));
    }
}
