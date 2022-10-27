package com.example.dbtestrestapibft

import com.example.dbtestrestapibft.PersonRepository.Person
import com.example.dbtestrestapibft.PersonRepository.PersonRepository
import org.springframework.boot.CommandLineRunner
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType
import org.springframework.scheduling.annotation.EnableScheduling

@Configuration
@EnableScheduling
class Configuration {

    @Bean
    fun dataSource() = EmbeddedDatabaseBuilder()
        .setType(EmbeddedDatabaseType.H2)
        .addScript("sql/schema.sql")
        .build()

    @Bean
    fun initDatabase(repository: PersonRepository) =
        CommandLineRunner {
            repository.add(Person(name = "Klaudia", lastName = "Duffy"))
            repository.add(Person(name = "Amman", lastName = "Booth"))
            repository.add(Person(name = "Clementine", lastName = "Adam"))
            repository.add(Person(name = "Clay", lastName = "Cox"))
            repository.add(Person(name = "Tilly-Mae", lastName = "Hyde"))
        }
}