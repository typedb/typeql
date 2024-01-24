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
import com.vaticle.typedb.common.test.TypeDBRunner;
import com.vaticle.typedb.common.test.Util;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Path;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static com.vaticle.typedb.common.collection.Collections.map;
import static com.vaticle.typedb.common.collection.Collections.pair;

public class TypeDBEnterpriseRunner implements TypeDBRunner {

    private static final Logger LOG = LoggerFactory.getLogger(TypeDBEnterpriseRunner.class);

    protected final Map<Addresses, Map<String, String>> serverOptionsMap;
    private final TypeDBEnterpriseServerRunner.Factory serverRunnerFactory;
    protected final Map<Addresses, TypeDBEnterpriseServerRunner> serverRunners;
    private static Path runnerPath;

    public static TypeDBEnterpriseRunner create(Path enterpriseRunnerDir, int serverCount) {
        return create(enterpriseRunnerDir, serverCount, new HashMap<>(),
                new TypeDBEnterpriseServerRunner.Factory());
    }

    public static TypeDBEnterpriseRunner create(Path enterpriseRunnerDir, int serverCount, Map<String, String> extraOptions) {
        return create(enterpriseRunnerDir, serverCount, extraOptions, new TypeDBEnterpriseServerRunner.Factory());
    }

    public static TypeDBEnterpriseRunner create(Path enterpriseRunnerDir, int serverCount, Map<String, String> extraOptions,
                                             TypeDBEnterpriseServerRunner.Factory serverRunnerFactory) {
        Set<Addresses> serverAddressesSet = allocateAddressesSet(serverCount);
        Map<Addresses, Map<String, String>> serverOptionsMap = new HashMap<>();
        runnerPath = enterpriseRunnerDir;
        enterpriseRunnerDir = enterpriseRunnerDir.resolve(java.util.UUID.randomUUID().toString());
        for (Addresses addrs: serverAddressesSet) {
            Map<String, String> options = new HashMap<>();
            options.putAll(extraOptions);
            options.putAll(EnterpriseServerOpts.address(addrs));
            options.putAll(EnterpriseServerOpts.peers(serverAddressesSet));
            Path srvRunnerDir = enterpriseRunnerDir.resolve(addrs.externalString()).toAbsolutePath();
            options.putAll(
                    map(
                            pair(EnterpriseServerOpts.STORAGE_DATA, srvRunnerDir.resolve("server/data").toAbsolutePath().toString()),
                            pair(EnterpriseServerOpts.STORAGE_REPLICATION, srvRunnerDir.resolve("server/replication").toAbsolutePath().toString()),
                            pair(EnterpriseServerOpts.STORAGE_USER, srvRunnerDir.resolve("server/user").toAbsolutePath().toString()),
                            pair(EnterpriseServerOpts.LOG_OUTPUT_FILE_DIRECTORY, srvRunnerDir.resolve("server/logs").toAbsolutePath().toString())
                    )
            );
            serverOptionsMap.put(addrs, options);
        }
        return new TypeDBEnterpriseRunner(serverOptionsMap, serverRunnerFactory);
    }

    private static Set<Addresses> allocateAddressesSet(int serverCount) {
        Set<Addresses> addresses = new HashSet<>();
        List<Integer> ports = Util.findUnusedPorts(serverCount * 3);
        for (int i = 0; i < serverCount; i++) {
            String host = "localhost";
            int externalPort = ports.get(3 * i);
            int internalPortZMQ = ports.get(3 * i + 1);
            int internalPortGRPC = ports.get(3 * i + 2);
            addresses.add(Addresses.create(host, externalPort, host, internalPortZMQ, host, internalPortGRPC));
        }
        return addresses;
    }

    private TypeDBEnterpriseRunner(Map<Addresses, Map<String, String>> serverOptionsMap, TypeDBEnterpriseServerRunner.Factory serverRunnerFactory) {
        assert serverOptionsMap.size() >= 1;
        this.serverOptionsMap = serverOptionsMap;
        this.serverRunnerFactory = serverRunnerFactory;
        serverRunners = createServerRunners(this.serverOptionsMap);
    }

    private Map<Addresses, TypeDBEnterpriseServerRunner> createServerRunners(Map<Addresses, Map<String, String>> serverOptsMap) {
        Map<Addresses, TypeDBEnterpriseServerRunner> srvRunners = new HashMap<>();
        for (Addresses addrs: serverOptsMap.keySet()) {
            Map<String, String> options = serverOptsMap.get(addrs);
            TypeDBEnterpriseServerRunner srvRunner = serverRunnerFactory.createServerRunner(options);
            srvRunners.put(addrs, srvRunner);
        }
        return srvRunners;
    }

    @Override
    public void start() {
        for (TypeDBEnterpriseServerRunner runner : serverRunners.values()) {
            if (runner.isStopped()) {
                runner.start();
            } else {
                LOG.debug("not starting server {} - it is already started.", runner.addresses());
            }
        }
    }

    @Override
    public boolean isStopped() {
        return serverRunners.values().stream().allMatch(TypeDBRunner::isStopped);
    }

    @Override
    public String address() {
        return externalAddresses().stream().findAny().get();
    }

    public Set<Addresses> addressesSet() {
        return serverOptionsMap.keySet();
    }

    public Set<String> externalAddresses() {
        return addressesSet().stream().map(Addresses::externalString).collect(Collectors.toSet());
    }

    public Map<Addresses, TypeDBEnterpriseServerRunner> serverRunners() {
        return serverRunners;
    }

    public TypeDBEnterpriseServerRunner serverRunner(String externalAddr) {
        Addresses addresses = addressesSet()
                .stream()
                .filter(addrs -> addrs.externalString().equals(externalAddr))
                .findAny()
                .orElseThrow(() -> new RuntimeException("Server runner '" + externalAddr + "' not found"));
        return serverRunner(addresses);
    }

    public TypeDBEnterpriseServerRunner serverRunner(Addresses addrs) {
        return serverRunners.get(addrs);
    }

    @Override
    public void stop() {
        for (TypeDBEnterpriseServerRunner runner : serverRunners.values()) {
            if (!runner.isStopped()) {
                runner.stop();
            } else {
                LOG.debug("not stopping server {} - it is already stopped.", runner.addresses());
            }
        }
    }

    @Override
    public void deleteFiles() {
        for (TypeDBRunner runner : serverRunners.values()) {
            runner.deleteFiles();
        }
    }

    @Override
    public void reset() {
        for (TypeDBRunner runner : serverRunners.values()) {
            runner.reset();
        }
    }
}
