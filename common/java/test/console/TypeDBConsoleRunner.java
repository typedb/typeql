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

package com.vaticle.typedb.common.test.console;

import com.vaticle.typedb.common.test.Util;
import org.zeroturnaround.exec.ProcessExecutor;
import org.zeroturnaround.exec.StartedProcess;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.concurrent.TimeoutException;

import static com.vaticle.typedb.common.collection.Collections.concatToList;
import static com.vaticle.typedb.common.collection.Collections.list;
import static com.vaticle.typedb.common.test.Util.getConsoleArchiveFile;
import static com.vaticle.typedb.common.test.Util.unarchive;

public class TypeDBConsoleRunner {

    protected final Path distribution;
    protected ProcessExecutor executor;

    public TypeDBConsoleRunner() throws InterruptedException, TimeoutException, IOException {
        System.out.println("Constructing " + name() + " runner");
        System.out.println("Extracting " + name() + " distribution archive.");
        distribution = unarchive(getConsoleArchiveFile());
        System.out.println(name() + " distribution archive extracted.");
        executor = new ProcessExecutor()
                .directory(distribution.toFile())
                .redirectOutput(System.out)
                .redirectError(System.err)
                .readOutput(true)
                .destroyOnExit();
        System.out.println(name() + " runner constructed");
    }

    public int run(String... options) {
        try {
            StartedProcess consoleProcess = executor.command(command(options)).start();
            return consoleProcess.getProcess().waitFor();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private List<String> command(String... options) {
        List<String> cmd = concatToList(list("console"), list(options));
        return Util.typeDBCommand(cmd);
    }

    private String name() {
        return "TypeDB Console";
    }
}
