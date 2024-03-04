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

import argo.format.*;
import argo.jdom.JsonNode;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.infra.Blackhole;

import java.io.IOException;
import java.io.Writer;

import static argo.JsonGenerator.JsonGeneratorStyle.COMPACT;
import static argo.JsonGenerator.JsonGeneratorStyle.PRETTY;
import static argo.jdom.JsonNodeFactories.*;

@State(Scope.Benchmark)
public class GeneratorBenchmark {

    private static final JsonNode JSON_NODE = object(
            field("web-app", object(
                            field("servlet", array(
                                            object(
                                                    field("servlet-name", string("cofaxCDS")),
                                                    field("servlet-class", string("org.cofax.cds.CDSServlet")),
                                                    field("init-param", object(
                                                                    field("configGlossary:installationAt", string("Philadelphia, PA")),
                                                                    field("configGlossary:adminEmail", string("ksm@pobox.com")),
                                                                    field("configGlossary:poweredBy", string("Cofax")),
                                                                    field("configGlossary:poweredByIcon", string("/images/cofax.gif")),
                                                                    field("configGlossary:staticPath", string("/content/static")),
                                                                    field("templateProcessorClass", string("org.cofax.WysiwygTemplate")),
                                                                    field("templateLoaderClass", string("org.cofax.FilesTemplateLoader")),
                                                                    field("templatePath", string("templates")),
                                                                    field("templateOverridePath", string("")),
                                                                    field("defaultListTemplate", string("listTemplate.htm")),
                                                                    field("defaultFileTemplate", string("articleTemplate.htm")),
                                                                    field("useJSP", falseNode()),
                                                                    field("jspListTemplate", string("listTemplate.jsp")),
                                                                    field("jspFileTemplate", string("articleTemplate.jsp")),
                                                                    field("cachePackageTagsTrack", number("200")),
                                                                    field("cachePackageTagsStore", number("200")),
                                                                    field("cachePackageTagsRefresh", number("60")),
                                                                    field("cacheTemplatesTrack", number("100")),
                                                                    field("cacheTemplatesStore", number("50")),
                                                                    field("cacheTemplatesRefresh", number("15")),
                                                                    field("cachePagesTrack", number("200")),
                                                                    field("cachePagesStore", number("100")),
                                                                    field("cachePagesRefresh", number("10")),
                                                                    field("cachePagesDirtyRead", number("10")),
                                                                    field("searchEngineListTemplate", string("forSearchEnginesList.htm")),
                                                                    field("searchEngineFileTemplate", string("forSearchEngines.htm")),
                                                                    field("searchEngineRobotsDb", string("WEB-INF/robots.db")),
                                                                    field("useDataStore", trueNode()),
                                                                    field("dataStoreClass", string("org.cofax.SqlDataStore")),
                                                                    field("redirectionClass", string("org.cofax.SqlRedirection")),
                                                                    field("dataStoreName", string("cofax")),
                                                                    field("dataStoreDriver", string("com.microsoft.jdbc.sqlserver.SQLServerDriver")),
                                                                    field("dataStoreUrl", string("jdbc:microsoft:sqlserver://LOCALHOST:1433;DatabaseName=goon")),
                                                                    field("dataStoreUser", string("sa")),
                                                                    field("dataStorePassword", string("dataStoreTestQuery")),
                                                                    field("dataStoreTestQuery", string("SET NOCOUNT ON;select test='test';")),
                                                                    field("dataStoreLogFile", string("/usr/local/tomcat/logs/datastore.log")),
                                                                    field("dataStoreInitConns", number("10")),
                                                                    field("dataStoreMaxConns", number("100")),
                                                                    field("dataStoreConnUsageLimit", number("100")),
                                                                    field("dataStoreLogLevel", string("debug")),
                                                                    field("maxUrlLength", number("500"))
                                                            )
                                                    )
                                            ),
                                            object(
                                                    field("servlet-name", string("cofaxEmail")),
                                                    field("servlet-class", string("org.cofax.cds.EmailServlet")),
                                                    field("init-param", object(
                                                                    field("mailHost", string("mail1")),
                                                                    field("mailHostOverride", string("mail2"))
                                                            )
                                                    )
                                            ),
                                            object(
                                                    field("servlet-name", string("cofaxAdmin")),
                                                    field("servlet-class", string("org.cofax.cds.AdminServlet"))
                                            ),
                                            object(
                                                    field("servlet-name", string("fileServlet")),
                                                    field("servlet-class", string("org.cofax.cds.FileServlet"))
                                            ),
                                            object(
                                                    field("servlet-name", string("cofaxTools")),
                                                    field("servlet-class", string("org.cofax.cms.CofaxToolsServlet")),
                                                    field("init-param", object(
                                                                    field("templatePath", string("toolstemplates/")),
                                                                    field("log", number("1")),
                                                                    field("logLocation", string("/usr/local/tomcat/logs/CofaxTools.log")),
                                                                    field("logMaxSize", string("")),
                                                                    field("dataLog", number("1")),
                                                                    field("dataLogLocation", string("/usr/local/tomcat/logs/dataLog.log")),
                                                                    field("dataLogMaxSize", string("")),
                                                                    field("removePageCache", string("/content/admin/remove?cache=pages&id=")),
                                                                    field("removeTemplateCache", string("/content/admin/remove?cache=templates&id=")),
                                                                    field("fileTransferFolder", string("/usr/local/tomcat/webapps/content/fileTransferFolder")),
                                                                    field("lookInContext", number("1")),
                                                                    field("adminGroupID", number("4")),
                                                                    field("betaServer", trueNode())
                                                            )
                                                    )
                                            )
                                    )
                            ),
                            field("servlet-mapping", object(
                                            field("cofaxCDS", string("/")),
                                            field("cofaxEmail", string("/cofaxutil/aemail/*")),
                                            field("cofaxAdmin", string("/admin/*")),
                                            field("fileServlet", string("/static/*")),
                                            field("cofaxTools", string("/tools/*"))
                                    )
                            ),
                            field("taglib", object(
                                    field("taglib-uri", string("cofax.tld")),
                                    field("taglib-location", string("/WEB-INF/tlds/cofax.tld"))
                            ))
                    )
            )
    );
    private static final WriteableJsonObject WRITEABLE_JSON = rootObjectWriter ->
            rootObjectWriter.writeField("web-app", (WriteableJsonObject) webappObjectWriter -> {
                        webappObjectWriter.writeField("servlet", (WriteableJsonArray) arrayWriter -> {
                                    arrayWriter.writeElement((WriteableJsonObject) servletObjectWriter -> {
                                        servletObjectWriter.writeField("servlet-name", (WriteableJsonString) writer -> writer.write("cofaxCDS"));
                                        servletObjectWriter.writeField("servlet-class", (WriteableJsonString) writer -> writer.write("org.cofax.cds.CDSServlet"));
                                        servletObjectWriter.writeField("init-param", (WriteableJsonObject) initParamObjectWriter -> {
                                            initParamObjectWriter.writeField("configGlossary:installationAt", (WriteableJsonString) writer -> writer.write("Philadelphia, PA"));
                                            initParamObjectWriter.writeField("configGlossary:adminEmail", (WriteableJsonString) writer -> writer.write("ksm@pobox.com"));
                                            initParamObjectWriter.writeField("configGlossary:poweredBy", (WriteableJsonString) writer -> writer.write("Cofax"));
                                            initParamObjectWriter.writeField("configGlossary:poweredByIcon", (WriteableJsonString) writer -> writer.write("/images/cofax.gif"));
                                            initParamObjectWriter.writeField("configGlossary:staticPath", (WriteableJsonString) writer -> writer.write("/content/static"));
                                            initParamObjectWriter.writeField("templateProcessorClass", (WriteableJsonString) writer -> writer.write("org.cofax.WysiwygTemplate"));
                                            initParamObjectWriter.writeField("templateLoaderClass", (WriteableJsonString) writer -> writer.write("org.cofax.FilesTemplateLoader"));
                                            initParamObjectWriter.writeField("templatePath", (WriteableJsonString) writer -> writer.write("templates"));
                                            initParamObjectWriter.writeField("templateOverridePath", (WriteableJsonString) writer -> writer.write(""));
                                            initParamObjectWriter.writeField("defaultListTemplate", (WriteableJsonString) writer -> writer.write("listTemplate.htm"));
                                            initParamObjectWriter.writeField("defaultFileTemplate", (WriteableJsonString) writer -> writer.write("articleTemplate.htm"));
                                            initParamObjectWriter.writeField("useJSP", falseNode());
                                            initParamObjectWriter.writeField("jspListTemplate", (WriteableJsonString) writer -> writer.write("listTemplate.jsp"));
                                            initParamObjectWriter.writeField("jspFileTemplate", (WriteableJsonString) writer -> writer.write("articleTemplate.jsp"));
                                            initParamObjectWriter.writeField("cachePackageTagsTrack", (WriteableJsonNumber) writer -> writer.write("200"));
                                            initParamObjectWriter.writeField("cachePackageTagsStore", (WriteableJsonNumber) writer -> writer.write("200"));
                                            initParamObjectWriter.writeField("cachePackageTagsRefresh", (WriteableJsonNumber) writer -> writer.write("60"));
                                            initParamObjectWriter.writeField("cacheTemplatesTrack", (WriteableJsonNumber) writer -> writer.write("100"));
                                            initParamObjectWriter.writeField("cacheTemplatesStore", (WriteableJsonNumber) writer -> writer.write("50"));
                                            initParamObjectWriter.writeField("cacheTemplatesRefresh", (WriteableJsonNumber) writer -> writer.write("15"));
                                            initParamObjectWriter.writeField("cachePagesTrack", (WriteableJsonNumber) writer -> writer.write("200"));
                                            initParamObjectWriter.writeField("cachePagesStore", (WriteableJsonNumber) writer -> writer.write("100"));
                                            initParamObjectWriter.writeField("cachePagesRefresh", (WriteableJsonNumber) writer -> writer.write("10"));
                                            initParamObjectWriter.writeField("cachePagesDirtyRead", (WriteableJsonNumber) writer -> writer.write("10"));
                                            initParamObjectWriter.writeField("searchEngineListTemplate", (WriteableJsonString) writer -> writer.write("forSearchEnginesList.htm"));
                                            initParamObjectWriter.writeField("searchEngineFileTemplate", (WriteableJsonString) writer -> writer.write("forSearchEngines.htm"));
                                            initParamObjectWriter.writeField("searchEngineRobotsDb", (WriteableJsonString) writer -> writer.write("WEB-INF/robots.db"));
                                            initParamObjectWriter.writeField("useDataStore", trueNode());
                                            initParamObjectWriter.writeField("dataStoreClass", (WriteableJsonString) writer -> writer.write("org.cofax.SqlDataStore"));
                                            initParamObjectWriter.writeField("redirectionClass", (WriteableJsonString) writer -> writer.write("org.cofax.SqlRedirection"));
                                            initParamObjectWriter.writeField("dataStoreName", (WriteableJsonString) writer -> writer.write("cofax"));
                                            initParamObjectWriter.writeField("dataStoreDriver", (WriteableJsonString) writer -> writer.write("com.microsoft.jdbc.sqlserver.SQLServerDriver"));
                                            initParamObjectWriter.writeField("dataStoreUrl", (WriteableJsonString) writer -> writer.write("jdbc:microsoft:sqlserver://LOCALHOST:1433;DatabaseName=goon"));
                                            initParamObjectWriter.writeField("dataStoreUser", (WriteableJsonString) writer -> writer.write("sa"));
                                            initParamObjectWriter.writeField("dataStorePassword", (WriteableJsonString) writer -> writer.write("dataStoreTestQuery"));
                                            initParamObjectWriter.writeField("dataStoreTestQuery", (WriteableJsonString) writer -> writer.write("SET NOCOUNT ON;select test='test';"));
                                            initParamObjectWriter.writeField("dataStoreLogFile", (WriteableJsonString) writer -> writer.write("/usr/local/tomcat/logs/datastore.log"));
                                            initParamObjectWriter.writeField("dataStoreInitConns", (WriteableJsonNumber) writer -> writer.write("10"));
                                            initParamObjectWriter.writeField("dataStoreMaxConns", (WriteableJsonNumber) writer -> writer.write("100"));
                                            initParamObjectWriter.writeField("dataStoreConnUsageLimit", (WriteableJsonNumber) writer -> writer.write("100"));
                                            initParamObjectWriter.writeField("dataStoreLogLevel", (WriteableJsonString) writer -> writer.write("debug"));
                                            initParamObjectWriter.writeField("maxUrlLength", (WriteableJsonNumber) writer -> writer.write("500"));
                                        });
                                    });
                                    arrayWriter.writeElement((WriteableJsonObject) servletObjectWriter -> {
                                        servletObjectWriter.writeField("servlet-name", (WriteableJsonString) writer -> writer.write("cofaxEmail"));
                                        servletObjectWriter.writeField("servlet-class", (WriteableJsonString) writer -> writer.write("org.cofax.cds.EmailServlet"));
                                        servletObjectWriter.writeField("init-param", (WriteableJsonObject) initParamObjectWriter -> {
                                            initParamObjectWriter.writeField("mailHost", (WriteableJsonString) writer -> writer.write("mail1"));
                                            initParamObjectWriter.writeField("mailHostOverride", (WriteableJsonString) writer -> writer.write("mail2"));
                                        });
                                    });
                                    arrayWriter.writeElement((WriteableJsonObject) servletObjectWriter -> {
                                        servletObjectWriter.writeField("servlet-name", (WriteableJsonString) writer -> writer.write("cofaxAdmin"));
                                        servletObjectWriter.writeField("servlet-class", (WriteableJsonString) writer -> writer.write("org.cofax.cds.AdminServlet"));
                                    });
                                    arrayWriter.writeElement((WriteableJsonObject) servletObjectWriter -> {
                                        servletObjectWriter.writeField("servlet-name", (WriteableJsonString) writer -> writer.write("fileServlet"));
                                        servletObjectWriter.writeField("servlet-class", (WriteableJsonString) writer -> writer.write("org.cofax.cds.FileServlet"));
                                    });
                                    arrayWriter.writeElement((WriteableJsonObject) servletObjectWriter -> {
                                        servletObjectWriter.writeField("servlet-name", (WriteableJsonString) writer -> writer.write("cofaxTools"));
                                        servletObjectWriter.writeField("servlet-class", (WriteableJsonString) writer -> writer.write("org.cofax.cms.CofaxToolsServlet"));
                                        servletObjectWriter.writeField("init-param", (WriteableJsonObject) initParamObjectWriter -> {
                                            initParamObjectWriter.writeField("templatePath", (WriteableJsonString) writer -> writer.write("toolstemplates/"));
                                            initParamObjectWriter.writeField("log", (WriteableJsonNumber) writer -> writer.write("1"));
                                            initParamObjectWriter.writeField("logLocation", (WriteableJsonString) writer -> writer.write("/usr/local/tomcat/logs/CofaxTools.log"));
                                            initParamObjectWriter.writeField("logMaxSize", (WriteableJsonString) writer -> writer.write(""));
                                            initParamObjectWriter.writeField("dataLog", (WriteableJsonNumber) writer -> writer.write("1"));
                                            initParamObjectWriter.writeField("dataLogLocation", (WriteableJsonString) writer -> writer.write("/usr/local/tomcat/logs/dataLog.log"));
                                            initParamObjectWriter.writeField("dataLogMaxSize", (WriteableJsonString) writer -> writer.write(""));
                                            initParamObjectWriter.writeField("removePageCache", (WriteableJsonString) writer -> writer.write("/content/admin/remove?cache=pages&id="));
                                            initParamObjectWriter.writeField("removeTemplateCache", (WriteableJsonString) writer -> writer.write("/content/admin/remove?cache=templates&id="));
                                            initParamObjectWriter.writeField("fileTransferFolder", (WriteableJsonString) writer -> writer.write("/usr/local/tomcat/webapps/content/fileTransferFolder"));
                                            initParamObjectWriter.writeField("lookInContext", (WriteableJsonNumber) writer -> writer.write("1"));
                                            initParamObjectWriter.writeField("adminGroupID", (WriteableJsonNumber) writer -> writer.write("4"));
                                            initParamObjectWriter.writeField("betaServer", trueNode());
                                        });
                                    });
                                }
                        );
                        webappObjectWriter.writeField("servlet-mapping", (WriteableJsonObject) servletMappingObjectWriter -> {
                            servletMappingObjectWriter.writeField("cofaxCDS", (WriteableJsonString) writer -> writer.write("/"));
                            servletMappingObjectWriter.writeField("cofaxEmail", (WriteableJsonString) writer -> writer.write("/cofaxutil/aemail/*"));
                            servletMappingObjectWriter.writeField("cofaxAdmin", (WriteableJsonString) writer -> writer.write("/admin/*"));
                            servletMappingObjectWriter.writeField("fileServlet", (WriteableJsonString) writer -> writer.write("/static/*"));
                            servletMappingObjectWriter.writeField("cofaxTools", (WriteableJsonString) writer -> writer.write("/tools/*"));
                        });
                        webappObjectWriter.writeField("taglib", (WriteableJsonObject) taglibObjectWriter -> {
                            taglibObjectWriter.writeField("taglib-uri", (WriteableJsonString) writer -> writer.write("cofax.tld"));
                            taglibObjectWriter.writeField("taglib-location", (WriteableJsonString) writer -> writer.write("/WEB-INF/tlds/cofax.tld"));
                        });

                    }
            );

    private final JsonGenerator compactJsonGenerator = new JsonGenerator().style(COMPACT);
    private final JsonGenerator prettyJsonGenerator = new JsonGenerator().style(PRETTY);

    @Benchmark
    public void compactJdomGenerate(final Blackhole blackhole) throws IOException {
        compactJsonGenerator.generate(new BlackholeWriter(blackhole), JSON_NODE);
    }

    @Benchmark
    public void prettyJdomGenerate(final Blackhole blackhole) throws IOException {
        prettyJsonGenerator.generate(new BlackholeWriter(blackhole), JSON_NODE);
    }

    @Benchmark
    public void compactStreamingGenerate(final Blackhole blackhole) throws IOException {
        compactJsonGenerator.generate(new BlackholeWriter(blackhole), WRITEABLE_JSON);
    }

    @Benchmark
    public void prettyStreamingGenerate(final Blackhole blackhole) throws IOException {
        prettyJsonGenerator.generate(new BlackholeWriter(blackhole), WRITEABLE_JSON);
    }

    @Benchmark
    public void compactJdomGenerateString(final Blackhole blackhole) throws IOException {
        blackhole.consume(compactJsonGenerator.generate(JSON_NODE));
    }

    @Benchmark
    public void prettyJdomGenerateString(final Blackhole blackhole) throws IOException {
        blackhole.consume(prettyJsonGenerator.generate(JSON_NODE));
    }

    @Benchmark
    public void compactStreamingGenerateString(final Blackhole blackhole) throws IOException {
        blackhole.consume(compactJsonGenerator.generate(WRITEABLE_JSON));
    }

    @Benchmark
    public void prettyStreamingGenerateString(final Blackhole blackhole) throws IOException {
        blackhole.consume(prettyJsonGenerator.generate(WRITEABLE_JSON));
    }

    private static final class BlackholeWriter extends Writer {
        private final Blackhole blackhole;

        public BlackholeWriter(Blackhole blackhole) {
            this.blackhole = blackhole;
        }

        @Override
        public void write(final char[] cbuf, final int off, final int len) {
            blackhole.consume(cbuf);
        }

        @Override
        public void flush() {
        }

        @Override
        public void close() {
        }
    }
}
