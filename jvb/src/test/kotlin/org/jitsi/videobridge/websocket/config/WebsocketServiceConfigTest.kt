/*
 * Copyright @ 2018 - present 8x8, Inc.
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

package org.jitsi.videobridge.websocket.config

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.shouldBe
import org.jitsi.ConfigTest
import org.jitsi.config.withNewConfig
import org.jitsi.metaconfig.ConfigException
import org.jitsi.videobridge.websocket.config.WebsocketServiceConfig.Companion.config

class WebsocketServiceConfigTest : ConfigTest() {
    init {
        context("when websockets are disabled") {
            withNewConfig("videobridge.websockets.enabled = false") {
                context("accessing useTls should throw") {
                    shouldThrow<ConfigException.UnableToRetrieve.ConditionNotMet> {
                        config.useTls
                    }
                }
            }
        }
        context("when websockets are enabled") {
            context("accessing domain and relay-domain") {
                withNewConfig(
                    """
    videobridge.websockets.enabled = true
    videobridge.websockets.domain = "new_domain"
    videobridge.websockets.relay-domain = "relay_domain"
                    """.trimIndent()
                ) {
                    config.domains shouldBe listOf("new_domain")
                    config.relayDomains shouldBe listOf("relay_domain")
                }
                withNewConfig(
                    """
    videobridge.websockets.enabled = true
    videobridge.websockets.domain = "new_domain"
    videobridge.websockets.relay-domain = "relay_domain"
                    """.trimIndent()
                ) {
                    config.domains shouldBe listOf("new_domain")
                    config.relayDomains shouldBe listOf("relay_domain")
                }
                withNewConfig(
                    """
    videobridge.websockets.enabled = true
    videobridge.websockets.domains = ["new_domain"]
                    """.trimIndent()
                ) {
                    config.domains shouldBe listOf("new_domain")
                    config.relayDomains shouldBe listOf("new_domain")
                }
                withNewConfig(
                    """
    videobridge.websockets.enabled = true
    videobridge.websockets.domains = ["new_domain"]
    videobridge.websockets.relay-domain = "relay_domain"
                    """.trimIndent()
                ) {
                    config.domains shouldBe listOf("new_domain")
                    config.relayDomains shouldBe listOf("relay_domain")
                }
                withNewConfig(
                    """
    videobridge.websockets.enabled = true
    videobridge.websockets.domains = ["new_domain"]
    videobridge.websockets.relay-domains = ["relay_domain1", "relay_domain2"]
                    """.trimIndent()
                ) {
                    config.domains shouldBe listOf("new_domain")
                    config.relayDomains shouldBe listOf("relay_domain1", "relay_domain2")
                }
                withNewConfig(
                    """
    videobridge.websockets.enabled = true
    videobridge.websockets.domain = "new_domain1"
    videobridge.websockets.domains = ["new_domain2"]
                    """.trimIndent()
                ) {
                    config.domains shouldBe listOf("new_domain1", "new_domain2")
                    config.relayDomains shouldBe listOf("new_domain1", "new_domain2")
                }
            }
            context("accessing useTls") {
                context("when no value has been set") {
                    withNewConfig(newConfigWebsocketsEnabled) {
                        should("return null") {
                            config.useTls shouldBe null
                        }
                    }
                }
                context("when a value has been set") {
                    withNewConfig(newConfigWebsocketsEnableduseTls) {
                        should("get the right value") {
                            config.useTls shouldBe true
                        }
                    }
                }
            }
        }
    }
}
private val newConfigWebsocketsEnabled = """
    videobridge.websockets.enabled = true
""".trimIndent()

private val newConfigWebsocketsEnabledDomain = newConfigWebsocketsEnabled + "\n" + """
    videobridge.websockets.domain = "new_domain"
""".trimIndent()

private val newConfigWebsocketsEnableduseTls = newConfigWebsocketsEnabled + "\n" + """
    videobridge.websockets.tls = true
""".trimIndent()
