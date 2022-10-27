package com.example.dbtestrestapibft

import com.example.dbtestrestapibft.PersonRepository.Person
import com.example.dbtestrestapibft.PersonRepository.PersonRepository
import com.fasterxml.jackson.databind.DatabindException
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import org.slf4j.LoggerFactory
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import java.io.File
import java.io.IOException
import java.nio.file.Path
import java.nio.file.Paths
import java.nio.file.StandardWatchEventKinds
import java.nio.file.WatchService
import javax.annotation.PostConstruct


@Component
class PersonWatchService(
    private val repository: PersonRepository
) {

    val logger: org.slf4j.Logger = LoggerFactory.getLogger(PersonWatchService::class.java)

    /** Путь к наблюдаемой папке */
    lateinit var path2Folder: Path
    /** Сервис для наблюдения за папкой */
    lateinit var watchService: WatchService
    /** Объект для десереализации данных с JSON */
    lateinit var jsonMapper: ObjectMapper

    /** Инициализация переменных для работы WatchService */
    @PostConstruct
    fun initWatchService() {
        jsonMapper = ObjectMapper().registerKotlinModule()
        path2Folder = Paths.get(System.getProperty("user.dir"), "PersonFolder")
        watchService = path2Folder.fileSystem.newWatchService()
        path2Folder.register(watchService, StandardWatchEventKinds.ENTRY_CREATE)
    }


    /** Периодический опрос папки на наличие данных */
    @Scheduled(fixedDelay = 1000)
    fun checkFolder() {

        val watchKey = try {
            watchService.take()
        } catch (_: InterruptedException) {
            return
        }

        for (event in watchKey.pollEvents()) {

            val file = File(path2Folder.toString(), event.context().toString())

            var newPersons: Array<Person>? = null

            // До 10 попыток читать файл, т.к. система может иногда не успеть освободить файл
            for (attempt in 1..10) {
                try {
                    newPersons = jsonMapper.readValue(file, Array<Person>::class.java)
                    break
                } catch (ex: DatabindException) {
                    logger.info("File ${event.context()} in wrong format")
                    break
                } catch (ex: IOException) {
                    Thread.sleep(10)
                }
            }

            file.delete()

            // Вставка считанных данных в БД
            newPersons?.forEach { person ->
                if (repository.findByLastNameAndName(person).isEmpty()) {
                    repository.add(person)
                    logger.info("Added person (${person.name} ${person.lastName})")
                } else
                    logger.info("Ignoring person (${person.name} ${person.lastName})")
            }
        }

        if (!watchKey.reset()) {
            watchKey.cancel()
            watchService.close()
        }
    }
}