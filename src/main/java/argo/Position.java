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

final class Position {

    /**
     * The line number, or -1 if the line number is unknown.
     */
    final int line;

    /**
     * The column number, or -1 if the column number is unknown.
     */
    final int column;

    Position(final int column, final int line) {
        this.line = line;
        this.column = column;
    }
}
