/**
 * Copyright 2020-2021, Eötvös Loránd University.
 * All rights reserved.
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
package p4query.applications.visualisation;

import java.io.IOException;
import java.nio.file.Path;

import org.apache.commons.exec.CommandLine;
import org.apache.commons.exec.DefaultExecuteResultHandler;
import org.apache.commons.exec.DefaultExecutor;
import org.apache.commons.exec.ExecuteException;
import org.apache.commons.exec.ExecuteWatchdog;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class SimpleProcess {

    final Logger logger = LoggerFactory.getLogger(SimpleProcess.class);

    private final CommandLine cmdLine;

    public SimpleProcess(final Path cmd, final String... cmdArgs) {
        this.cmdLine = new CommandLine(cmd.toString());
        cmdLine.addArguments(cmdArgs);
    }

    public void addArguments(final String... cmdArgs) {
        cmdLine.addArguments(cmdArgs);
    }

    public int run() throws ExecuteException, IOException, InterruptedException {
        final DefaultExecutor executor = new DefaultExecutor();
        executor.setWatchdog(new ExecuteWatchdog(100000));
        executor.setExitValue(1);

        final DefaultExecuteResultHandler resultHandler = new DefaultExecuteResultHandler();
        System.out.println("External process is running: " + String.join(" ", cmdLine.toStrings()));
        executor.execute(cmdLine, resultHandler);
        logger.info(cmdLine.toString());
        resultHandler.waitFor();
        System.out.println("Done.");
        logger.info("DONE");

        return resultHandler.getExitValue();
    }
}