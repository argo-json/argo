/*
 *  Copyright 2024 Mark Slater
 *
 *  Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at
 *
 *  	http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.
 */
plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version ("0.8.0")
}

rootProject.name = "argo"

dependencyResolutionManagement {
    versionCatalogs {
        create("libs") {
            version("asciidoctor", "4.0.2")
            version("junit", "5.10.2")

            plugin("jmh", "me.champeau.jmh").version("0.7.2")
            plugin("revapi", "com.palantir.revapi").version("1.7.0")
            plugin("spotbugs", "com.github.spotbugs").version("6.0.12")
            plugin("nexusPublish", "io.github.gradle-nexus.publish-plugin").version("2.0.0")
            plugin("svg2ico", "com.gitlab.svg2ico").version("1.4")
            plugin("asciidoctorConvert", "org.asciidoctor.jvm.convert").versionRef("asciidoctor")
            plugin("asciidoctorGems", "org.asciidoctor.jvm.gems").versionRef("asciidoctor")

            library("junitJupiterParams", "org.junit.jupiter", "junit-jupiter-params").versionRef("junit")
            library("commonsLang", "org.apache.commons", "commons-lang3").version("3.14.0")
            library("commonsIO", "commons-io", "commons-io").version("2.16.1")
            library("ickles", "net.sourceforge.ickles", "ickles").version("0.21")
            library("hamcrest", "org.hamcrest", "hamcrest").version("2.2")

            library("spotbugs", "com.github.spotbugs", "spotbugs").version("4.8.3")

            library("asciidoctorTabs", "rubygems", "asciidoctor-tabs").version("1.0.0.beta.6")
        }
    }
}