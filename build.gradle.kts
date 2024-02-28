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
    id("com.github.spotbugs") version "6.0.7"
    id("io.github.gradle-nexus.publish-plugin") version "1.3.0"
    id("com.gitlab.svg2ico") version "1.2"
    id("org.asciidoctor.jvm.convert") version "4.0.2"

    id("release.sourceforge")
}

repositories {
    mavenCentral()
}

sourceSets {
    create("moduleInfo")
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
                implementation("commons-io:commons-io:2.15.1")
                runtimeOnly("org.junit.jupiter:junit-jupiter-engine:5.10.2")
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

        register<JvmTestSuite>("docs") {
            useJUnitJupiter()
            dependencies {
                implementation(project())
                implementation(testFixtures(project()))
            }
        }
    }
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

    spotbugs(group = "com.github.spotbugs", name = "spotbugs", version = "4.8.3")

    jmhImplementation(testFixtures(project))
}


group = "net.sourceforge.argo"
base.archivesName = "argo"
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

val javadocJar by tasks.registering(Jar::class) {
    archiveClassifier = "javadoc"
    from(tasks.javadoc)
}

val sourcesJar by tasks.registering(Jar::class) {
    archiveClassifier = "sources"
    from(sourceSets["main"].allSource)
}

pmd {
    toolVersion = "6.29.0"
    ruleSetFiles = files("tools/pmd-ruleset.xml")
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
//                "--add-modules", "ALL-SYSTEM",
                "-d", sourceSets["main"].output.classesDirs.asPath
            )
        }
    }

    val compileTinyJava by registering(JavaCompile::class) {
        sourceCompatibility = "1.5"
        targetCompatibility = "1.5"
        javaCompiler = project.javaToolchains.compilerFor {
            languageVersion = JavaLanguageVersion.of(8)
        }
        source = sourceSets["main"].allSource
        classpath = sourceSets["main"].compileClasspath
        destinationDirectory.set(project.layout.buildDirectory.dir("tiny-classes/main"))
        options.compilerArgs = listOf("-g:none", "-Xlint:-options")
    }

    check {
        @Suppress("UnstableApiUsage")
        dependsOn(testing.suites["codeSamples"])
    }

    jar {
        from(sourceSets["main"].output)
        from(sourceSets["moduleInfo"].output)
    }

    val combinedJar by registering(Jar::class) {
        archiveClassifier = "combined"
        from(sourceSets["main"].allSource)
        from(sourceSets["main"].output)
    }

    val tinyJar by registering(Jar::class) {
        dependsOn(compileTinyJava)
        archiveClassifier = "tiny"
        from(project.layout.buildDirectory.dir("tiny-classes/main"))
    }

    javadoc {
        title = "Argo version $version"
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
        useJUnitPlatform()
    }

    val ico by registering(com.gitlab.svg2ico.Svg2IcoTask::class) {
        source {
            sourcePath = file("resources/favicon.svg")
        }
        destination = project.layout.buildDirectory.file("icons/favicon.ico")
    }

    val png by registering(com.gitlab.svg2ico.Svg2PngTask::class) {
        source = file("resources/favicon.svg")
        width = 128
        height = 128
        destination = project.layout.buildDirectory.file("icons/favicon.png")
    }

    asciidoctor {
        dependsOn(ico, png, javadoc) // doesn't seem to infer dependencies properly from the resources CopySpec
        resources {
            from(ico, png)
            from(javadoc) {
                into("javadoc")
            }
        }
    }

    val documentationTar by registering(Tar::class) {
        from(asciidoctor)
        archiveBaseName.set("documentation")
        compression = Compression.GZIP
    }

    pmdMain {
        ruleSetFiles = files("tools/pmd-ruleset.xml", "tools/pmd-main-extra-ruleset.xml")
        ruleSets = emptyList()
    }

    spotbugsMain {
        excludeFilter = file("tools/spotbugs-main-filter.xml")
    }

    spotbugsTest {
        excludeFilter = file("tools/spotbugs-test-filter.xml")
    }

    spotbugsTestFixtures {
        excludeFilter = file("tools/spotbugs-testFixtures-filter.xml")
    }

    getByName("release") {
        dependsOn(jar, documentationTar, javadocJar, combinedJar, tinyJar)
    }

    val performRelease by registering {
        dependsOn(clean, build, "publishToSonatype", png, "closeAndReleaseStagingRepository", "release")
        doLast {
            println("Release complete :)")
        }
    }

    register("deploy") {
        dependsOn(incrementVersionNumber)
    }
}

artifacts {
    archives(javadocJar)
    archives(sourcesJar)
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
