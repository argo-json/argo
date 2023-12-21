/*
 *  Copyright 2023 Mark Slater
 *
 *  Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at
 *
 *  	http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.
 */

package argo.staj;

import java.io.Reader;
import java.util.Arrays;

public final class InfiniteReader extends Reader {

    private final char repeatingChar;

    public InfiniteReader(final char repeatingChar) {
        this.repeatingChar = repeatingChar;
    }

    @Override
    public int read(final char[] cbuf, final int off, final int len) {
        Arrays.fill(cbuf, off, off + len, repeatingChar);
        return len;
    }

    @Override
    public void close() {}
}
