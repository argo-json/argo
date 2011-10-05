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

import org.sourceforge.xazzle.xhtml.HtmlTag;

import static documentation.ArgoPage.anArgoPage;
import static documentation.ArgoPage.codeBlock;
import static org.sourceforge.xazzle.xhtml.Href.href;
import static org.sourceforge.xazzle.xhtml.Tags.*;

final class DownloadsPage {

    private DownloadsPage() {
    }

    @SuppressWarnings({"StaticMethodOnlyUsedInOneClass"})
    static HtmlTag downloadsPage(final String version) {
        String standardJarUrl = "https://sourceforge.net/projects/argo/files/argo/" + version + "/argo-" + version + ".jar/download";
        String smallJarUrl = "https://sourceforge.net/projects/argo/files/argo/" + version + "/argo-small-" + version + ".jar/download";
        String subversionUrl = "https://argo.svn.sourceforge.net/svnroot/argo/tags/" + version;
        return anArgoPage(
                h2Tag(xhtmlText("Downloads")),
                paragraphTag(
                        xhtmlText("Argo is available under the "),
                        anchorTag(xhtmlText("Apache 2 license")).withHref(href("http://www.apache.org/licenses/LICENSE-2.0")),
                        xhtmlText(".  It can be downloaded in four forms:")),
                unorderedListTag(
                        listItemTag(
                                xhtmlText("the "), anchorTag(xhtmlText("standard jar")).withHref(href(standardJarUrl)), xhtmlText(", with source code included,")
                        ),
                        listItemTag(
                                xhtmlText("a "), anchorTag(xhtmlText("compact jar")).withHref(href(smallJarUrl)), xhtmlText(", with no source, and no debug information,")
                        ),
                        listItemTag(
                                xhtmlText("as a Maven dependency from central, using "), codeBlock("<dependency>\n" +
                                "   <groupId>net.sourceforge.argo</groupId>\n" +
                                "   <artifactId>argo</artifactId>\n" +
                                "   <version>" + version + "</version>\n" +
                                "</dependency>")
                        ),
                        listItemTag(
                                xhtmlText("or as the full source code including tests etc. using Subversion from "), codeTag(anchorTag(xhtmlText(subversionUrl)).withHref(href(subversionUrl))), xhtmlText(".")
                        )
                ),
                paragraphTag(
                        xhtmlText("It has no runtime dependencies, and all dependencies used in tests are in Subversion.")
                )
        );
    }
}
