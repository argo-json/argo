/*
 *  Copyright  2019 Mark Slater
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
import java.io.Reader;
import java.io.StringReader;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.apache.commons.io.FileUtils.readFileToString;


public final class LongJsonExampleTest {

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

    private final Reader[] jsonReaders = new Reader[10000];

    @BeforeEach
    public void getJson() throws Exception {
        final File longJsonExample = new File(this.getClass().getResource("LongJsonExample.json").getFile());
        final String json = readFileToString(longJsonExample, UTF_8);
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
    public void testArgoSaj() throws Exception {
        final SajParser sajParser = new SajParser();
        for (final Reader reader : jsonReaders) {
            sajParser.parse(reader, BLACK_HOLE_JSON_LISTENER);
        }
    }

    @Test
    public void testArgoStaj() throws Exception {
        for (final Reader reader : jsonReaders) {
            final StajParser stajParser = new StajParser(reader);
            while (stajParser.hasNext()) {
                stajParser.next();
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
