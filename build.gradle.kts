/*
 *  Copyright 2025 Mark Slater
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
    alias(libs.plugins.jmh)
    alias(libs.plugins.revapi)
    alias(libs.plugins.spotbugs)
    alias(libs.plugins.nexusPublish)
    alias(libs.plugins.svg2ico)
    alias(libs.plugins.asciidoctorConvert)
    alias(libs.plugins.asciidoctorGems)

    id("release")
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

    testFixturesImplementation(libs.commonsLang)
    testFixturesImplementation(libs.commonsIO)
    testFixturesImplementation(libs.ickles)
    testFixturesImplementation(libs.hamcrest)

    spotbugs(libs.spotbugs)

    asciidoctorGems(libs.asciidoctorTabs)

    jmhImplementation(testFixtures(project))
}

testing {
    @Suppress("UnstableApiUsage")
    suites {
        named<JvmTestSuite>("test") {
            useJUnitJupiter(libs.versions.junit)
            dependencies {
                implementation(libs.junitJupiterParams)
                implementation(libs.hamcrest)
                implementation(libs.ickles)
                implementation(libs.commonsIO)
            }
        }

        register<JvmTestSuite>("docs") {
            useJUnitJupiter(libs.versions.junit)
            dependencies {
                implementation(project())
            }
        }

        register<JvmTestSuite>("limitations") {
            useJUnitJupiter(libs.versions.junit)
            dependencies {
                implementation(project())
                implementation(testFixtures(project()))
                implementation(libs.commonsIO)
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
    includes = listOf("Parse")
    includeTests = false
}

revapi {
    setOldVersion("7.0")
}

pmd {
    toolVersion = "7.11.0"
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

    register<Jar>("combinedJar") {
        archiveClassifier = "combined"
        from(sourceSets["main"].allSource)
        from(sourceSets["main"].output)
        from(sourceSets["moduleInfo"].output)
    }

    register<Jar>("smallJar") {
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
        width = 500
        height = 500
        destination = project.layout.buildDirectory.file("icons/favicon.png")
    }

    asciidoctor {
        dependsOn(ico, png, javadoc, "asciidoctorGemsPrepare") // doesn't seem to infer dependencies properly from the resources CopySpec
        jvm {
            jvmArgs(
                "--add-opens", "java.base/sun.nio.ch=ALL-UNNAMED",
                "--add-opens", "java.base/java.io=ALL-UNNAMED" // because of https://github.com/asciidoctor/asciidoctor-gradle-plugin/issues/597 (maybe fixed in v5 of the plugin?)
            )
        }
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

    register<Tar>("documentationTar") {
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

    register("release") {
        group = "publishing"
        dependsOn(clean, build, publish, closeAndReleaseStagingRepositories, sourceforgeRelease, gitHubRelease)
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

releasing {
    jar = tasks.jar.get().archiveFile
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
                url = "https://argo.sourceforge.net"
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
                        url = "https://www.apache.org/licenses/LICENSE-2.0.txt"
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
            nexusUrl.set(uri("https://ossrh-staging-api.central.sonatype.com/service/local/"))
            snapshotRepositoryUrl.set(uri("https://central.sonatype.com/repository/maven-snapshots/"))
            username.set(project.findProperty("sonatypeCentralUser").toString())
            password.set(project.findProperty("sonatypeCentralPassword").toString())
        }
    }
}

val javaComponent = components["java"] as AdhocComponentWithVariants
javaComponent.withVariantsFromConfiguration(configurations["testFixturesApiElements"]) { skip() }
javaComponent.withVariantsFromConfiguration(configurations["testFixturesRuntimeElements"]) { skip() }
