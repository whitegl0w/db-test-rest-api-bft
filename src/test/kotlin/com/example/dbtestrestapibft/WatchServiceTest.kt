package com.example.dbtestrestapibft

import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean
import java.io.File
import java.util.concurrent.ScheduledExecutorService


@SpringBootTest
class WatchServiceTest {


    @MockBean
    private lateinit var schedulerService: ScheduledExecutorService

    @Test
    fun personWatchServiceTest() {
        val data2Add = """
            [
              {
                "name": "alex",
                "lastName": "pushkin"
              },
              {
                "name": "leva",
                "lastName": "tolstoi"
              }
            ]
        """.trimIndent()

        val file = File(System.getProperty("user.dir"), "PersonFolder")
        file.createNewFile()
        file.writeText(data2Add, Charsets.UTF_8)

//        schedulerService.
    }
}