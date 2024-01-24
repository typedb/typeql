/*
 * Copyright (C) 2022 Vaticle
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 *
 */

package com.vaticle.typedb.common.test.enterprise;

import com.vaticle.typedb.common.conf.enterprise.Addresses;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

class EnterpriseServerOpts {
    private static final String ADDR = "--server.address";
    private static final String INTERNAL_ADDR_ZMQ = "--server.internal-address.zeromq";
    private static final String INTERNAL_ADDR_GRPC = "--server.internal-address.grpc";
    private static final String PEERS = "--server.peers";
    private static final String PEERS_ADDR = PEERS + ".%s.address";
    private static final String PEERS_INTERNAL_ADDR_ZMQ = PEERS + ".%s.internal-address.zeromq";
    private static final String PEERS_INTERNAL_ADDR_GRPC = PEERS + ".%s.internal-address.grpc";
    static final String STORAGE_DATA = "--storage.data";
    static final String STORAGE_REPLICATION = "--storage.replication";
    static final String STORAGE_USER = "--storage.user";
    static final String LOG_OUTPUT_FILE_DIRECTORY = "--log.output.file.base-dir";

    static Addresses address(Map<String, String> options) {
        return Addresses.create(options.get(ADDR), options.get(INTERNAL_ADDR_ZMQ), options.get(INTERNAL_ADDR_GRPC));
    }

    static Map<String, String> address(Addresses addresses) {
        Map<String, String> options = new HashMap<>();
        options.put(ADDR, addresses.externalString());
        options.put(INTERNAL_ADDR_ZMQ, addresses.internalZMQString());
        options.put(INTERNAL_ADDR_GRPC, addresses.internalGRPCString());
        return options;
    }

    static Set<Addresses> peers(Map<String, String> options) {
        Set<String> names = new HashSet<>();
        for (String opt : options.keySet()) {
            Matcher nameMatcher = Pattern.compile(PEERS + ".(.+).*$").matcher(opt);
            if (nameMatcher.find()) {
                names.add(nameMatcher.group(1));
            }
        }
        Set<Addresses> peers = new HashSet<>();
        for (String name : names) {
            Addresses peer = Addresses.create(
                    options.get(String.format(PEERS_ADDR, name)),
                    options.get(String.format(PEERS_INTERNAL_ADDR_ZMQ, name)),
                    options.get(String.format(PEERS_INTERNAL_ADDR_GRPC, name))
            );
            peers.add(peer);
        }
        return peers;
    }

    static Map<String, String> peers(Set<Addresses> peers) {
        Map<String, String> options = new HashMap<>();
        int index = 0;
        for (Addresses peer : peers) {
            String addrKey = String.format(PEERS_ADDR, "server-peer-"+index);
            String intAddrZMQKey = String.format(PEERS_INTERNAL_ADDR_ZMQ, "server-peer-"+index);
            String intAddrGRPCKey = String.format(PEERS_INTERNAL_ADDR_GRPC, "server-peer-"+index);
            options.put(addrKey, peer.externalString());
            options.put(intAddrZMQKey, peer.internalZMQString());
            options.put(intAddrGRPCKey, peer.internalGRPCString());
            index++;
        }
        return options;
    }

    static Path storageData(Map<String, String> options) {
        return Paths.get(options.get(STORAGE_DATA));
    }

    static Path storageReplication(Map<String, String> options) {
        return Paths.get(options.get(STORAGE_REPLICATION));
    }

    static Path logOutput(Map<String, String> options) {
        return Paths.get(options.get(LOG_OUTPUT_FILE_DIRECTORY));
    }
}
