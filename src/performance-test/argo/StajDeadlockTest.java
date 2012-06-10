/*
 * Copyright 2012 Mark Slater
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at
 *
 * 	http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.
 */

package argo;

import argo.staj.JsonStreamException;
import argo.staj.StajParser;
import edu.umd.cs.mtc.MultithreadedTestCase;
import edu.umd.cs.mtc.TestFramework;
import org.junit.Test;

import java.io.StringReader;

public class StajDeadlockTest extends MultithreadedTestCase {

    private StajParser stajParser;

    @Override
    public void initialize() {
        stajParser = new StajParser(new StringReader("{}"));
    }

    @SuppressWarnings("UnusedDeclaration")
    public void thread1() throws JsonStreamException {
        stajParser.next();
        waitForTick(1);
        stajParser.close();
    }

    @Test
    public void deadlocksWhenANewElementIsReadButNotConsumedBeforeClosing() throws Throwable {
        TestFramework.runOnce(new StajDeadlockTest());
    }
}
