package com.example.dbtestrestapibft

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*


@RestController
@RequestMapping("/person")
class PersonController {

    @GetMapping
    fun getAllPersons() = listOf(
        Person(1, "Ivan", "Petrov"),
        Person(2, "George", "Ivanov")
    )

    @GetMapping("/name/{name}")
    fun getPersonByName(@PathVariable name: String): Person {
        return Person(1, "Ivan", "Petrov")
    }

    @GetMapping("/lastName/{lastName}")
    fun getPersonByLastName(@PathVariable lastName: String): Person {
        return Person(1, "Ivan", "Petrov")
    }

    @GetMapping("/id/{id}")
    fun getPersonById(@PathVariable id: Long): Person {
        if (id != 1L)
            throw PersonNotFoundException(id)

        return Person(1, "Ivan", "Petrov")
    }

    @PostMapping
    fun addPerson(@RequestBody newPerson: Person) {

    }

    @PutMapping("/{id}")
    fun updatePerson(@RequestBody newPerson: Person, @PathVariable id: Long) {

    }

    @DeleteMapping("/{id}")
    fun deletePerson(@PathVariable id: Long) {

    }
}

internal class PersonNotFoundException(id: Long) : RuntimeException("Couldn't find person id=$id")

@ControllerAdvice
internal class PersonAdvice {
    @ResponseBody
    @ExceptionHandler(PersonNotFoundException::class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    fun personNotFoundHandler(ex: PersonNotFoundException): ErrorNotify {
        return ErrorNotify(ex.message!!)
    }

    data class ErrorNotify(val error: String)
}

data class Person(val id: Long, val name: String, val lastName: String)

