# Diplom_2

API

Тебе нужно протестировать ручки API для Stellar Burgers.
Пригодится документация API. В ней описаны все ручки сервиса. Тестировать нужно только те, которые указаны в задании. Всё остальное — просто для контекста.

Создание пользователя:
- создать уникального пользователя;
- создать пользователя, который уже зарегистрирован;
- создать пользователя и не заполнить одно из обязательных полей.

Логин пользователя:
- логин под существующим пользователем,
- логин с неверным логином и паролем.

Изменение данных пользователя:
- с авторизацией,
- без авторизации,
  Для обеих ситуаций нужно проверить, что любое поле можно изменить. Для неавторизованного пользователя — ещё и то, что система вернёт ошибку.

Создание заказа:
- с авторизацией,
- без авторизации,
- с ингредиентами,
- без ингредиентов,
- с неверным хешем ингредиентов.

Получение заказов конкретного пользователя:
- авторизованный пользователь,
- неавторизованный пользователь.

Что нужно сделать
- Создай отдельный репозиторий для тестов API.
- Создай Maven-проект.
- Подключи JUnit 4, RestAssured и Allure.
- Напиши тесты.
- Сделай отчёт в Allure.

Как выполнить и сдать работу
- Создай проект в IntelliJ IDEA, залей его на GitHub, запушь ветку develop2 и сделай пул-реквест. Подробная инструкция.

Как будут оценивать твою работу
- Для каждой ручки тесты лежат в отдельном классе.
- Тесты запускаются и проходят.
- Написаны все тесты, указанные в задании.
- В тестах проверяется тело и код ответа.
- Все тесты независимы.
- Для всех шагов автотестов должна быть использована аннотация @Step.
- Нужные тестовые данные создаются перед тестом и удаляются после того, как он выполнится.
- Сделан Allure-отчёт. Отчёт добавлен в пул-реквест.
- Тесты в test/java.
- В файле pom.xml нет ничего лишнего.



Пожелания:
использовать ломбок
использовать рандомизацию на логинах паролях
использовать req spec
урлы как константы
ингриденты получать из ручки
использовать параметризацию
Всем тестам дать аллюр имена
Все вызовы ручек нужно вынести в отдельные классы
Все тесты должны не зависеть от базы данных - юзеров создаем перед тестом и удаляем после.
Расставить аллюр степы
Base uri должен присваиваться в одном месте проекта: можно использовать req spec или класс предок
Всем тестам нужно дать аллюр описания
Для уменьшения boilerplate кода удобно использовать библиотеку lombok
Наследование это отношения «является», то есть тесты наследуют тестам .
везде удаляется юзер