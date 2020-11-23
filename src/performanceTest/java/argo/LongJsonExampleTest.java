/*
 *  Copyright  2020 Mark Slater
 *
 *  Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at
 *
 *  	http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.
 */

package argo;

import argo.jdom.JdomParser;
import argo.saj.JsonListener;
import argo.saj.SajParser;
import argo.staj.StajParser;
import net.sf.json.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.StringReader;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.apache.commons.io.FileUtils.readFileToString;


final class LongJsonExampleTest {

    private static final JsonListener BLACK_HOLE_JSON_LISTENER = new JsonListener() {
        public void startDocument() {
        }

        public void endDocument() {
        }

        public void startArray() {
        }

        public void endArray() {
        }

        public void startObject() {
        }

        public void endObject() {
        }

        public void startField(String name) {
        }

        public void endField() {
        }

        public void stringValue(String value) {
        }

        public void numberValue(String value) {
        }

        public void trueValue() {
        }

        public void falseValue() {
        }

        public void nullValue() {
        }
    };

    private final StringReader[] jsonReaders = new StringReader[10_000];

    @BeforeEach
    void readJson() throws Exception {
        final File longJsonExample = new File(this.getClass().getResource("LongJsonExample.json").getFile());
        final String json = readFileToString(longJsonExample, UTF_8);
        for (int i = 0; i < jsonReaders.length; i++) {
            jsonReaders[i] = new StringReader(json);
        }
    }

    @Test
    void testJsonLib() {
        for (final StringReader reader : jsonReaders) {
            JSONObject.toBean(JSONObject.fromObject(reader));
        }
    }

    @Test
    void testArgoSaj() throws Exception {
        final SajParser sajParser = new SajParser();
        for (final StringReader reader : jsonReaders) {
            sajParser.parse(reader, BLACK_HOLE_JSON_LISTENER);
        }
    }

    @Test
    void testArgoStaj() {
        for (final StringReader reader : jsonReaders) {
            final StajParser stajParser = new StajParser(reader);
            while (stajParser.hasNext()) {
                stajParser.next();
            }
        }
    }

    @Test
    void testArgoJdom() throws Exception {
        final JdomParser jdomParser = new JdomParser();
        for (final StringReader reader : jsonReaders) {
            jdomParser.parse(reader);
        }
    }

}
