/*
 *  Copyright 2024 Mark Slater
 *
 *  Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at
 *
 *  	http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.
 */

package argo;

import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

class InvalidSyntaxRuntimeExceptionTest {

    @Test
    void messageIsCorrectForKnownColumnAndLine() {
        assertThat(
                new InvalidSyntaxRuntimeException("Something went wrong", new Position(1, 2)).getMessage(),
                equalTo("At line 2, column 1:  Something went wrong")
        );
    }

    @Test
    void messageIsCorrectForUnknownColumnAndKnownLine() {
        assertThat(
                new InvalidSyntaxRuntimeException("Something went wrong", new Position(-1, 2)).getMessage(),
                equalTo("At line 2, unknown column:  Something went wrong")
        );
    }

    @Test
    void messageIsCorrectForKnownColumnAndUnknownLine() {
        assertThat(
                new InvalidSyntaxRuntimeException("Something went wrong", new Position(1, -1)).getMessage(),
                equalTo("At unknown line, column 1:  Something went wrong")
        );
    }

    @Test
    void messageIsCorrectForUnknownColumnAndUnknownLine() {
        assertThat(
                new InvalidSyntaxRuntimeException("Something went wrong", new Position(-1, -1)).getMessage(),
                equalTo("At unknown line, unknown column:  Something went wrong")
        );
    }

}