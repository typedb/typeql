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
import com.vaticle.typedb.common.test.Util;
import com.vaticle.typedb.common.test.TypeDBRunner;
import org.zeroturnaround.exec.ProcessExecutor;
import org.zeroturnaround.exec.StartedProcess;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeoutException;

import static com.vaticle.typedb.common.test.Util.createProcessExecutor;
import static com.vaticle.typedb.common.test.Util.getServerArchiveFile;
import static com.vaticle.typedb.common.test.Util.unarchive;

public interface TypeDBEnterpriseServerRunner extends TypeDBRunner {

    Addresses addresses();

    class Factory {

        protected TypeDBEnterpriseServerRunner createServerRunner(Map<String, String> options) {
            try {
                return new Standalone(options);
            } catch (InterruptedException | TimeoutException | IOException e) {
                throw new RuntimeException("Unable to construct runner.");
            }
        }
    }

    class Standalone implements TypeDBEnterpriseServerRunner {

        protected final Path distribution;
        protected final Map<String, String> serverOptions;
        private StartedProcess process;
        protected ProcessExecutor executor;

        public Standalone(Map<String, String> serverOptions) throws IOException, InterruptedException, TimeoutException {
            distribution = unarchive(getServerArchiveFile());
            this.serverOptions = serverOptions;
            System.out.println(addresses() + ": " + name() + " constructing runner...");
            Files.createDirectories(dataDir());
            Files.createDirectories(logsDir());
            executor = createProcessExecutor(distribution);
            System.out.println(addresses() + ": " + name() + " runner constructed.");
        }

        private String name() {
            return "TypeDB Enterprise";
        }

        public Map<String, String> options() {
            return serverOptions;
        }

        @Override
        public String address() {
            return addresses().externalString();
        }

        @Override
        public Addresses addresses() {
            return EnterpriseServerOpts.address(serverOptions);
        }

        public Set<Addresses> peers() {
            return EnterpriseServerOpts.peers(serverOptions);
        }

        private Path dataDir() {
            return EnterpriseServerOpts.storageData(serverOptions);
        }

        private Path replicationDir() {
            return EnterpriseServerOpts.storageReplication(serverOptions);
        }

        private Path logsDir() {
            return EnterpriseServerOpts.logOutput(serverOptions);
        }

        @Override
        public void start() {
            System.out.println(addresses() + ": " + name() + " is starting... ");
            System.out.println(addresses() + ": Distribution is located at " + distribution.toAbsolutePath());
            System.out.println(addresses() + ": Data directory is located at " + dataDir().toAbsolutePath());
            System.out.println(addresses() + ": Server bootup command: '" + command() + "'");
            try {
                process = Util.startProcess(executor, command(), addresses().external());
            } catch (Throwable e) {
                printLogs();
                throw new RuntimeException(e);
            }
        }

        public List<String> command() {
            List<String> cmd = new ArrayList<>();
            cmd.add("enterprise");
            serverOptions.forEach((key, value) -> cmd.add(key + "=" + value));
            return Util.typeDBCommand(cmd);
        }

        @Override
        public boolean isStopped() {
            return process == null || !process.getProcess().isAlive();
        }

        @Override
        public void stop() {
            if (process != null) {
                try {
                    System.out.println(addresses() + ": Stopping...");
                    CompletableFuture<Process> future = process.getProcess().onExit();
                    process.getProcess().destroy();
                    future.get();
                    process = null;
                    System.out.println(addresses() + ": Stopped.");
                } catch (Exception e) {
                    System.out.println("Unable to destroy runner.");
                    printLogs();
                }
            }
        }

        @Override
        public void deleteFiles() {
            stop();
            try {
                Util.deleteDirectoryContents(distribution);
            }
            catch (IOException e) {
                System.out.println("Unable to delete distribution " + distribution.toAbsolutePath());
                e.printStackTrace();
            }
        }

        @Override
        public void reset() {
            stop();
            List<Path> paths = Arrays.asList(dataDir(), logsDir(), replicationDir());
            paths.forEach(path -> {
                try {
                    Util.deleteDirectoryContents(path);
                } catch (IOException e) {
                    System.out.println("Unable to delete " + path.toAbsolutePath());
                    e.printStackTrace();
                }
            });
        }

        private void printLogs() {
            System.out.println(addresses() + ": ================");
            System.out.println(addresses() + ": Logs:");
            Path logPath = logsDir().resolve("typedb.log").toAbsolutePath();
            try {
                executor.command("cat", logPath.toString()).execute();
            } catch (IOException | InterruptedException | TimeoutException e) {
                System.out.println(addresses() + ": Unable to print '" + logPath + "'");
                e.printStackTrace();
            }
            System.out.println(addresses() + ": ================");
        }
    }

}
