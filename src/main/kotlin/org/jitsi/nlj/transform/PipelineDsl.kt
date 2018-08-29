/*
 * Copyright @ 2018 Atlassian Pty Ltd
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
package org.jitsi.nlj.transform

import org.jitsi.nlj.PacketHandler
import org.jitsi.nlj.transform.node.DemuxerNode
import org.jitsi.nlj.transform.node.Node
import org.jitsi.nlj.transform.node.PacketPath
import org.jitsi.rtp.Packet

fun DemuxerNode.packetPath(b: PacketPath.() -> Unit) {
    this.addPacketPath(PacketPath().apply(b))
}

class PipelineBuilder {
    private var head: Node? = null
    private var tail: Node? = null

    private fun addNode(node: Node) {
        if (head == null) {
            head = node
        }
        if (tail is DemuxerNode) {
            // In the future we could separate 'input/output' nodes from purely
            // input nodes and use that here?
            throw Exception("Cannot attach node to a DemuxerNode")
        }
        tail?.attach(node)
        tail = node
    }

    fun node(node: Node) = addNode(node)

    /**
     * simpleNode allows the caller to pass in a block of code which takes a list of input
     * [Packet]s and returns a list of output [Packet]s to be forwarded to the next
     * [Node]
     */
    fun simpleNode(name: String, packetHandler: (List<Packet>) -> List<Packet>) {
        val node = object : Node(name) {
            override fun doProcessPackets(p: List<Packet>) {
                next(packetHandler.invoke(p))
            }
        }
        addNode(node)
    }

    fun demux(block: DemuxerNode.() -> Unit) {
        val demuxer = DemuxerNode().apply(block)
        addNode(demuxer)
    }

    fun build(): Node = head!!
}

fun pipeline(block: PipelineBuilder.() -> Unit): Node = PipelineBuilder().apply(block).build()
