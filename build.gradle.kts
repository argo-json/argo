/*
 *  Copyright 2024 Mark Slater
 *
 *  Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at
 *
 *  	http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.
 */

import com.gitlab.svg2ico.Svg2IcoTask
import com.gitlab.svg2ico.Svg2PngTask
import java.util.*

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
    id("com.github.spotbugs") version "5.0.14"
    id("io.github.gradle-nexus.publish-plugin") version "1.3.0"
    id("com.gitlab.svg2ico") version "0.14"

    id("release.sourceforge")
}

repositories {
    mavenCentral()
}

val documentationDirectory = project.layout.buildDirectory.dir("documentation")

sourceSets {
    create("moduleInfo")
    create("documentation")
}

testing {
    @Suppress("UnstableApiUsage")
    suites {
        val test by getting(JvmTestSuite::class) {
            useJUnitJupiter()
            dependencies {
                implementation("org.junit.jupiter:junit-jupiter-api:5.9.3")
                implementation("org.junit.jupiter:junit-jupiter-params:5.9.3")
                implementation("org.hamcrest:hamcrest:2.2")
                implementation("net.sourceforge.ickles:ickles:0.21")
                implementation("commons-io:commons-io:2.15.1")
                runtimeOnly("org.junit.jupiter:junit-jupiter-engine:5.9.3")
            }
        }

        register<JvmTestSuite>("codeSamples") {
            useJUnitJupiter()
            dependencies {
                implementation(project())
                implementation(testFixtures(project()))
                implementation("commons-io:commons-io:2.15.1")
                implementation("org.hamcrest:hamcrest:2.2")
            }
        }
    }
}

tasks.named("check") {
    @Suppress("UnstableApiUsage")
    dependsOn(testing.suites["codeSamples"])
}

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(8)
    }
}

dependencies {
    "moduleInfoImplementation"(sourceSets["main"].output)

    testFixturesImplementation(group = "org.apache.commons", name = "commons-lang3", version = "3.12.0")
    testFixturesImplementation(group = "commons-io", name = "commons-io", version = "2.15.1")
    testFixturesImplementation(group = "net.sourceforge.ickles", name = "ickles", version = "0.21")
    testFixturesImplementation(group = "org.hamcrest", name = "hamcrest", version = "2.2")

    "documentationImplementation"(sourceSets["main"].output)
    "documentationImplementation"(group = "net.sourceforge.urin", name = "urin", version = "4.1")
    "documentationImplementation"(group = "net.sourceforge.writexml", name = "writexml", version = "1.3")
    "documentationImplementation"(group = "net.sourceforge.xazzle", name = "xazzle", version = "0.39")

    spotbugs(group = "com.github.spotbugs", name = "spotbugs", version = "4.8.3")

    jmhImplementation(testFixtures(project))
}


group = "net.sourceforge.argo"
base.archivesName = "argo"
version = Properties().apply {
    file("version.properties").reader().use {
        load(it)
    }
}.let {
    "${it.getProperty("majorVersion")}.${it.getProperty("minorVersion")}"
}
description = "Argo is an open source JSON parser and generator written in Java.  It offers document, push, and pull APIs."

idea {
    project {
        jdkName = "1.8"
        languageLevel.level = "1.5"
    }
    module {
        jdkName = "11"
    }
}

tasks.compileJava {
    sourceCompatibility = "1.5"
    targetCompatibility = "1.5"
    options.compilerArgs.add("-Xlint:-options")
}

val compileTinyJava by tasks.registering(JavaCompile::class) {
    sourceCompatibility = "1.5"
    targetCompatibility = "1.5"
    javaCompiler = javaToolchains.compilerFor {
        languageVersion = JavaLanguageVersion.of(8)
    }
    source = sourceSets["main"].allSource
    classpath = sourceSets["main"].compileClasspath
    destinationDirectory.set(project.layout.buildDirectory.dir("tiny-classes/main"))
    options.compilerArgs = listOf("-g:none", "-Xlint:-options")
}

tasks.named<JavaCompile>("compileModuleInfoJava") {
    sourceCompatibility = "9"
    targetCompatibility = "9"
    javaCompiler = javaToolchains.compilerFor {
        languageVersion = JavaLanguageVersion.of(9)
    }
    doFirst {
        classpath += sourceSets["main"].compileClasspath

        options.compilerArgs = listOf(
                "--module-path", classpath.asPath,
//                "--add-modules", "ALL-SYSTEM",
                "-d", sourceSets["main"].output.classesDirs.asPath
        )
    }
}

val myJavadoc by tasks.registering(Javadoc::class) {
    source = sourceSets["main"].allJava
    title = "Argo version $version"
}

val javadocJar by tasks.registering(Jar::class) {
    archiveClassifier = "javadoc"
    from(myJavadoc)
}

val tinyJar by tasks.registering(Jar::class) {
    dependsOn(compileTinyJava)
    archiveClassifier = "tiny"
    from(project.layout.buildDirectory.dir("tiny-classes/main"))
}

val sourcesJar by tasks.registering(Jar::class) {
    archiveClassifier = "sources"
    from(sourceSets["main"].allSource)
}

val combinedJar by tasks.registering(Jar::class) {
    archiveClassifier = "combined"
    from(sourceSets["main"].allSource)
    from(sourceSets["main"].output)
}

tasks.jar {
    from(sourceSets["main"].output)
    from(sourceSets["moduleInfo"].output)
}

pmd {
    toolVersion = "6.29.0"
    ruleSetFiles = files("tools/pmd-ruleset.xml")
    ruleSets = emptyList()
}

tasks.pmdMain {
    ruleSetFiles = files("tools/pmd-ruleset.xml", "tools/pmd-main-extra-ruleset.xml")
    ruleSets = emptyList()
}

tasks.spotbugsMain {
    excludeFilter = file("tools/spotbugs-main-filter.xml")
}

tasks.spotbugsTest {
    excludeFilter = file("tools/spotbugs-test-filter.xml")
}

tasks.spotbugsTestFixtures {
    excludeFilter = file("tools/spotbugs-testFixtures-filter.xml")
}

artifacts {
    archives(javadocJar)
    archives(sourcesJar)
}

val ico by tasks.registering(Svg2IcoTask::class) {
    destination = documentationDirectory.get().file("favicon.ico").asFile
    input.source = file("resources/favicon.svg")
    input.width = 32
    input.height = 32
}

val png by tasks.registering(Svg2PngTask::class) {
    source = file("resources/favicon.svg")
    destination = project.layout.buildDirectory.file("icons/favicon.png").get().asFile
    width = 128
    height = 128
}


val buildDocumentation by tasks.registering(JavaExec::class) {
    outputs.dir(documentationDirectory)
    mainClass = "documentation.DocumentationGenerator"
    args(documentationDirectory.get().toString())
    classpath = sourceSets["documentation"].runtimeClasspath
}

val documentationJar by tasks.registering(Tar::class) {
    dependsOn(buildDocumentation, ico)
    from(documentationDirectory)
    from("docs")
    archiveBaseName.set("documentation")
    compression = Compression.GZIP
}

tasks.getByName("release") {
    dependsOn(tasks.jar, documentationJar, javadocJar, combinedJar, tinyJar)
}

publishing {
    publications {
        create<MavenPublication>("mavenJava") {
            artifact(sourcesJar)
            artifact(javadocJar)
            from(components["java"])
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

val performRelease by tasks.registering {
    dependsOn(tasks.clean, tasks.build, "publishToSonatype", png, "closeAndReleaseStagingRepository", "release")
    doLast {
        println("Release complete :)")
    }
}

val incrementVersionNumber by tasks.registering {
    dependsOn(performRelease)
    doLast {
        Properties().apply {
            load(file("version.properties").reader())
            setProperty("minorVersion", (getProperty("minorVersion").toInt() + 1).toString())
            file("version.properties").writer().use {
                store(it, null)
            }
        }
    }
}

tasks.register("deploy") {
    dependsOn(incrementVersionNumber)
}