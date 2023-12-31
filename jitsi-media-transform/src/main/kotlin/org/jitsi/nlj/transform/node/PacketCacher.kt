/*
 * Copyright @ 2018 - Present, 8x8 Inc
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
package org.jitsi.nlj.transform.node

import org.jitsi.nlj.PacketInfo
import org.jitsi.nlj.stats.NodeStatsBlock
import org.jitsi.nlj.util.PacketCache

class PacketCacher : ObserverNode("Packet cache") {
    private val packetCache = PacketCache()

    override fun observe(packetInfo: PacketInfo) {
        packetCache.insert(packetInfo.packetAs())
    }

    override fun stop() {
        packetCache.stop()
        super.stop()
    }

    override fun trace(f: () -> Unit) = f.invoke()

    fun getPacketCache(): PacketCache = packetCache

    override fun getNodeStats(): NodeStatsBlock {
        return super.getNodeStats().apply {
            // Put the values directly in the node stats and not inside a block to make aggregation work correctly.
            aggregate(packetCache.getNodeStats())
        }
    }
}
