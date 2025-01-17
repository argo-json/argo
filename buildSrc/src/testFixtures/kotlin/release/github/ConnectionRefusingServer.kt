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

import net.sourceforge.urin.Authority
import net.sourceforge.urin.Authority.authority
import net.sourceforge.urin.Host.LOCAL_HOST
import net.sourceforge.urin.Port.port
import release.pki.PkiTestingFactories
import java.security.SecureRandom
import java.util.concurrent.CountDownLatch
import javax.net.ssl.SSLContext

class ConnectionRefusingServer private constructor(val authority: Authority, private val shutdown: () -> Unit) : AutoCloseable {
    companion object {
        fun connectionRefusingServer(publicKeyInfrastructure: PkiTestingFactories.PublicKeyInfrastructure): ConnectionRefusingServer {
            val readyCountDownLatch = CountDownLatch(1)
            val disposeCountDownLatch = CountDownLatch(1)
            return SSLContext.getInstance("TLS").apply {
                init(publicKeyInfrastructure.keyManagers.toTypedArray(), null, SecureRandom())
            }.serverSocketFactory.createServerSocket(0).let { serverSocket ->
                val serverThread = Thread {
                    serverSocket.accept().use { _ ->
                        readyCountDownLatch.countDown()
                        disposeCountDownLatch.await()
                    }
                }.apply {
                    start()
                }
                val clientThread = Thread {
                    publicKeyInfrastructure.releaseTrustStore.sslContext.socketFactory.createSocket("localhost", serverSocket.localPort).use { _ ->
                        disposeCountDownLatch.await()
                    }
                }.apply {
                    start()
                }

                readyCountDownLatch.await()

                ConnectionRefusingServer(authority(LOCAL_HOST, port(serverSocket.localPort))) {
                    disposeCountDownLatch.countDown()
                    serverThread.join()
                    clientThread.join()
                    serverSocket.close()
                }
            }
        }
    }

    override fun close() {
        shutdown()
    }
}