package org.dpppt.backend.sdk.data;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;


class EtagGeneratorTest {

    private EtagGeneratorInterface target;

    @BeforeEach
    void setUp() {
        target = new EtagGenerator();
    }

    // TODO ADD MORE TEST WHEN SECRET IS CONFIGURABLE
    @Test
    void shouldReturnCorrectEtag() {
        Assertions.assertThat(target.getEtag(1)).isEqualTo("6bd26b412635ad2a7bdbe07b9f2f6e8b");
    }
}
