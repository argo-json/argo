/*
 *  Copyright 2024 Mark Slater
 *
 *  Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at
 *
 *  	http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.
 */

buildscript {
    repositories {
        mavenCentral()
    }
}

plugins {
    java
    signing
    `maven-publish`
    idea
    pmd
    `java-test-fixtures`
    `jvm-test-suite`
    id("me.champeau.jmh") version "0.7.2"
    id("com.palantir.revapi") version "1.7.0"
    id("com.github.spotbugs") version "6.0.9"
    id("io.github.gradle-nexus.publish-plugin") version "1.3.0"
    id("com.gitlab.svg2ico") version "1.4"
    id("org.asciidoctor.jvm.convert") version "4.0.2"
    id("org.asciidoctor.jvm.gems") version "4.0.2"

    id("release.sourceforge")
}

group = "net.sourceforge.argo"
description = "Argo is an open source JSON parser and generator written in Java.  It offers document, push, and pull APIs."

repositories {
    mavenCentral()
    ruby {
        gems()
    }
}

sourceSets {
    create("moduleInfo")
}

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(8)
    }
}

dependencies {
    "moduleInfoImplementation"(sourceSets["main"].output)

    testFixturesImplementation(group = "org.apache.commons", name = "commons-lang3", version = "3.14.0")
    testFixturesImplementation(group = "commons-io", name = "commons-io", version = "2.15.1")
    testFixturesImplementation(group = "net.sourceforge.ickles", name = "ickles", version = "0.21")
    testFixturesImplementation(group = "org.hamcrest", name = "hamcrest", version = "2.2")

    spotbugs(group = "com.github.spotbugs", name = "spotbugs", version = "4.8.3")

    asciidoctorGems(group = "rubygems", name = "asciidoctor-tabs", version = "1.0.0.beta.6")

    jmhImplementation(testFixtures(project))
}

testing {
    @Suppress("UnstableApiUsage")
    suites {
        val test by getting(JvmTestSuite::class) {
            useJUnitJupiter()
            dependencies {
                implementation("org.junit.jupiter:junit-jupiter-api:5.10.2")
                implementation("org.junit.jupiter:junit-jupiter-params:5.10.2")
                implementation("org.hamcrest:hamcrest:2.2")
                implementation("net.sourceforge.ickles:ickles:0.21")
                implementation("commons-io:commons-io:2.16.0")
            }
        }

        register<JvmTestSuite>("docs") {
            useJUnitJupiter()
            dependencies {
                implementation(project())
            }
        }

        register<JvmTestSuite>("limitations") {
            useJUnitJupiter()
            dependencies {
                implementation(project())
                implementation(testFixtures(project()))
                implementation("commons-io:commons-io:2.15.1")
            }
        }
    }
}

idea {
    project {
        jdkName = "1.8"
        languageLevel.level = "1.5"
    }
    module {
        jdkName = "11"
    }
}

jmh {
    includes = listOf("jdomParse")
    includeTests = false
}

revapi {
    setOldVersion("6.5")
}

pmd {
    toolVersion = "7.0.0"
    ruleSetFiles = files("tools/pmd-ruleset.xml", "tools/pmd-non-docs-extra-ruleset.xml")
    ruleSets = emptyList()
}

tasks {
    compileJava {
        sourceCompatibility = "1.5"
        targetCompatibility = "1.5"
        options.compilerArgs.add("-Xlint:-options")
    }

    named<JavaCompile>("compileModuleInfoJava") {
        sourceCompatibility = "9"
        targetCompatibility = "9"
        javaCompiler = project.javaToolchains.compilerFor {
            languageVersion = JavaLanguageVersion.of(9)
        }
        doFirst {
            classpath += sourceSets["main"].compileClasspath

            options.compilerArgs = listOf(
                "--module-path", classpath.asPath,
                "-d", sourceSets["main"].output.classesDirs.asPath
            )
        }
    }

    val compileSmallJava by registering(JavaCompile::class) {
        sourceCompatibility = "1.5"
        targetCompatibility = "1.5"
        javaCompiler = project.javaToolchains.compilerFor {
            languageVersion = JavaLanguageVersion.of(8)
        }
        source = sourceSets["main"].allSource
        classpath = sourceSets["main"].compileClasspath
        destinationDirectory.set(project.layout.buildDirectory.dir("small-classes/main"))
        options.compilerArgs = listOf("-g:none", "-Xlint:-options")
    }

    check {
        @Suppress("UnstableApiUsage")
        dependsOn(testing.suites["docs"])
    }

    jar {
        from(sourceSets["main"].output)
        from(sourceSets["moduleInfo"].output)
    }

    val combinedJar by registering(Jar::class) {
        archiveClassifier = "combined"
        from(sourceSets["main"].allSource)
        from(sourceSets["main"].output)
        from(sourceSets["moduleInfo"].output)
    }

    val smallJar by registering(Jar::class) {
        dependsOn(compileSmallJava)
        archiveClassifier = "small"
        from(project.layout.buildDirectory.dir("small-classes/main"))
        from(sourceSets["moduleInfo"].output)
    }

    javadoc {
        title = "Argo version $version"
        exclude("argo/internal")
    }

    named<JavaCompile>("compileDocsJava") {
        javaCompiler.set(project.javaToolchains.compilerFor {
            languageVersion.set(JavaLanguageVersion.of(21))
        })
    }

    named<Test>("docs") {
        javaLauncher.set(project.javaToolchains.launcherFor {
            languageVersion.set(JavaLanguageVersion.of(21))
        })
    }

    named<Test>("limitations") {
        testLogging {
            showStandardStreams = true
        }
        maxHeapSize = "448g"
    }

    val ico by registering(com.gitlab.svg2ico.Svg2IcoTask::class) {
        group = "documentation"
        source {
            sourcePath = file("resources/favicon.svg")
            output { width = 64; height = 64 }
        }
        source {
            sourcePath = file("resources/favicon.svg")
            userStyleSheet = file("resources/no-outline.css")
            output { width = 48; height = 48 }
            output { width = 32; height = 32 }
            output { width = 24; height = 24 }
            output { width = 16; height = 16 }
        }
        destination = project.layout.buildDirectory.file("icons/favicon.ico")
    }

    val png by registering(com.gitlab.svg2ico.Svg2PngTask::class) {
        group = "documentation"
        source = file("resources/favicon.svg")
        width = 128
        height = 128
        destination = project.layout.buildDirectory.file("icons/favicon.png")
    }

    asciidoctor {
        dependsOn(ico, png, javadoc, "asciidoctorGemsPrepare") // doesn't seem to infer dependencies properly from the resources CopySpec
        resources {
            from(ico, png)
            from(javadoc) {
                into("javadoc")
            }
        }
        asciidoctorj {
            requires(
                "asciidoctor",
                project.layout.buildDirectory.file(".asciidoctorGems/gems/asciidoctor-tabs-1.0.0.beta.6/lib/asciidoctor-tabs.rb") // TODO this is a workaround for https://github.com/asciidoctor/asciidoctor-gradle-plugin/issues/718
            )
        }
    }

    val documentationTar by registering(Tar::class) {
        group = "documentation"
        from(asciidoctor)
        archiveBaseName.set("documentation")
        compression = Compression.GZIP
    }

    pmdMain {
        ruleSetFiles = files("tools/pmd-ruleset.xml", "tools/pmd-non-docs-extra-ruleset.xml", "tools/pmd-main-extra-ruleset.xml")
        ruleSets = emptyList()
    }

    named<Pmd>("pmdDocs") {
        ruleSetFiles = files("tools/pmd-ruleset.xml")
        ruleSets = emptyList()
    }

    spotbugsMain {
        excludeFilter = file("tools/spotbugs-main-filter.xml")
    }

    named<com.github.spotbugs.snom.SpotBugsTask>("spotbugsDocs") {
        excludeFilter = file("tools/spotbugs-docs-filter.xml")
    }

    spotbugsTest {
        excludeFilter = file("tools/spotbugs-test-filter.xml")
    }

    spotbugsTestFixtures {
        excludeFilter = file("tools/spotbugs-testFixtures-filter.xml")
    }

    val release by registering {
        group = "publishing"
        dependsOn(clean, build, "publishToSonatype", closeAndReleaseStagingRepository, sourceforgeRelease, incrementVersionNumber)
    }

    incrementVersionNumber {
        mustRunAfter(closeAndReleaseStagingRepository, sourceforgeRelease)
    }
}

val javadocJar by tasks.registering(Jar::class) {
    group = "documentation"
    archiveClassifier = "javadoc"
    from(tasks.javadoc)
}

val sourcesJar by tasks.registering(Jar::class) {
    group = "documentation"
    archiveClassifier = "sources"
    from(sourceSets["main"].allSource)
}

artifacts {
    archives(javadocJar)
    archives(sourcesJar)
}

releasing {
    combinedJar = tasks.named<Jar>("combinedJar").get().archiveFile
    smallJar = tasks.named<Jar>("smallJar").get().archiveFile
    documentationTar = tasks.named<Tar>("documentationTar").get().archiveFile
}

publishing {
    publications {
        create<MavenPublication>("mavenJava") {
            from(components["java"])
            artifact(sourcesJar)
            artifact(javadocJar)
            pom {
                name = "Argo"
                description = project.description
                url = "http://argo.sourceforge.net"
                scm {
                    url = "git://git.code.sf.net/p/argo/git"
                }
                developers {
                    developer {
                        id = "mos20"
                        name = "Mark Slater"
                    }
                }
                licenses {
                    license {
                        name = "The Apache Software License, Version 2.0"
                        url = "http://www.apache.org/licenses/LICENSE-2.0.txt"
                        distribution = "repo"
                    }
                }
            }
        }
    }
}

signing {
    useGpgCmd()
    sign(publishing.publications["mavenJava"])
}

nexusPublishing {
    repositories {
        sonatype {
            stagingProfileId = "12462889504a1e"
            username.set(project.findProperty("ossrhUser").toString())
            password.set(project.findProperty("ossrhPassword").toString())
        }
    }
}

val javaComponent = components["java"] as AdhocComponentWithVariants
javaComponent.withVariantsFromConfiguration(configurations["testFixturesApiElements"]) { skip() }
javaComponent.withVariantsFromConfiguration(configurations["testFixturesRuntimeElements"]) { skip() }
