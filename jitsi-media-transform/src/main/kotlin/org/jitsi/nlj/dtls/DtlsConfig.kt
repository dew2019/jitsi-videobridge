/*
 * Copyright @ 2019 - present 8x8, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jitsi.nlj.dtls

import org.bouncycastle.tls.CipherSuite
import org.jitsi.config.JitsiConfig
import org.jitsi.metaconfig.ConfigException
import org.jitsi.metaconfig.config
import java.time.Duration

class DtlsConfig {
    val handshakeTimeout: Duration by config {
        "jmt.dtls.handshake-timeout".from(JitsiConfig.newConfig)
    }

    val cipherSuites: List<Int> by config {
        "jmt.dtls.cipher-suites".from(JitsiConfig.newConfig).convertFrom<List<String>> { list ->
            val ciphers = list.map { it.toBcCipherSuite() }
            if (ciphers.isEmpty()) {
                throw ConfigException.UnableToRetrieve.ConditionNotMet("cipher-suites must not be empty")
            }
            ciphers
        }
    }

    companion object {
        val config = DtlsConfig()
    }
}

private fun String.toBcCipherSuite(): Int = try {
    CipherSuite::class.java.getDeclaredField(this).getInt(null)
} catch (e: Exception) {
    throw ConfigException.UnableToRetrieve.ConditionNotMet("Value is not a valid BouncyCastle cipher suite name: $this")
}
