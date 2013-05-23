/*
 * Copyright 2013 Mark Slater
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at
 *
 * 	http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.
 */

package build.maven;

import net.sourceforge.writexml.*;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URI;
import java.util.Properties;

import static java.util.Arrays.asList;
import static net.sourceforge.writexml.Attributes.attributes;
import static net.sourceforge.writexml.Document.document;
import static net.sourceforge.writexml.Namespace.namespace;

public class PomGenerator {

    public static void main(String[] args) throws Exception {
        final File destination = new File(args[0]);
        final String version = versionString();

        writeXml(pom(version), destination, "pom.xml");
        writeXml(settings(args[1], args[2], args[3], args[4]), destination, "settings.xml");
    }

    private static Tag settings(final String serverId, final String username, final String password, final String gpgPassphrase) {
        return settingsTag(TagName.tagName("settings"),
                settingsTag(TagName.tagName("servers"),
                        settingsTag(TagName.tagName("server"),
                                settingsTag(TagName.tagName("id"), Text.text(serverId)),
                                settingsTag(TagName.tagName("username"), Text.text(username)),
                                settingsTag(TagName.tagName("password"), Text.text(password))
                        )
                ),
                settingsTag(TagName.tagName("pluginGroups"),
                        settingsTag(TagName.tagName("pluginGroup"), Text.text("org.sonatype.plugins"))
                ),
                settingsTag(TagName.tagName("profiles"),
                        settingsTag(TagName.tagName("profile"),
                                settingsTag(TagName.tagName("id"), Text.text("gpg")),
                                settingsTag(TagName.tagName("properties"),
                                        settingsTag(TagName.tagName("gpg.passphrase"), Text.text(gpgPassphrase))
                                )
                        )
                )
        );
    }

    private static Tag pom(final String version) {
        return pomTag(TagName.tagName("project"),
                pomTag(TagName.tagName("modelVersion"), Text.text("4.0.0")),
                pomTag(TagName.tagName("groupId"), Text.text("net.sourceforge.argo")),
                pomTag(TagName.tagName("artifactId"), Text.text("argo")),
                pomTag(TagName.tagName("version"), Text.text(version)),
                pomTag(TagName.tagName("packaging"), Text.text("jar")),
                pomTag(TagName.tagName("name"), Text.text("Argo")),
                pomTag(TagName.tagName("description"), Text.text("Argo is an open source JSON parser and generator written in Java.  It offers document, push, and pull APIs.")),
                pomTag(TagName.tagName("url"), Text.text("http://argo.sourceforge.net")),
                pomTag(TagName.tagName("licenses"),
                        pomTag(TagName.tagName("license"),
                                pomTag(TagName.tagName("name"), Text.text("The Apache Software License, Version 2.0")),
                                pomTag(TagName.tagName("url"), Text.text("http://www.apache.org/licenses/LICENSE-2.0.txt")),
                                pomTag(TagName.tagName("distribution"), Text.text("repo"))
                        )
                ),
                pomTag(TagName.tagName("scm"),
                        pomTag(TagName.tagName("url"), Text.text("https://svn.code.sf.net/p/argo/code/tags/" + version))
                ),
                pomTag(TagName.tagName("dependencies")),
                pomTag(TagName.tagName("parent"),
                        pomTag(TagName.tagName("groupId"), Text.text("org.sonatype.oss")),
                        pomTag(TagName.tagName("artifactId"), Text.text("oss-parent")),
                        pomTag(TagName.tagName("version"), Text.text("7"))
                )
        );
    }

    private static Tag pomTag(final TagName tagName, final TagContent... children) {
        return Tag.tag(namespace(URI.create("http://maven.apache.org/POM/4.0.0")), tagName, attributes(), asList(children));
    }

    private static Tag settingsTag(final TagName tagName, final TagContent... children) {
        return Tag.tag(namespace(URI.create("http://maven.apache.org/SETTINGS/1.0.0")), tagName, attributes(), asList(children));
    }

    private static String versionString() throws IOException {
        final Properties properties = new Properties();
        properties.load(new FileReader("version.properties"));
        return properties.getProperty("argo.version.major") + "." + properties.getProperty("argo.version.minor");
    }

    private static void writeXml(final Tag root, final File destination, final String fileName) throws IOException, XmlWriteException {
        final File file = new File(destination, fileName);
        final FileWriter fileWriter = new FileWriter(file);
        new PrettyXmlFormatter().write(document(root), fileWriter);
        fileWriter.close();
    }

}
