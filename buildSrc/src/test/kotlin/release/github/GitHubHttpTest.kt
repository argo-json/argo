/*
 *  Copyright 2024 Mark Slater
 *
 *  Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at
 *
 *  	http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.
 */

package release.github

import argo.InvalidSyntaxException
import argo.JsonParser
import argo.jdom.JsonNodeFactories.*
import com.sun.net.httpserver.HttpHandler
import io.kotest.assertions.assertSoftly
import io.kotest.inspectors.forAll
import io.kotest.inspectors.forOne
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.collections.shouldBeSingleton
import io.kotest.matchers.collections.shouldContain
import io.kotest.matchers.collections.shouldExistInOrder
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldBeEqualIgnoringCase
import io.kotest.matchers.types.shouldBeInstanceOf
import io.kotest.matchers.types.shouldNotBeInstanceOf
import net.sourceforge.urin.Authority
import net.sourceforge.urin.Authority.authority
import net.sourceforge.urin.Host.registeredName
import net.sourceforge.urin.Path.path
import net.sourceforge.urin.scheme.http.HttpQuery.queryParameter
import net.sourceforge.urin.scheme.http.HttpQuery.queryParameters
import net.sourceforge.urin.scheme.http.Https.https
import org.junit.jupiter.api.DynamicContainer.dynamicContainer
import org.junit.jupiter.api.DynamicNode
import org.junit.jupiter.api.DynamicTest
import org.junit.jupiter.api.DynamicTest.dynamicTest
import org.junit.jupiter.api.TestFactory
import release.VersionNumber
import release.github.ConnectionRefusingServer.Companion.connectionRefusingServer
import release.github.FakeHttpServer.Companion.fakeHttpServer
import release.github.GitHub.ReleaseVersionOutcome
import release.github.GitHubHttp.AuditEvent.RequestCompleted
import release.github.GitHubHttp.AuditEvent.RequestFailed
import release.github.GitHubHttp.GitHubApiAuthority
import release.github.GitHubHttp.GitHubUploadAuthority
import release.github.GitHubHttpTest.FakeServers.Companion.fakeServers
import release.github.PrivilegedGitHub.*
import release.pki.PkiTestingFactories.Companion.aPublicKeyInfrastructure
import release.pki.PkiTestingFactories.PublicKeyInfrastructure
import release.pki.ReleaseTrustStore
import release.utilities.withTemporaryFile
import release.utilities.withTimeout
import java.io.IOException
import java.io.OutputStream.nullOutputStream
import java.net.URI
import java.net.http.HttpConnectTimeoutException
import java.net.http.HttpTimeoutException
import java.nio.charset.StandardCharsets.UTF_8
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeoutException
import javax.net.ssl.KeyManager
import kotlin.random.Random
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Duration.Companion.seconds

class GitHubHttpTest {

    companion object {
        private val publicKeyInfrastructure = aPublicKeyInfrastructure()
    }

    private object LatestReleaseVersionTestSuite : TestSuite<ReleaseVersionOutcome>("latest release version", publicKeyInfrastructure) {
        override val executor = { gitHubHttp: GitHubHttp, _: GitHubUploadAuthority -> gitHubHttp.latestReleaseVersion() }
        override val validResponseCode = 200

        @Suppress("SpellCheckingInspection")
        override val sunnyDayResponse =
            """[{"url":"https://api.github.com/repos/argo-json/argo/releases/151719838","assets_url":"https://api.github.com/repos/argo-json/argo/releases/151719838/assets","upload_url":"https://uploads.github.com/repos/argo-json/argo/releases/151719838/assets{?name,label}","html_url":"https://github.com/argo-json/argo/releases/tag/7.4","id":151719838,"author":{"login":"markslater","id":642523,"node_id":"MDQ6VXNlcjY0MjUyMw==","avatar_url":"https://avatars.githubusercontent.com/u/642523?v=4","gravatar_id":"","url":"https://api.github.com/users/markslater","html_url":"https://github.com/markslater","followers_url":"https://api.github.com/users/markslater/followers","following_url":"https://api.github.com/users/markslater/following{/other_user}","gists_url":"https://api.github.com/users/markslater/gists{/gist_id}","starred_url":"https://api.github.com/users/markslater/starred{/owner}{/repo}","subscriptions_url":"https://api.github.com/users/markslater/subscriptions","organizations_url":"https://api.github.com/users/markslater/orgs","repos_url":"https://api.github.com/users/markslater/repos","events_url":"https://api.github.com/users/markslater/events{/privacy}","received_events_url":"https://api.github.com/users/markslater/received_events","type":"User","site_admin":false},"node_id":"RE_kwDOLP1Xis4JCw-e","tag_name":"7.4","target_commitish":"main","name":null,"draft":false,"prerelease":false,"created_at":"2024-04-18T10:57:20Z","published_at":"2024-04-18T11:09:28Z","assets":[{"url":"https://api.github.com/repos/argo-json/argo/releases/assets/162844411","id":162844411,"node_id":"RA_kwDOLP1Xis4JtM77","name":"argo-7.4.jar","label":"Jar","uploader":{"login":"markslater","id":642523,"node_id":"MDQ6VXNlcjY0MjUyMw==","avatar_url":"https://avatars.githubusercontent.com/u/642523?v=4","gravatar_id":"","url":"https://api.github.com/users/markslater","html_url":"https://github.com/markslater","followers_url":"https://api.github.com/users/markslater/followers","following_url":"https://api.github.com/users/markslater/following{/other_user}","gists_url":"https://api.github.com/users/markslater/gists{/gist_id}","starred_url":"https://api.github.com/users/markslater/starred{/owner}{/repo}","subscriptions_url":"https://api.github.com/users/markslater/subscriptions","organizations_url":"https://api.github.com/users/markslater/orgs","repos_url":"https://api.github.com/users/markslater/repos","events_url":"https://api.github.com/users/markslater/events{/privacy}","received_events_url":"https://api.github.com/users/markslater/received_events","type":"User","site_admin":false},"content_type":"application/java-archive","state":"uploaded","size":145634,"download_count":1,"created_at":"2024-04-18T11:09:28Z","updated_at":"2024-04-18T11:09:29Z","browser_download_url":"https://github.com/argo-json/argo/releases/download/7.4/argo-7.4.jar"},{"url":"https://api.github.com/repos/argo-json/argo/releases/assets/162844416","id":162844416,"node_id":"RA_kwDOLP1Xis4JtM8A","name":"argo-small-7.4.jar","label":"Jar stripped of debug information","uploader":{"login":"markslater","id":642523,"node_id":"MDQ6VXNlcjY0MjUyMw==","avatar_url":"https://avatars.githubusercontent.com/u/642523?v=4","gravatar_id":"","url":"https://api.github.com/users/markslater","html_url":"https://github.com/markslater","followers_url":"https://api.github.com/users/markslater/followers","following_url":"https://api.github.com/users/markslater/following{/other_user}","gists_url":"https://api.github.com/users/markslater/gists{/gist_id}","starred_url":"https://api.github.com/users/markslater/starred{/owner}{/repo}","subscriptions_url":"https://api.github.com/users/markslater/subscriptions","organizations_url":"https://api.github.com/users/markslater/orgs","repos_url":"https://api.github.com/users/markslater/repos","events_url":"https://api.github.com/users/markslater/events{/privacy}","received_events_url":"https://api.github.com/users/markslater/received_events","type":"User","site_admin":false},"content_type":"application/java-archive","state":"uploaded","size":117267,"download_count":0,"created_at":"2024-04-18T11:09:30Z","updated_at":"2024-04-18T11:09:30Z","browser_download_url":"https://github.com/argo-json/argo/releases/download/7.4/argo-small-7.4.jar"},{"url":"https://api.github.com/repos/argo-json/argo/releases/assets/162844412","id":162844412,"node_id":"RA_kwDOLP1Xis4JtM78","name":"argo-with-source-7.4.jar","label":"Jar with source code included","uploader":{"login":"markslater","id":642523,"node_id":"MDQ6VXNlcjY0MjUyMw==","avatar_url":"https://avatars.githubusercontent.com/u/642523?v=4","gravatar_id":"","url":"https://api.github.com/users/markslater","html_url":"https://github.com/markslater","followers_url":"https://api.github.com/users/markslater/followers","following_url":"https://api.github.com/users/markslater/following{/other_user}","gists_url":"https://api.github.com/users/markslater/gists{/gist_id}","starred_url":"https://api.github.com/users/markslater/starred{/owner}{/repo}","subscriptions_url":"https://api.github.com/users/markslater/subscriptions","organizations_url":"https://api.github.com/users/markslater/orgs","repos_url":"https://api.github.com/users/markslater/repos","events_url":"https://api.github.com/users/markslater/events{/privacy}","received_events_url":"https://api.github.com/users/markslater/received_events","type":"User","site_admin":false},"content_type":"application/java-archive","state":"uploaded","size":225872,"download_count":0,"created_at":"2024-04-18T11:09:29Z","updated_at":"2024-04-18T11:09:30Z","browser_download_url":"https://github.com/argo-json/argo/releases/download/7.4/argo-with-source-7.4.jar"}],"tarball_url":"https://api.github.com/repos/argo-json/argo/tarball/7.4","zipball_url":"https://api.github.com/repos/argo-json/argo/zipball/7.4","body":null}]"""
        override val sunnyDayAssertion: (outcome: ReleaseVersionOutcome) -> Unit =
            { it.shouldBeInstanceOf<ReleaseVersionOutcome.Success>().versionNumber shouldBe VersionNumber.ReleaseVersion.of(7, 4) }
        override val expectedUri: (apiAuthority: Authority, uploadAuthority: Authority) -> URI = { apiAuthority, _ ->
            https(
                apiAuthority,
                path("repos", "argo-json", "argo", "releases"),
                queryParameters(queryParameter("per_page", "1"))
            ).asUri()
        }
        override val apiRequestBodiesAssertions: (requestBodies: List<ByteArray>) -> Unit =
            { requestBodies -> requestBodies.shouldBeSingleton { it shouldBe byteArrayOf() } }
        override val failureOutcomeAssertions: (outcome: ReleaseVersionOutcome) -> Failure =
            { outcome -> outcome.shouldBeInstanceOf<ReleaseVersionOutcome.Failure>().failure }
        override val supplementaryTests = listOf(handlesUnexpectedlyShapedJsonResponse(), handlesNonJsonResponse())
    }

    private object CreateReleaseTestSuite : TestSuite<ReleaseOutcome>("create release", publicKeyInfrastructure) {
        private const val GIT_HUB_TOKEN = "MY_TOKEN"
        private val versionNumber = VersionNumber.ReleaseVersion.of(1, 82)
        override val executor = { gitHubHttp: GitHubHttp, uploadAuthority: GitHubUploadAuthority ->
            gitHubHttp
                .privileged(uploadAuthority, GitHubHttp.GitHubToken(GIT_HUB_TOKEN))
                .release(versionNumber)
        }
        override val validResponseCode = 201

        @Suppress("SpellCheckingInspection")
        override val sunnyDayResponse =
            """{"url":"https://api.github.com/repos/argo-json/argo/releases/155311213","assets_url":"https://api.github.com/repos/argo-json/argo/releases/155311213/assets","upload_url":"https://uploads.github.com/repos/argo-json/argo/releases/155311213/assets{?name,label}","html_url":"https://github.com/argo-json/argo/releases/tag/4.17","id":155311213,"author":{"login":"markslater","id":642523,"node_id":"MDQ6VXNlcjY0MjUyMw==","avatar_url":"https://avatars.githubusercontent.com/u/642523?v=4","gravatar_id":"","url":"https://api.github.com/users/markslater","html_url":"https://github.com/markslater","followers_url":"https://api.github.com/users/markslater/followers","following_url":"https://api.github.com/users/markslater/following{/other_user}","gists_url":"https://api.github.com/users/markslater/gists{/gist_id}","starred_url":"https://api.github.com/users/markslater/starred{/owner}{/repo}","subscriptions_url":"https://api.github.com/users/markslater/subscriptions","organizations_url":"https://api.github.com/users/markslater/orgs","repos_url":"https://api.github.com/users/markslater/repos","events_url":"https://api.github.com/users/markslater/events{/privacy}","received_events_url":"https://api.github.com/users/markslater/received_events","type":"User","site_admin":false},"node_id":"RE_kwDOLzFRO84JQdxt","tag_name":"4.17","target_commitish":"main","name":null,"draft":false,"prerelease":false,"created_at":"2024-05-10T19:18:06Z","published_at":"2024-05-10T19:28:06Z","assets":[],"tarball_url":"https://api.github.com/repos/argo-json/argo/tarball/4.17","zipball_url":"https://api.github.com/repos/argo-json/argo/zipball/4.17","body":null}"""
        override val sunnyDayAssertion: (outcome: ReleaseOutcome) -> Unit =
            { it.shouldBeInstanceOf<ReleaseOutcome.Success>().releaseId shouldBe ReleaseId("155311213") }
        override val expectedUri: (apiAuthority: Authority, uploadAuthority: Authority) -> URI = { apiAuthority, _ ->
            https(
                apiAuthority,
                path("repos", "argo-json", "argo", "releases")
            ).asUri()
        }
        override val supplementaryRequestHeaderAssertions: (requestHeaders: List<Pair<String, String>>) -> Unit = { requestHeaders ->
            requestHeaders
                .forOne { (key, value) ->
                    key shouldBeEqualIgnoringCase "authorization"
                    value shouldBe "Bearer $GIT_HUB_TOKEN"
                }
                .forOne { (key, value) ->
                    key shouldBeEqualIgnoringCase "content-type"
                    value shouldBe "application/json"
                }
        }
        override val apiRequestBodiesAssertions: (requestBodies: List<ByteArray>) -> Unit = { requestBodies ->
            requestBodies.shouldBeSingleton {
                JsonParser().parse(it.toString(UTF_8)) shouldBe `object`(
                    field(
                        "tag_name",
                        string(versionNumber.toString())
                    )
                )
            }
        }
        override val failureOutcomeAssertions: (outcome: ReleaseOutcome) -> Failure =
            { outcome -> outcome.shouldBeInstanceOf<ReleaseOutcome.Failure>().failure }
        override val supplementaryTests = listOf(handlesUnexpectedlyShapedJsonResponse(), handlesNonJsonResponse())
    }

    private object UploadArtifactTestSuite : TestSuite<UploadArtifactOutcome>("upload artifact", publicKeyInfrastructure) {
        private const val GIT_HUB_TOKEN = "MY_TOKEN"
        private const val RELEASE_ID = "152871162"
        private const val TARGET_NAME = "my.jar"
        private const val LABEL = "Best Jar!"
        private val fileContents = Random.Default.nextBytes(1024)
        override val executor = { gitHubHttp: GitHubHttp, uploadAuthority: GitHubUploadAuthority ->
            withTemporaryFile(fileContents) { file ->
                gitHubHttp
                    .privileged(uploadAuthority, GitHubHttp.GitHubToken(GIT_HUB_TOKEN))
                    .uploadArtifact(ReleaseId(RELEASE_ID), TARGET_NAME, LABEL, file)
            }
        }
        override val validResponseCode = 201

        @Suppress("SpellCheckingInspection")
        override val sunnyDayResponse =
            """{"url":"https://api.github.com/repos/argo-json/argo/releases/assets/167220330","id":167220330,"node_id":"RA_kwDOLzFRO84J95Rq","name":"argo-with-source-4.17.jar","label":"Jar with source code included","uploader":{"login":"markslater","id":642523,"node_id":"MDQ6VXNlcjY0MjUyMw==","avatar_url":"https://avatars.githubusercontent.com/u/642523?v=4","gravatar_id":"","url":"https://api.github.com/users/markslater","html_url":"https://github.com/markslater","followers_url":"https://api.github.com/users/markslater/followers","following_url":"https://api.github.com/users/markslater/following{/other_user}","gists_url":"https://api.github.com/users/markslater/gists{/gist_id}","starred_url":"https://api.github.com/users/markslater/starred{/owner}{/repo}","subscriptions_url":"https://api.github.com/users/markslater/subscriptions","organizations_url":"https://api.github.com/users/markslater/orgs","repos_url":"https://api.github.com/users/markslater/repos","events_url":"https://api.github.com/users/markslater/events{/privacy}","received_events_url":"https://api.github.com/users/markslater/received_events","type":"User","site_admin":false},"content_type":"application/java-archive","state":"uploaded","size":219250,"download_count":0,"created_at":"2024-05-10T19:28:07Z","updated_at":"2024-05-10T19:28:08Z","browser_download_url":"https://github.com/argo-json/argo/releases/download/4.17/argo-with-source-4.17.jar"}"""
        override val sunnyDayAssertion: (outcome: UploadArtifactOutcome) -> Unit = { it.shouldBeInstanceOf<UploadArtifactOutcome.Success>() }
        override val expectedUri: (apiAuthority: Authority, uploadAuthority: Authority) -> URI = { _, uploadAuthority ->
            https(
                uploadAuthority,
                path("repos", "argo-json", "argo", "releases", RELEASE_ID, "assets"),
                queryParameters(
                    queryParameter("name", TARGET_NAME),
                    queryParameter("label", LABEL),
                )
            ).asUri()

        }
        override val supplementaryRequestHeaderAssertions: (requestHeaders: List<Pair<String, String>>) -> Unit = { requestHeaders ->
            requestHeaders
                .forOne { (key, value) ->
                    key shouldBeEqualIgnoringCase "authorization"
                    value shouldBe "Bearer $GIT_HUB_TOKEN"
                }
                .forOne { (key, value) ->
                    key shouldBeEqualIgnoringCase "content-length"
                    value shouldBe "${fileContents.size}"
                }
                .forOne { (key, value) ->
                    key shouldBeEqualIgnoringCase "content-type"
                    value shouldBe "application/java-archive"
                }
        }
        override val uploadRequestBodiesAssertions: (requestBodies: List<ByteArray>) -> Unit =
            { requestBodies -> requestBodies.shouldBeSingleton { it shouldBe fileContents } }
        override val failureOutcomeAssertions: (outcome: UploadArtifactOutcome) -> Failure =
            { outcome -> outcome.shouldBeInstanceOf<UploadArtifactOutcome.Failure>().failure }
        override val supplementaryTests = listOf(handlesTimeoutDuringSlowRequestReceipt())
    }

    @TestFactory
    fun `test suites`(): List<DynamicNode> {
        return listOf(
            LatestReleaseVersionTestSuite,
            CreateReleaseTestSuite,
            UploadArtifactTestSuite,
        ).map { it.toDynamicNode() }
    }

    private abstract class TestSuite<OUTCOME>(val name: String, val publicKeyInfrastructure: PublicKeyInfrastructure) {
        abstract val executor: (GitHubHttp, GitHubUploadAuthority) -> OUTCOME
        abstract val validResponseCode: Int
        abstract val sunnyDayResponse: String
        abstract val sunnyDayAssertion: (outcome: OUTCOME) -> Unit
        abstract val expectedUri: (apiAuthority: Authority, uploadAuthority: Authority) -> URI
        open val supplementaryRequestHeaderAssertions: (requestHeaders: List<Pair<String, String>>) -> Unit = {}
        open val apiRequestBodiesAssertions: (requestBodies: List<ByteArray>) -> Unit = { it.shouldBeEmpty() }
        open val uploadRequestBodiesAssertions: (requestBodies: List<ByteArray>) -> Unit = { it.shouldBeEmpty() }
        abstract val failureOutcomeAssertions: (outcome: OUTCOME) -> Failure
        open val supplementaryTests: List<DynamicTest> = emptyList()

        private fun sunnyDay(): DynamicTest = dynamicTest("sunny day") {
            val responseBodyBytes = sunnyDayResponse.toByteArray(UTF_8)
            val apiRequestBodies = mutableListOf<ByteArray>()
            val uploadRequestBodies = mutableListOf<ByteArray>()
            fakeServers(publicKeyInfrastructure.keyManagers, { exchange ->
                exchange.requestBody.use { requestBody ->
                    apiRequestBodies.add(requestBody.readBytes())
                }
                exchange.sendResponseHeaders(validResponseCode, responseBodyBytes.size.toLong())
                exchange.responseBody.use { it.write(responseBodyBytes) }
            }, { exchange ->
                exchange.requestBody.use { requestBody ->
                    uploadRequestBodies.add(requestBody.readBytes())
                }
                exchange.sendResponseHeaders(validResponseCode, responseBodyBytes.size.toLong())
                exchange.responseBody.use { it.write(responseBodyBytes) }
            }).use { fakeServers ->
                val recordingAuditor = RecordingAuditor<GitHubHttp.AuditEvent>()
                val outcome = executor(
                    GitHubHttp(GitHubApiAuthority(fakeServers.apiServerAuthority), publicKeyInfrastructure.releaseTrustStore, recordingAuditor),
                    GitHubUploadAuthority(fakeServers.uploadServerAuthority)
                )
                sunnyDayAssertion(outcome)
                recordingAuditor.auditEvents().shouldBeSingleton().forAll { element ->
                    element.shouldBeInstanceOf<RequestCompleted>().also {
                        assertSoftly(element) {
                            it.uri shouldBe expectedUri(fakeServers.apiServerAuthority, fakeServers.uploadServerAuthority)
                            it.statusCode shouldBe validResponseCode
                            it.headers.shouldContain("content-length" to responseBodyBytes.size.toString())
                            it.responseBody shouldBe responseBody
                        }
                    }
                }
                apiRequestBodiesAssertions(apiRequestBodies)
                uploadRequestBodiesAssertions(uploadRequestBodies)
            }
        }

        private fun setsRequestHeaders(): DynamicTest = dynamicTest("sets request headers") {
            val responseBodyBytes = sunnyDayResponse.toByteArray(UTF_8)
            val receivedRequestHeaders = mutableListOf<Pair<String, String>>()
            fakeServers(publicKeyInfrastructure.keyManagers) { exchange ->
                exchange.requestHeaders.forEach { entry ->
                    entry.value.forEach { value ->
                        receivedRequestHeaders.add(entry.key to value)
                    }
                }
                exchange.sendResponseHeaders(validResponseCode, responseBodyBytes.size.toLong())
                exchange.responseBody.use { it.write(responseBodyBytes) }
            }.use { fakeServers ->
                executor(
                    GitHubHttp(GitHubApiAuthority(fakeServers.apiServerAuthority), publicKeyInfrastructure.releaseTrustStore, {}),
                    GitHubUploadAuthority(fakeServers.uploadServerAuthority)
                )
                receivedRequestHeaders
                    .forOne { (key, value) ->
                        key shouldBeEqualIgnoringCase "x-github-api-version"
                        value shouldBe "2022-11-28"
                    }
                    .forOne { (key, value) ->
                        key shouldBeEqualIgnoringCase "accept"
                        value shouldBe "application/vnd.github+json"
                    }
                    .forOne { (key, value) ->
                        key shouldBeEqualIgnoringCase "user-agent"
                        value shouldBe "argo-build"
                    }
                supplementaryRequestHeaderAssertions(receivedRequestHeaders)
            }
        }

        private fun handlesUnexpectedResponseCode(): DynamicTest = dynamicTest("handles unexpected response code") {
            val responseCode = 403
            val responseBody = """"you're not allowed to see this""""
            val responseBodyBytes = responseBody.toByteArray(UTF_8)
            fakeServers(publicKeyInfrastructure.keyManagers) { exchange ->
                exchange.sendResponseHeaders(responseCode, responseBodyBytes.size.toLong())
                exchange.responseBody.use { it.write(responseBodyBytes) }
            }.use { fakeServers ->
                val recordingAuditor = RecordingAuditor<GitHubHttp.AuditEvent>()
                val outcome = executor(
                    GitHubHttp(GitHubApiAuthority(fakeServers.apiServerAuthority), publicKeyInfrastructure.releaseTrustStore, recordingAuditor),
                    GitHubUploadAuthority(fakeServers.uploadServerAuthority)
                )

                val expectedRequestUri = expectedUri(fakeServers.apiServerAuthority, fakeServers.uploadServerAuthority)

                failureOutcomeAssertions(outcome).shouldBeInstanceOf<Failure.InvalidResponseCode>()
                    .also { failure ->
                        assertSoftly(failure) {
                            it.uri shouldBe expectedRequestUri
                            it.expectedResponseCode shouldBe expectedResponseCode
                            it.responseCode shouldBe responseCode
                            it.responseHeaders.shouldContain("content-length" to responseBodyBytes.size.toString())
                            it.responseBody shouldBe responseBody
                        }
                    }
                recordingAuditor.auditEvents().shouldBeSingleton().forAll { element ->
                    element.shouldBeInstanceOf<RequestCompleted>().also {
                        assertSoftly(element) {
                            it.uri shouldBe expectedRequestUri
                            it.statusCode shouldBe responseCode
                            it.headers.shouldContain("content-length" to responseBodyBytes.size.toString())
                            it.responseBody shouldBe responseBody
                        }
                    }
                }
            }
        }

        private fun handlesIoExceptionProcessingResponse(): DynamicTest = dynamicTest("handles IOException processing response") {
            val responseBody = """"something short""""
            fakeServers(publicKeyInfrastructure.keyManagers) { exchange ->
                exchange.sendResponseHeaders(validResponseCode, 1024)
                exchange.responseBody.use { it.write(responseBody.toByteArray(UTF_8)) }
            }.use { fakeServers ->
                val recordingAuditor = RecordingAuditor<GitHubHttp.AuditEvent>()
                val outcome = executor(
                    GitHubHttp(GitHubApiAuthority(fakeServers.apiServerAuthority), publicKeyInfrastructure.releaseTrustStore, recordingAuditor),
                    GitHubUploadAuthority(fakeServers.uploadServerAuthority)
                )

                val expectedRequestUri = expectedUri(fakeServers.apiServerAuthority, fakeServers.uploadServerAuthority)

                failureOutcomeAssertions(outcome).shouldBeInstanceOf<Failure.RequestSubmittingException>()
                    .also { failure ->
                        assertSoftly(failure) {
                            it.uri shouldBe expectedRequestUri
                            it.exception.shouldBeInstanceOf<IOException>()
                        }
                    }
                recordingAuditor.auditEvents().shouldExistInOrder(
                    { it is RequestFailed && it.uri == expectedRequestUri && it.cause is IOException }
                )
            }
        }

        private fun handlesUnresolvableAddress(): DynamicTest = dynamicTest("handles unresolvable address") {
            val authority = authority(registeredName("something.invalid"))
            val recordingAuditor = RecordingAuditor<GitHubHttp.AuditEvent>()
            val outcome = executor(
                GitHubHttp(GitHubApiAuthority(authority), publicKeyInfrastructure.releaseTrustStore, recordingAuditor),
                GitHubUploadAuthority(authority)
            )

            val expectedRequestUri = expectedUri(authority, authority)

            failureOutcomeAssertions(outcome).shouldBeInstanceOf<Failure.RequestSubmittingException>()
                .also { failure ->
                    assertSoftly(failure) {
                        it.uri shouldBe expectedRequestUri
                        it.exception.shouldBeInstanceOf<IOException>()
                    }
                }
            recordingAuditor.auditEvents().shouldExistInOrder(
                { it is RequestFailed && it.uri == expectedRequestUri && it.cause is IOException }
            )
        }

        private fun handlesSslFailure() = dynamicTest("handles ssl failure") {
            val responseBodyBytes = sunnyDayResponse.toByteArray(UTF_8)
            fakeServers(publicKeyInfrastructure.keyManagers) { exchange ->
                exchange.sendResponseHeaders(validResponseCode, responseBodyBytes.size.toLong())
                exchange.responseBody.use { it.write(responseBodyBytes) }
            }.use { fakeServers ->
                val recordingAuditor = RecordingAuditor<GitHubHttp.AuditEvent>()
                val outcome = executor(
                    GitHubHttp(GitHubApiAuthority(fakeServers.apiServerAuthority), ReleaseTrustStore(emptyList()), recordingAuditor),
                    GitHubUploadAuthority(fakeServers.uploadServerAuthority)
                )

                val expectedRequestUri = expectedUri(fakeServers.apiServerAuthority, fakeServers.uploadServerAuthority)

                failureOutcomeAssertions(outcome).shouldBeInstanceOf<Failure.RequestSubmittingException>()
                    .also { failure ->
                        assertSoftly(failure) {
                            it.uri shouldBe expectedRequestUri
                            it.exception.shouldBeInstanceOf<IOException>()
                        }
                    }
                recordingAuditor.auditEvents().shouldExistInOrder(
                    { it is RequestFailed && it.uri == expectedRequestUri && it.cause is IOException }
                )
            }
        }

        private fun handlesConnectTimeout() = dynamicTest("handles connect timeout") {
            withTimeout(2.seconds) {
                connectionRefusingServer(publicKeyInfrastructure).use { connectionRefusingServer ->
                    val recordingAuditor = RecordingAuditor<GitHubHttp.AuditEvent>()
                    val connectTimeout = 100.milliseconds
                    val outcome = executor(
                        GitHubHttp(
                            GitHubApiAuthority(connectionRefusingServer.authority),
                            publicKeyInfrastructure.releaseTrustStore,
                            recordingAuditor,
                            connectTimeout = connectTimeout,
                            firstByteTimeout = 10.seconds,
                            endToEndTimeout = 10.seconds,
                        ),
                        GitHubUploadAuthority(connectionRefusingServer.authority)
                    )
                    val expectedRequestUri = expectedUri(connectionRefusingServer.authority, connectionRefusingServer.authority)

                    failureOutcomeAssertions(outcome).shouldBeInstanceOf<Failure.ConnectTimeout>()
                        .also { failure ->
                            assertSoftly(failure) {
                                it.uri shouldBe expectedRequestUri
                                it.connectTimeout shouldBe connectTimeout
                                it.exception.shouldBeInstanceOf<HttpConnectTimeoutException>()
                            }
                        }
                    recordingAuditor.auditEvents().shouldExistInOrder(
                        { it is RequestFailed && it.uri == expectedRequestUri && it.cause is HttpConnectTimeoutException }
                    )
                }
            }
        }

        private fun handlesTimeoutAwaitingFirstResponseByte() = dynamicTest("handles timeout awaiting first response byte") {
            withTimeout(2.seconds) {
                val countDownLatch = CountDownLatch(1)
                fakeServers(publicKeyInfrastructure.keyManagers) { exchange ->
                    countDownLatch.await()
                    exchange.sendResponseHeaders(validResponseCode, 2)
                    exchange.responseBody.use { it.write("[]".toByteArray(UTF_8)) }
                }.use { fakeServers ->
                    val recordingAuditor = RecordingAuditor<GitHubHttp.AuditEvent>()
                    try {
                        val firstByteTimeout = 100.milliseconds
                        val outcome = executor(
                            GitHubHttp(
                                GitHubApiAuthority(fakeServers.apiServerAuthority),
                                publicKeyInfrastructure.releaseTrustStore,
                                recordingAuditor,
                                connectTimeout = 10.seconds,
                                firstByteTimeout = firstByteTimeout,
                                endToEndTimeout = 10.seconds
                            ),
                            GitHubUploadAuthority(fakeServers.uploadServerAuthority)
                        )
                        val expectedRequestUri = expectedUri(fakeServers.apiServerAuthority, fakeServers.uploadServerAuthority)
                        failureOutcomeAssertions(outcome).shouldBeInstanceOf<Failure.FirstByteTimeout>()
                            .also { failure ->
                                assertSoftly(failure) {
                                    it.uri shouldBe expectedRequestUri
                                    it.firstByteTimeout shouldBe firstByteTimeout
                                    it.exception.shouldBeInstanceOf<HttpTimeoutException>().shouldNotBeInstanceOf<HttpConnectTimeoutException>()
                                }
                            }
                        recordingAuditor.auditEvents().shouldExistInOrder(
                            { it is RequestFailed && it.uri == expectedRequestUri && it.cause is HttpTimeoutException && it.cause !is HttpConnectTimeoutException }
                        )
                    } finally {
                        countDownLatch.countDown()
                    }
                }
            }
        }

        private fun handlesTimeoutDuringSlowResponse() = dynamicTest("handles timeout during slow response") {
            withTimeout(2.seconds) {
                val countDownLatch = CountDownLatch(1)
                fakeServers(publicKeyInfrastructure.keyManagers) { exchange ->
                    exchange.sendResponseHeaders(validResponseCode, 23)
                    exchange.responseBody.use {
                        it.write("\"first part".toByteArray(UTF_8))
                        countDownLatch.await()
                        it.write("second part/".toByteArray(UTF_8))
                    }
                }.use { fakeServers ->
                    val recordingAuditor = RecordingAuditor<GitHubHttp.AuditEvent>()
                    try {
                        val endToEndTimeout = 100.milliseconds
                        val outcome = executor(
                            GitHubHttp(
                                GitHubApiAuthority(fakeServers.apiServerAuthority),
                                publicKeyInfrastructure.releaseTrustStore,
                                recordingAuditor,
                                connectTimeout = 10.seconds,
                                firstByteTimeout = 10.seconds,
                                endToEndTimeout = endToEndTimeout
                            ),
                            GitHubUploadAuthority(fakeServers.uploadServerAuthority)
                        )
                        val expectedRequestUri = expectedUri(fakeServers.apiServerAuthority, fakeServers.uploadServerAuthority)
                        failureOutcomeAssertions(outcome).shouldBeInstanceOf<Failure.EndToEndTimeout>()
                            .also { failure ->
                                assertSoftly(failure) {
                                    it.uri shouldBe expectedRequestUri
                                    it.endToEndTimeout shouldBe endToEndTimeout
                                    it.exception.shouldBeInstanceOf<TimeoutException>()
                                }
                            }
                        recordingAuditor.auditEvents().shouldExistInOrder(
                            { it is RequestFailed && it.uri == expectedRequestUri && it.cause is TimeoutException }
                        )
                    } finally {
                        countDownLatch.countDown()
                    }
                }
            }
        }

        fun handlesTimeoutDuringSlowRequestReceipt(): DynamicTest = dynamicTest("handles timeout during slow request receipt") {
            withTimeout(2.seconds) {
                val responseBodyBytes = sunnyDayResponse.toByteArray(UTF_8)
                val countDownLatch = CountDownLatch(1)
                fakeServers(publicKeyInfrastructure.keyManagers) { exchange ->
                    exchange.requestBody.use {
                        countDownLatch.await()
                        it.transferTo(nullOutputStream())
                    }
                    exchange.sendResponseHeaders(validResponseCode, responseBodyBytes.size.toLong())
                    exchange.responseBody.use { it.write(responseBodyBytes) }
                }.use { fakeServers ->
                    val recordingAuditor = RecordingAuditor<GitHubHttp.AuditEvent>()
                    try {
                        val endToEndTimeout = 100.milliseconds
                        val outcome = executor(
                            GitHubHttp(
                                GitHubApiAuthority(fakeServers.apiServerAuthority),
                                publicKeyInfrastructure.releaseTrustStore,
                                recordingAuditor,
                                connectTimeout = 10.seconds,
                                firstByteTimeout = 10.seconds,
                                endToEndTimeout = endToEndTimeout
                            ),
                            GitHubUploadAuthority(fakeServers.uploadServerAuthority)
                        )
                        val expectedRequestUri = expectedUri(fakeServers.apiServerAuthority, fakeServers.uploadServerAuthority)
                        failureOutcomeAssertions(outcome).shouldBeInstanceOf<Failure.EndToEndTimeout>()
                            .also { failure ->
                                assertSoftly(failure) {
                                    it.uri shouldBe expectedRequestUri
                                    it.endToEndTimeout shouldBe endToEndTimeout
                                    it.exception.shouldBeInstanceOf<TimeoutException>()
                                }
                            }
                        recordingAuditor.auditEvents().shouldExistInOrder(
                            { it is RequestFailed && it.uri == expectedRequestUri && it.cause is TimeoutException }
                        )
                    } finally {
                        countDownLatch.countDown()
                    }
                }
            }
        }

        fun handlesUnexpectedlyShapedJsonResponse(): DynamicTest = dynamicTest("handles unexpectedly-shaped json response") {
            val responseBody = """{}"""
            val responseBodyBytes = responseBody.toByteArray(UTF_8)
            fakeServers(publicKeyInfrastructure.keyManagers) { exchange ->
                exchange.sendResponseHeaders(validResponseCode, responseBodyBytes.size.toLong())
                exchange.responseBody.use { it.write(responseBodyBytes) }
            }.use { fakeServers ->
                val recordingAuditor = RecordingAuditor<GitHubHttp.AuditEvent>()
                val outcome = executor(
                    GitHubHttp(GitHubApiAuthority(fakeServers.apiServerAuthority), publicKeyInfrastructure.releaseTrustStore, recordingAuditor),
                    GitHubUploadAuthority(fakeServers.uploadServerAuthority)
                )

                val expectedRequestUri = expectedUri(fakeServers.apiServerAuthority, fakeServers.uploadServerAuthority)

                failureOutcomeAssertions(outcome).shouldBeInstanceOf<Failure.ResponseHandlingException>()
                    .also { failure ->
                        assertSoftly(failure) {
                            it.uri shouldBe expectedRequestUri
                            it.responseCode shouldBe validResponseCode
                            it.responseHeaders.shouldContain("content-length" to responseBodyBytes.size.toString())
                            it.responseBody shouldBe responseBody
                            it.exception.shouldBeInstanceOf<IllegalArgumentException>()
                        }
                    }
                recordingAuditor.auditEvents().shouldBeSingleton().forAll { element ->
                    element.shouldBeInstanceOf<RequestCompleted>().also {
                        assertSoftly(element) {
                            it.uri shouldBe expectedRequestUri
                            it.statusCode shouldBe validResponseCode
                            it.headers.shouldContain("content-length" to responseBodyBytes.size.toString())
                            it.responseBody shouldBe responseBody
                        }
                    }
                }
            }
        }

        fun handlesNonJsonResponse(): DynamicTest = dynamicTest("handles non-json response") {
            val responseBody = """not json"""
            val responseBodyBytes = responseBody.toByteArray(UTF_8)
            fakeServers(publicKeyInfrastructure.keyManagers) { exchange ->
                exchange.sendResponseHeaders(validResponseCode, responseBodyBytes.size.toLong())
                exchange.responseBody.use { it.write(responseBodyBytes) }
            }.use { fakeServers ->
                val recordingAuditor = RecordingAuditor<GitHubHttp.AuditEvent>()
                val outcome = executor(
                    GitHubHttp(GitHubApiAuthority(fakeServers.apiServerAuthority), publicKeyInfrastructure.releaseTrustStore, recordingAuditor),
                    GitHubUploadAuthority(fakeServers.uploadServerAuthority)
                )

                val expectedRequestUri = expectedUri(fakeServers.apiServerAuthority, fakeServers.uploadServerAuthority)

                failureOutcomeAssertions(outcome).shouldBeInstanceOf<Failure.ResponseHandlingException>()
                    .also { failure ->
                        assertSoftly(failure) {
                            it.uri shouldBe expectedRequestUri
                            it.responseCode shouldBe validResponseCode
                            it.responseHeaders.shouldContain("content-length" to responseBodyBytes.size.toString())
                            it.responseBody shouldBe responseBody
                            it.exception.shouldBeInstanceOf<InvalidSyntaxException>()
                        }
                    }
                recordingAuditor.auditEvents().shouldBeSingleton().forAll { element ->
                    element.shouldBeInstanceOf<RequestCompleted>().also {
                        assertSoftly(element) {
                            it.uri shouldBe expectedRequestUri
                            it.statusCode shouldBe validResponseCode
                            it.headers.shouldContain("content-length" to responseBodyBytes.size.toString())
                            it.responseBody shouldBe responseBody
                        }
                    }
                }
            }
        }

        fun toDynamicNode(): DynamicNode = dynamicContainer(
            name, listOf(
                sunnyDay(),
                setsRequestHeaders(),
                handlesUnexpectedResponseCode(),
                handlesIoExceptionProcessingResponse(),
                handlesUnresolvableAddress(),
                handlesSslFailure(),
                handlesConnectTimeout(),
                handlesTimeoutAwaitingFirstResponseByte(),
                handlesTimeoutDuringSlowResponse(),
            ) + supplementaryTests
        )
    }

    interface FakeServers : AutoCloseable {
        val apiServerAuthority: Authority
        val uploadServerAuthority: Authority

        companion object {
            fun fakeServers(keyManagers: List<KeyManager>, httpHandler: HttpHandler) = fakeServers(keyManagers, httpHandler, httpHandler)
            fun fakeServers(keyManagers: List<KeyManager>, apiServerHttpHandler: HttpHandler, uploadServerHttpHandler: HttpHandler): FakeServers {
                val fakeApiServer = fakeHttpServer(keyManagers, apiServerHttpHandler)
                val fakeUploadServer = fakeHttpServer(keyManagers, uploadServerHttpHandler)
                return object : FakeServers {
                    override val apiServerAuthority = fakeApiServer.authority
                    override val uploadServerAuthority = fakeUploadServer.authority

                    override fun close() {
                        fakeUploadServer.close()
                        fakeApiServer.close()
                    }
                }
            }
        }
    }
}