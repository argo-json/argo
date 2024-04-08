/*
 *  Copyright 2024 Mark Slater
 *
 *  Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at
 *
 *  	http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.
 */

package argo;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.infra.Blackhole;

import java.io.IOException;
import java.io.StringReader;
import java.util.Iterator;

import static argo.JsonParser.NodeInterningStrategy.INTERN_NOTHING;

@State(Scope.Benchmark)
public class ParserBenchmark {

    private static final String JSON_STRING = "{\"web-app\": {\n" +
            "    \"servlet\": [\n" +
            "        {\n" +
            "            \"servlet-name\": \"cofaxCDS\",\n" +
            "            \"servlet-class\": \"org.cofax.cds.CDSServlet\",\n" +
            "            \"init-param\": {\n" +
            "                \"configGlossary:installationAt\": \"Philadelphia, PA\",\n" +
            "                \"configGlossary:adminEmail\": \"ksm@pobox.com\",\n" +
            "                \"configGlossary:poweredBy\": \"Cofax\",\n" +
            "                \"configGlossary:poweredByIcon\": \"/images/cofax.gif\",\n" +
            "                \"configGlossary:staticPath\": \"/content/static\",\n" +
            "                \"templateProcessorClass\": \"org.cofax.WysiwygTemplate\",\n" +
            "                \"templateLoaderClass\": \"org.cofax.FilesTemplateLoader\",\n" +
            "                \"templatePath\": \"templates\",\n" +
            "                \"templateOverridePath\": \"\",\n" +
            "                \"defaultListTemplate\": \"listTemplate.htm\",\n" +
            "                \"defaultFileTemplate\": \"articleTemplate.htm\",\n" +
            "                \"useJSP\": false,\n" +
            "                \"jspListTemplate\": \"listTemplate.jsp\",\n" +
            "                \"jspFileTemplate\": \"articleTemplate.jsp\",\n" +
            "                \"cachePackageTagsTrack\": 200,\n" +
            "                \"cachePackageTagsStore\": 200,\n" +
            "                \"cachePackageTagsRefresh\": 60,\n" +
            "                \"cacheTemplatesTrack\": 100,\n" +
            "                \"cacheTemplatesStore\": 50,\n" +
            "                \"cacheTemplatesRefresh\": 15,\n" +
            "                \"cachePagesTrack\": 200,\n" +
            "                \"cachePagesStore\": 100,\n" +
            "                \"cachePagesRefresh\": 10,\n" +
            "                \"cachePagesDirtyRead\": 10,\n" +
            "                \"searchEngineListTemplate\": \"forSearchEnginesList.htm\",\n" +
            "                \"searchEngineFileTemplate\": \"forSearchEngines.htm\",\n" +
            "                \"searchEngineRobotsDb\": \"WEB-INF/robots.db\",\n" +
            "                \"useDataStore\": true,\n" +
            "                \"dataStoreClass\": \"org.cofax.SqlDataStore\",\n" +
            "                \"redirectionClass\": \"org.cofax.SqlRedirection\",\n" +
            "                \"dataStoreName\": \"cofax\",\n" +
            "                \"dataStoreDriver\": \"com.microsoft.jdbc.sqlserver.SQLServerDriver\",\n" +
            "                \"dataStoreUrl\": \"jdbc:microsoft:sqlserver://LOCALHOST:1433;DatabaseName=goon\",\n" +
            "                \"dataStoreUser\": \"sa\",\n" +
            "                \"dataStorePassword\": \"dataStoreTestQuery\",\n" +
            "                \"dataStoreTestQuery\": \"SET NOCOUNT ON;select test='test';\",\n" +
            "                \"dataStoreLogFile\": \"/usr/local/tomcat/logs/datastore.log\",\n" +
            "                \"dataStoreInitConns\": 10,\n" +
            "                \"dataStoreMaxConns\": 100,\n" +
            "                \"dataStoreConnUsageLimit\": 100,\n" +
            "                \"dataStoreLogLevel\": \"debug\",\n" +
            "                \"maxUrlLength\": 500}},\n" +
            "        {\n" +
            "            \"servlet-name\": \"cofaxEmail\",\n" +
            "            \"servlet-class\": \"org.cofax.cds.EmailServlet\",\n" +
            "            \"init-param\": {\n" +
            "                \"mailHost\": \"mail1\",\n" +
            "                \"mailHostOverride\": \"mail2\"}},\n" +
            "        {\n" +
            "            \"servlet-name\": \"cofaxAdmin\",\n" +
            "            \"servlet-class\": \"org.cofax.cds.AdminServlet\"},\n" +
            "\n" +
            "        {\n" +
            "            \"servlet-name\": \"fileServlet\",\n" +
            "            \"servlet-class\": \"org.cofax.cds.FileServlet\"},\n" +
            "        {\n" +
            "            \"servlet-name\": \"cofaxTools\",\n" +
            "            \"servlet-class\": \"org.cofax.cms.CofaxToolsServlet\",\n" +
            "            \"init-param\": {\n" +
            "                \"templatePath\": \"toolstemplates/\",\n" +
            "                \"log\": 1,\n" +
            "                \"logLocation\": \"/usr/local/tomcat/logs/CofaxTools.log\",\n" +
            "                \"logMaxSize\": \"\",\n" +
            "                \"dataLog\": 1,\n" +
            "                \"dataLogLocation\": \"/usr/local/tomcat/logs/dataLog.log\",\n" +
            "                \"dataLogMaxSize\": \"\",\n" +
            "                \"removePageCache\": \"/content/admin/remove?cache=pages&id=\",\n" +
            "                \"removeTemplateCache\": \"/content/admin/remove?cache=templates&id=\",\n" +
            "                \"fileTransferFolder\": \"/usr/local/tomcat/webapps/content/fileTransferFolder\",\n" +
            "                \"lookInContext\": 1,\n" +
            "                \"adminGroupID\": 4,\n" +
            "                \"betaServer\": true}}],\n" +
            "    \"servlet-mapping\": {\n" +
            "        \"cofaxCDS\": \"/\",\n" +
            "        \"cofaxEmail\": \"/cofaxutil/aemail/*\",\n" +
            "        \"cofaxAdmin\": \"/admin/*\",\n" +
            "        \"fileServlet\": \"/static/*\",\n" +
            "        \"cofaxTools\": \"/tools/*\"},\n" +
            "\n" +
            "    \"taglib\": {\n" +
            "        \"taglib-uri\": \"cofax.tld\",\n" +
            "        \"taglib-location\": \"/WEB-INF/tlds/cofax.tld\"}}}";

    private final JsonParser jsonParser = new JsonParser();
    private final JsonParser jsonParserNonInterning = new JsonParser().nodeInterning(INTERN_NOTHING);

    @Benchmark
    public void jdomParse(final Blackhole blackhole) throws InvalidSyntaxException {
        blackhole.consume(jsonParser.parse(JSON_STRING));
    }

    @Benchmark
    public void jdomParseNonInterning(final Blackhole blackhole) throws InvalidSyntaxException {
        blackhole.consume(jsonParserNonInterning.parse(JSON_STRING));
    }

    @Benchmark
    public void streamingIteratorParse(final Blackhole blackhole) {
        final Iterator<JsonStreamElement> jsonStreamElementIterator = jsonParser.parseStreaming(new StringReader(JSON_STRING));
        while (jsonStreamElementIterator.hasNext()) {
            blackhole.consume(jsonStreamElementIterator.next());
        }
    }

    @Benchmark
    public void streamingEventParse(final Blackhole blackhole) throws InvalidSyntaxException, IOException {
        jsonParser.parseStreaming(new StringReader(JSON_STRING), new BlackHoleJsonListener(blackhole::consume));
    }

}
