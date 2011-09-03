/*
 * Copyright 2011 Mark Slater
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at
 *
 * 	http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.
 */

package documentation;

import org.sourceforge.xazzle.xhtml.BlockLevelTag;
import org.sourceforge.xazzle.xhtml.Href;
import org.sourceforge.xazzle.xhtml.HtmlTag;
import org.sourceforge.xazzle.xhtml.InlineTag;

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

final class ArgoPage {

    private ArgoPage() {
    }

    static HtmlTag anArgoPage(final BlockLevelTag... content) {
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

    static InlineTag variableName(String name) {
        return codeTag(xhtmlText(name));
    }

    static InlineTag codeSnippet(String snippet) {
        return codeTag(xhtmlText(snippet));
    }

    static BlockLevelTag codeBlock(String someCode) {
        return divTag(
                xhtmlText(someCode)
        ).withClass(className("code"));
    }

    static InlineTag simpleNameOf(final Class clazz) {
        return codeTag(xhtmlText(clazz.getSimpleName()));
    }
}
