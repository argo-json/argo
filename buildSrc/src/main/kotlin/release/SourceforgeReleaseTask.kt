/*
 *  Copyright 2026 Mark Slater
 *
 *  Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at
 *
 *  	http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.
 */

package release

import com.sshtools.client.SshClient
import com.sshtools.client.sftp.SftpClient
import com.sshtools.client.sftp.SftpClient.SftpClientBuilder
import com.sshtools.client.tasks.FileTransferProgress
import org.gradle.api.DefaultTask
import org.gradle.api.GradleException
import org.gradle.api.file.ConfigurableFileCollection
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.tasks.InputFile
import org.gradle.api.tasks.InputFiles
import org.gradle.api.tasks.TaskAction
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpRequest.BodyPublishers
import java.net.http.HttpResponse.BodyHandlers

abstract class SourceforgeReleaseTask : DefaultTask() {

    @get:InputFile
    abstract val combinedJar: RegularFileProperty

    @get:InputFile
    abstract val smallJar: RegularFileProperty

    @get:InputFiles
    abstract val documentation: ConfigurableFileCollection

    @TaskAction
    fun release() {
        if (project.version == VersionNumber.DevelopmentVersion) {
            throw GradleException("Cannot release development version")
        }
        val username = "${project.property("sourceforgeUser")}"
        val password = project.property("sourceforgePassword").toString().toCharArray()
        try {
            withSftpClient("frs.sourceforge.net", username, password) { sftpClient ->
                sftpClient.mkdirs("/home/frs/project/argo/argo/${project.version}")
                sftpClient.put(combinedJar.get().toString(), "/home/frs/project/argo/argo/${project.version}/argo-${project.version}.jar")
                sftpClient.put(smallJar.get().toString(), "/home/frs/project/argo/argo/${project.version}/argo-small-${project.version}.jar")
            }
            withSftpClient("web.sourceforge.net", username, password) { sftpClient ->
                sftpClient.putLocalDirectory(documentation.singleFile.toString(), "/home/project-web/argo/${project.version}", true, false, true, object : FileTransferProgress {})
                sftpClient.rm("/home/project-web/argo/htdocs")
                sftpClient.symlink("${project.version}","/home/project-web/argo/htdocs")
            }
        } catch (e: SshExecuteRuntimeException) {
            logger.error("Remote command execution failed", e)
            e.commandOutput?.also {
                logger.error("Command output:")
                logger.error(it)
            }
            logger.error("Full session output:")
            logger.error(e.sessionOutput)
            throw e
        }

        val defaultDownloadUri = URI.create("https://sourceforge.net/projects/argo/files/argo/${project.version}/argo-${project.version}.jar")
        val response = HttpClient.newHttpClient()
            .send(
                HttpRequest.newBuilder(defaultDownloadUri)
                    .PUT(
                        BodyPublishers.ofString(
                            "default=windows&default=mac&default=linux&default=bsd&default=solaris&default=others&download_label=${project.version}%20with%20source&api_key=${
                                project.property(
                                    "sourceforgeApiKey"
                                )
                            }"
                        )
                    )
                    .setHeader("content-type", "application/x-www-form-urlencoded")
                    .build(),
                BodyHandlers.ofString()
            )
        if (response.statusCode() !in 200 until 400) {
            throw GradleException("updating SourceForge default download to {$defaultDownloadUri} resulted in response code ${response.statusCode()} with body\n${response.body()}")
        }

    }

    private fun <T> withSftpClient(hostname: String, username: String, password: CharArray, block: (SftpClient) -> T) = retrying {
        SftpClientBuilder.create()
            .withClient(
                SshClient.SshClientBuilder.create()
                    .withHostname(hostname)
                    .withPort(22)
                    .withUsername(username)
                    .withPassword(password)
                    .build()
            )
            .build()
    }.use(block)

    private fun <T> retrying(block: () -> T) = generateSequence { runCatching(block) }
        .filterIndexed { index, result -> index >= 5 || result.isSuccess }
        .first()
        .getOrThrow()

    private class SshExecuteRuntimeException(cause: Exception, val commandOutput: String?, val sessionOutput: String) : RuntimeException(cause.message, cause)

}