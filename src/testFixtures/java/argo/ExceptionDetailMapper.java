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

interface ExceptionDetailMapper {
    ExceptionDetailMapper POSITION_TRACKING_EXCEPTION_DETAIL_MAPPER = new ExceptionDetailMapper() {
        @Override
        public int line(final int actualLine) {
            return actualLine;
        }

        @Override
        public int column(final int actualColumn) {
            return actualColumn;
        }

        @Override
        public String positionText(final int actualLine, final int actualColumn) {
            return "At line " + actualLine + ", column " + actualColumn;
        }
    };
    ExceptionDetailMapper UNTRACKED_POSITION_EXCEPTION_DETAIL_MAPPER = new ExceptionDetailMapper() {
        @Override
        public int line(final int actualLine) {
            return -1;
        }

        @Override
        public int column(final int actualColumn) {
            return -1;
        }

        @Override
        public String positionText(final int actualLine, final int actualColumn) {
            return "At unknown line, unknown column";
        }
    };

    int line(int actualLine);

    int column(int actualColumn);

    String positionText(int actualLine, int actualColumn);
}
