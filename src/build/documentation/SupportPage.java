/*
 * Copyright 2013 Mark Slater
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at
 *
 * 	http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.
 */

package documentation;

import net.sourceforge.xazzle.xhtml.HtmlTag;

import static net.sourceforge.xazzle.xhtml.Href.href;
import static net.sourceforge.xazzle.xhtml.Tags.*;

final class SupportPage {

    private SupportPage() {
    }

    @SuppressWarnings({"StaticMethodOnlyUsedInOneClass"})
    static HtmlTag supportPage() {
        return ArgoPage.anArgoPage(
                h2Tag(xhtmlText("Support")),
                paragraphTag(
                        xhtmlText("The best way to get help on Argo is via the "),
                        anchorTag(xhtmlText("help forum")).withHref(href("https://sourceforge.net/projects/argo/forums/forum/887785")),
                        xhtmlText(".")),
                paragraphTag(
                        xhtmlText("Alternatively, report a bug or browse reported bugs via the "),
                        anchorTag(xhtmlText("bug tracker")).withHref(href("http://sourceforge.net/tracker/?group_id=245339&atid=1169592")),
                        xhtmlText(".")
                ),
                paragraphTag(
                        xhtmlText("Finally, there is an "),
                        anchorTag(xhtmlText("open discussion forum")).withHref(href("https://sourceforge.net/projects/argo/forums/forum/887784")),
                        xhtmlText(" for anything else.")
                )
        );
    }
}
