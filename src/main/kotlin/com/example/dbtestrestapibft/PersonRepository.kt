package com.example.dbtestrestapibft

interface PersonRepository {

    fun getAll(): List<Person>

    fun findByName(name: String): List<Person>

    fun findByLastName(lastName: String): List<Person>

    fun findById(id: Long): Person?

    fun add(newPerson: Person): Long

    fun update(id: Long, newPerson: Person): Boolean

    fun delete(id: Long): Boolean
}