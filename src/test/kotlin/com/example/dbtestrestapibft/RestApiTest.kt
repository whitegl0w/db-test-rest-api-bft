package com.example.dbtestrestapibft

import org.junit.jupiter.api.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.http.HttpStatus

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class RestApiTest {

    @Autowired
    private lateinit var restTemplate: TestRestTemplate

    @Autowired
    private lateinit var personRepository: PersonRepository

    private lateinit var testPerson: Person

    private val charPool : List<Char> = ('a'..'z') + ('A'..'Z')

    fun rndStr(len: Int) =
        (1..len)
            .map { kotlin.random.Random.nextInt(0, charPool.size) }
            .map(charPool::get)
            .joinToString("")


    @BeforeAll
    fun initDatabase() {
        testPerson = Person(name = rndStr(10), lastName = rndStr(20))
        testPerson.id = personRepository.add(testPerson)
    }

    @Test
    @Throws(Exception::class)
    fun addNew_GetById() {
        val personForAdd = Person(name = rndStr(10), lastName = rndStr(20))

        val location = restTemplate.postForLocation(
            "http://localhost:8080/person",
            personForAdd,
        )
        val respPerson = restTemplate.getForObject(location, Person::class.java)
        assert(respPerson.name == personForAdd.name && respPerson.lastName == personForAdd.lastName)
    }

    @Test
    @Throws(Exception::class)
    fun getByName() {
        val resp = restTemplate.getForEntity(
            "http://localhost:8080/person/name/${testPerson.name}",
            Array<Person>::class.java
        )

        assert(resp.statusCode == HttpStatus.OK)
        assert(resp.hasBody())
        resp.body?.let {
            assert(it.contains(testPerson))
            it.forEach {person ->
                assert(person.name == testPerson.name)
            }
        }
    }

    @Test
    @Throws(Exception::class)
    fun getByLastName() {
        val resp = restTemplate.getForEntity(
            "http://localhost:8080/person/lastName/${testPerson.lastName}",
            Array<Person>::class.java
        )

        assert(resp.statusCode == HttpStatus.OK)
        assert(resp.hasBody())
        resp.body?.let {
            assert(it.contains(testPerson))
            it.forEach {person ->
                assert(person.lastName == testPerson.lastName)
            }
        }
    }

    @Test
    @Throws(Exception::class)
    fun getAll() {
        val resp = restTemplate.getForEntity(
            "http://localhost:8080/person/lastName/${testPerson.lastName}",
            Array<Person>::class.java
        )

        assert(resp.statusCode == HttpStatus.OK)
        assert(resp.hasBody())
        resp.body?.let {
            assert(it.contains(testPerson))
        }
    }

    @Test
    @Throws(Exception::class)
    fun update() {
        val idForUpdate = personRepository.add(Person(name = rndStr(10), lastName = rndStr(20)))
        val data2Update = Person(name = rndStr(10), lastName = rndStr(20))

        restTemplate.put(
            "http://localhost:8080/person/${idForUpdate}",
            data2Update
        )

        data2Update.id = idForUpdate
        assert(personRepository.findById(idForUpdate) == data2Update)
    }

    @Test
    @Throws(Exception::class)
    fun delete() {
        val idForDelete = personRepository.add(Person(name = rndStr(10), lastName = rndStr(20)))

        restTemplate.delete(
            "http://localhost:8080/person/${idForDelete}",
        )

        assert(personRepository.findById(idForDelete) == null)
    }
}