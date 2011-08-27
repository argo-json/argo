/*
 * Copyright 2011 Mark Slater
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at
 *
 * 	http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.
 */

package argo;

import org.sourceforge.writexml.Document;
import org.sourceforge.writexml.JaxpXmlWriter;
import org.sourceforge.xazzle.xhtml.BlockLevelTag;
import org.sourceforge.xazzle.xhtml.Href;
import org.sourceforge.xazzle.xhtml.HtmlTag;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.Properties;

import static org.sourceforge.xazzle.xhtml.AlternateText.alternateText;
import static org.sourceforge.xazzle.xhtml.ClassName.className;
import static org.sourceforge.xazzle.xhtml.Href.href;
import static org.sourceforge.xazzle.xhtml.Id.id;
import static org.sourceforge.xazzle.xhtml.ImageSource.imageSource;
import static org.sourceforge.xazzle.xhtml.MetaContent.metaContent;
import static org.sourceforge.xazzle.xhtml.MetaName.metaName;
import static org.sourceforge.xazzle.xhtml.MimeType.mimeType;
import static org.sourceforge.xazzle.xhtml.Relationship.STYLESHEET;
import static org.sourceforge.xazzle.xhtml.Relationship.relationship;
import static org.sourceforge.xazzle.xhtml.ScriptSource.scriptSource;
import static org.sourceforge.xazzle.xhtml.Tags.*;
import static org.sourceforge.xazzle.xhtml.XhtmlDimension.pixels;

public class Index {
    public static void main(String[] args) throws Exception {
        Properties properties = new Properties();
        properties.load(new FileReader("version.properties"));
        final String version = properties.getProperty("argo.version.major") + "." + properties.getProperty("argo.version.minor");
        final HtmlTag indexPage = anArgoPage(
                h2Tag(xhtmlText("Introduction")),
                paragraphTag(xhtmlText("Argo is a JSON parser and generator for Java. It offers three parse interfaces - a push parser, a " +
                        "pull parser, and a DOM style parser. It is written to be easy to use, typesafe, and fast. It is open source, and " +
                        "free for you to use.")),
                paragraphTag(
                        xhtmlText("The latest version of Argo available for download is "),
                        anchorTag(xhtmlText(version)).withHref(href("https://sourceforge.net/projects/argo/files/latest")),
                        xhtmlText(".  The "),
                        anchorTag(xhtmlText("javadoc")).withHref(href("javadoc/")),
                        xhtmlText(" is also available online.")
                ),
                h2Tag(xhtmlText("Example")),
                paragraphTag(
                        xhtmlText("A brief example demonstrates the DOM style parser. The example is based on the following JSON, " +
                                "which is assumed to be available in a "),
                        codeTag(xhtmlText("String")),
                        xhtmlText(" called "),
                        codeTag(xhtmlText("jsonText")),
                        xhtmlText(".")
                ),
                divTag(
                        xhtmlText("{\n" +
                                "    \"name\": \"Black Lace\",\n" +
                                "    \"sales\": 110921,\n" +
                                "    \"totalRoyalties\": 10223.82,\n" +
                                "    \"singles\": [\n" +
                                "        \"Superman\", \"Agadoo\"\n" +
                                "    ]\n" +
                                "}")
                ).withClass(className("code")),
                paragraphTag(
                        xhtmlText("We can use Argo to get the second single like this:")
                ),
                divTag(
                        xhtmlText("String secondSingle = new JdomParser().parse(jsonText)\n" +
                                "    .getStringValue(\"singles\", 1);")
                ).withClass(className("code")),
                paragraphTag(
                        xhtmlText("On the first line, we parse the JSON text into an object hierarchy. There are various options for " +
                                "navigating this, but the simplest is just to give the path and type of node we expect.")
                ),
                paragraphTag(
                        xhtmlText("On line two, we ask for the JSON string at index 1 of the array in the "),
                        codeTag(xhtmlText("singles")),
                        xhtmlText(" field.")
                ),
                paragraphTag(
                        xhtmlText("If we check the "),
                        codeTag(xhtmlText("secondSingle")),
                        xhtmlText(" variable, we will find, as expected, it contains the "),
                        codeTag(xhtmlText("String")),
                        xhtmlText(" \""), codeTag(xhtmlText("Agadoo")), xhtmlText("\".")
                )
        );
        final File file = new File(args[0], "index.html");
        final FileWriter fileWriter = new FileWriter(file);
        new JaxpXmlWriter(fileWriter).write(new Document(indexPage.asWriteableToXml()));
        fileWriter.close();
    }

    private static HtmlTag anArgoPage(final BlockLevelTag... content) {
        final Href projectSiteHref = href("https://sourceforge.net/projects/argo");
        return htmlTag(
                headTag(
                        titleTag("Argo - A simple JSON parser and generator for Java"),
                        linkTag()
                                .withRelationships(STYLESHEET)
                                .withMimeType(mimeType("text/css"))
                                .withHref(href("argo.css")),
                        linkTag()
                                .withRelationships(relationship("icon"))
                                .withMimeType(mimeType("image/png"))
                                .withHref(href("favicon-32x32.png")),
                        metaTag(metaName("description"), metaContent("Argo is an open source JSON parser and generator written in Java.  It offers document, push, and pull APIs.  It is free to download and use in your project.")),
                        metaTag(metaName("verify-v1"), metaContent("8/1zmu6pwHM286FJ1VE9nWTdr1SF5VE819uJDcFXTj4="))
                ),
                bodyTag(
                        divTag(
                                divTag(
                                        h1Tag(xhtmlText("Argo"))
                                ).withId(id("header")),
                                divTag(
                                        unorderedListTag(
                                                listItemTag(
                                                        anchorTag(xhtmlText("Home"))
                                                                .withHref(href("index.html"))
                                                ),
                                                listItemTag(
                                                        anchorTag(xhtmlText("Downloads"))
                                                                .withHref(href("downloads.html"))
                                                ),
                                                listItemTag(
                                                        anchorTag(xhtmlText("Documentation"))
                                                                .withHref(href("documentation.html"))
                                                ),
                                                listItemTag(
                                                        anchorTag(xhtmlText("Support"))
                                                                .withHref(href("support.html"))
                                                ),
                                                listItemTag(
                                                        anchorTag(xhtmlText("Project Site"))
                                                                .withHref(projectSiteHref)
                                                )
                                        )
                                ).withId(id("navigation")),
                                divTag(
                                        content
                                ).withId(id("content")),
                                divTag(
                                        unorderedListTag(
                                                listItemTag(
                                                        anchorTag(
                                                                imageTag(
                                                                        imageSource("http://sflogo.sourceforge.net/sflogo.php?group_id=245339&type=13"),
                                                                        alternateText("Get Argo at SourceForge.net. Fast, secure and Free Open Source software downloads")
                                                                )
                                                                        .withHeight(pixels("30"))
                                                                        .withWidth(pixels("120"))
                                                        ).withHref(projectSiteHref)
                                                ),
                                                listItemTag(
                                                        anchorTag(
                                                                imageTag(
                                                                        imageSource("http://jigsaw.w3.org/css-validator/images/vcss"),
                                                                        alternateText("Valid CSS!")
                                                                )
                                                                        .withHeight(pixels("31"))
                                                                        .withWidth(pixels("88"))
                                                        ).withHref(href("http://jigsaw.w3.org/css-validator/check/referer"))
                                                ),
                                                listItemTag(
                                                        anchorTag(
                                                                imageTag(
                                                                        imageSource("http://www.w3.org/Icons/valid-xhtml10"),
                                                                        alternateText("Valid XHTML 1.0 Strict")
                                                                )
                                                                        .withHeight(pixels("31"))
                                                                        .withWidth(pixels("88"))
                                                        ).withHref(href("http://validator.w3.org/check?uri=referer"))
                                                )
                                        )
                                ).withId(id("footer"))
                        ).withId(id("root")),
                        divTag(
                                scriptTag(mimeType("text/javascript")).withSource(scriptSource("piwik_1.js")),
                                scriptTag(mimeType("text/javascript")).withSource(scriptSource("piwik_2.js")),
                                objectTag(
                                        noScriptTag(
                                                paragraphTag(
                                                        imageTag(imageSource("http://sourceforge.net/apps/piwik/argo/piwik.php?idsite=1"), alternateText("piwik"))
                                                )
                                        )
                                )
                        )
                )
        );
    }
}
