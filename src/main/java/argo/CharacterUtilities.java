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

final class CharacterUtilities {

    private CharacterUtilities() {
    }

    static String toPrintableString(final char character) {
        if (character < 32 || character >= 127) {
            return String.format("\\u%04X", (int) character);
        } else {
            return Character.toString(character);
        }
    }

    static String toCharacterArrayString(final String characters) {
        return toCharacterArrayString(characters, 0, characters.length());
    }

    static String toCharacterArrayString(final String characters, final int end) {
        return toCharacterArrayString(characters, 0, end);
    }

    static String toCharacterArrayString(final String characters, final int start, final int end) {
        final StringBuilder result = new StringBuilder("[");
        for (int i = start; i < characters.length() && i < end; i++) {
            if (i > start) {
                result.append(", ");
            }
            result.append(toPrintableString(characters.charAt(i)));
        }
        result.append(']');
        return result.toString();
    }
}
