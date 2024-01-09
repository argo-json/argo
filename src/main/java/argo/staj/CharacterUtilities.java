/*
 *  Copyright 2024 Mark Slater
 *
 *  Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at
 *
 *  	http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.
 */

package argo.staj;

final class CharacterUtilities {
    private CharacterUtilities() {
    }

    static String asPrintableString(final char character) {
        if (character < 32 || character >= 127) {
            return String.format("\\u%04X", (int) character);
        } else {
            return Character.toString(character);
        }
    }

    static String asPrintableString(final char[] characters, final int length) {
        final StringBuilder result = new StringBuilder("[");
        for (int i = 0; i < characters.length && i < length; i++) {
            if (i > 0) {
                result.append(", ");
            }
            result.append(asPrintableString(characters[i]));
        }
        result.append(']');
        return result.toString();
    }
}
