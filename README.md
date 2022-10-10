# REST API для базы данных

## Структура БД
В базе данных содержится таблица Person со следующими полями

| Поле     | Назначение    |
|----------|---------------|
| id       | Идентификатор |
| name     | Имя           |
| lastName | Фамилия       |

## Поддерживаемые комманды REST API

### Выборка данных

#### Все записи
`GET /person`

    curl -i -X GET localhost:8080/person -H 'Content-type:application/json'

#### По ID
`GET /person/id/{id}`

    curl -i -X GET localhost:8080/person/id/1 -H 'Content-type:application/json'

#### По имени
`GET /person/name/{name}`

    curl -i -X GET localhost:8080/person/name/ivan -H 'Content-type:application/json'

#### По фамилии
`GET /person/lastName/{lastName}`

    curl -i -X GET localhost:8080/person/lastName/Ivanov -H 'Content-type:application/json'

### Добавление данных

`POST /person (+JSON данные в теле запроса)`

    curl -i -X POST localhost:8080/person -H 'Content-type:application/json' -d '{"lastName": "Ivanov", "name": "Ivan"}'

### Обновление данных
`PUT /person/{id}  (+JSON данные в теле запроса)`

    curl -i -X PUT localhost:8080/person/1 -H 'Content-type:application/json' -d '{"lastName": "Ivanov", "name": "Ivan"}'


### Удаление данных
`DELETE person/{id}`

    curl -i -X DELETE localhost:8080/person/1 -H 'Content-type:application/json'