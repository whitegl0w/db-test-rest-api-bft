package com.example.dbtestrestapibft

import com.example.dbtestrestapibft.PersonRepository.Person
import com.example.dbtestrestapibft.PersonRepository.PersonRepository
import org.awaitility.Awaitility
import org.junit.jupiter.api.Test
import org.mockito.ArgumentCaptor
import org.mockito.Mockito
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.boot.test.mock.mockito.SpyBean
import java.nio.file.Paths
import java.time.Duration


@SpringBootTest
class WatchServiceTest {

    @SpyBean
    lateinit var personWatchService: PersonWatchService

    @MockBean
    lateinit var repository: PersonRepository

    // Всякие приведение типов c !!!, чтобы корректно работало в Kotlin
    fun <T> capture(argumentCaptor: ArgumentCaptor<T>): T = argumentCaptor.capture()
    fun <T> userAny(type: Class<T>): T = Mockito.any(type)

    fun createTestFile() {
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

        // Создание тестового файла
        val file = Paths.get(System.getProperty("user.dir"), "PersonFolder", "test.txt").toFile()
        file.writeText(data2Add, Charsets.UTF_8)
    }

    fun waitUntilWatchServiceFinished() {
        // Ожидание отработки метода WatchService
        Mockito.clearInvocations(personWatchService)
        Awaitility.await().atMost(Duration.ofSeconds(2)).untilAsserted {
            Mockito.verify(personWatchService, Mockito.times(1)).checkFolder()
        }
    }

    /** Проверка на вызов запланированной процедуры проверки */
    @Test
    fun taskRunning() {
        Awaitility.await().atMost(Duration.ofSeconds(2)).untilAsserted {
            Mockito.verify(personWatchService, Mockito.times(1)).checkFolder()
        }
    }

    /** Проверка на корректность работы WatchService (добавление новых) */
    @Test
    fun testInsertPerson() {

        // Имитация отсутствия данных людей в БД
        Mockito
            .`when`(repository.findByLastNameAndName(userAny(Person::class.java)))
            .thenReturn(emptyList())

        // Выполнение WatchService
        createTestFile()
        waitUntilWatchServiceFinished()

        // Проверка
        val requestCaptor: ArgumentCaptor<Person> = ArgumentCaptor.forClass(Person::class.java)
        Mockito.verify(repository, Mockito.atLeast(2)).add(capture(requestCaptor))

        assert(null != requestCaptor.allValues.find {it.name == "leva" && it.lastName == "tolstoi"})
        assert(null != requestCaptor.allValues.find {it.name == "alex" && it.lastName == "pushkin"})
    }

    /** Проверка на корректность работы WatchService (игнор повторов) */
    @Test
    fun testIgnoringDuplicatesPerson() {

        // Имитация наличия людей в БД
        Mockito
            .`when`(repository.findByLastNameAndName(userAny(Person::class.java)))
            .thenReturn(listOf(Person(name = "leva", lastName = "tolstoi")))

        Mockito.clearInvocations(repository)

        // Выполнение WatchService
        createTestFile()
        waitUntilWatchServiceFinished()

        // Проверка
        val requestCaptor: ArgumentCaptor<Person> = ArgumentCaptor.forClass(Person::class.java)
        Mockito.verify(repository, Mockito.never()).add(capture(requestCaptor))
    }

}