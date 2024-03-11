/*
 *  Copyright 2024 Mark Slater
 *
 *  Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at
 *
 *  	http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.
 */

package argo.jdom;

import net.sourceforge.ickles.RandomSizeListSupplier;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static java.util.stream.Collectors.toList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertThrows;

class BuildingCollectionTest {
    @Test
    void rejectsNullBuilderCollection() {
        assertThrows(NullPointerException.class, () -> new BuildingCollection<>(null));
    }

    @Test
    void sizeOfEmptyBuildingCollectionIsZero() {
        assertThat(new BuildingCollection<>(emptyList()).size(), equalTo(0));
    }

    @Test
    void iteratorOfEmptyBuildingCollectionNeverHasNext() {
        assertThat(new BuildingCollection<>(emptyList()).iterator().hasNext(), equalTo(false));
    }

    @Test
    void iteratorOfEmptyBuildingCollectionThrowsOnNext() {
        assertThrows(NoSuchElementException.class, () -> new BuildingCollection<>(emptyList()).iterator().next());
    }

    @Test
    void sizeIsSizeOfBuilderCollection() {
        assertThat(new BuildingCollection<>(singletonList(Object::new)).size(), equalTo(1));
    }

    @Test
    void iteratorOfNonEmptyBuildingCollectionHasNext() {
        assertThat(new BuildingCollection<>(singletonList(Object::new)).iterator().hasNext(), equalTo(true));
    }

    @Test
    void iteratorOfNonEmptyBuildingCollectionBuildsOnNext() {
        final Object built = new Object();
        assertThat(new BuildingCollection<>(singletonList(() -> built)).iterator().next(), equalTo(built));
    }

    @Test
    void producesCollectionOfBuiltElements() {
        final List<Object> expected = new RandomSizeListSupplier<>(Object::new, 1, 20).get();
        final BuildingCollection<Object> buildingCollection = new BuildingCollection<>(expected.stream().map(object -> (Builder<Object>) () -> object).collect(toList()));
        assertThat(new ArrayList<>(buildingCollection), equalTo(expected));
    }
}