/*
 *  Copyright 2024 Mark Slater
 *
 *  Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at
 *
 *  	http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.
 */

package release

import argo.JsonGenerator
import argo.JsonParser
import argo.jdom.JsonNodeFactories.*
import net.sourceforge.urin.Authority.authority
import net.sourceforge.urin.Host.registeredName
import net.sourceforge.urin.Path.path
import net.sourceforge.urin.scheme.http.HttpQuery.queryParameter
import net.sourceforge.urin.scheme.http.HttpQuery.queryParameters
import net.sourceforge.urin.scheme.http.Https.https
import org.gradle.api.DefaultTask
import org.gradle.api.GradleException
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.tasks.InputFile
import org.gradle.api.tasks.TaskAction
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse

abstract class GitHubReleaseTask : DefaultTask() {

    @get:InputFile
    abstract val jar: RegularFileProperty

    @get:InputFile
    abstract val combinedJar: RegularFileProperty

    @get:InputFile
    abstract val smallJar: RegularFileProperty

    @TaskAction
    fun release() {
        val gitHubToken = project.property("gitHubToken").toString()

        val releasesUri = URI("https://api.github.com/repos/argo-json/argo/releases")

        val response = HttpClient.newHttpClient()
            .send(
                HttpRequest.newBuilder(releasesUri)
                    .POST(HttpRequest.BodyPublishers.ofString(JsonGenerator().generate(`object`(
                        field("tag_name", string(project.version.toString()))
                    ))))
                    .setHeader("content-type", "application/json")
                    .setHeader("accept", "application/vnd.github+json")
                    .setHeader("authorization", "Bearer $gitHubToken")
                    .setHeader("x-github-api-version", "2022-11-28")
                    .build(),
                HttpResponse.BodyHandlers.ofString()
            )
        if (response.statusCode() != 201) {
            throw GradleException("Creating GitHub release via {$releasesUri} resulted in response code ${response.statusCode()} with body\n${response.body()}")
        } else {
            logger.info("GitHub responded with status code {}", response.statusCode())
            logger.info(response.body())
        }

        val releaseId = JsonParser().parse(response.body()).getNumberValue("id")

        listOf(
            Triple(jar, "argo-${project.version}.jar", "Jar"),
            Triple(combinedJar, "argo-with-source-${project.version}.jar", "Jar with source code included"),
            Triple(smallJar, "argo-small-${project.version}.jar", "Jar stripped of debug information"),
        ).forEach {(source, targetName, label) ->
            val uploadUri = https(
                authority(registeredName("uploads.github.com")),
                path("repos", "argo-json", "argo", "releases", releaseId, "assets"),
                queryParameters(
                    queryParameter("name", targetName),
                    queryParameter("label", label)
                )
            ).asUri()
            val uploadResponse = HttpClient.newHttpClient()
                .send(
                    HttpRequest.newBuilder(uploadUri)
                        .POST(HttpRequest.BodyPublishers.ofFile(source.get().asFile.toPath()))
                        .setHeader("content-type", "application/java-archive")
                        .setHeader("accept", "application/vnd.github+json")
                        .setHeader("authorization", "Bearer $gitHubToken")
                        .setHeader("x-github-api-version", "2022-11-28")
                        .build(),
                    HttpResponse.BodyHandlers.ofString()
                )
            if (uploadResponse.statusCode() != 201) {
                throw GradleException("Adding jar to GitHub release via {$uploadUri} resulted in response code ${uploadResponse.statusCode()} with body\n${uploadResponse.body()}")
            } else {
                logger.info("GitHub responded with status code {}", uploadResponse.statusCode())
                logger.info(uploadResponse.body())
            }

        }
    }

}