package com.github.jaybcee.boatsbegone

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
class BoatsbegoneApplicationTests {

    @Test
    fun contextLoads() {
        assertThat(true).isTrue
    }
}
