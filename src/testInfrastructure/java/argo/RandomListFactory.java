/*
 * Copyright 2015 Mark Slater
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at
 *
 * 	http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.
 */

package argo;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public final class RandomListFactory {

    private static final Random RANDOM = new Random();

    public static List<Object> aList() {
        return new ArrayList<Object>() {{
            for (int i = 0; i < RANDOM.nextInt(5); i++) {
                add(new Object());
            }
        }};
    }
}
