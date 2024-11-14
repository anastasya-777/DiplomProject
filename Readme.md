# Дипломный проект "Автоматизация тестирования веб-сервиса для покупки тура, взаимодействующего с СУБД и API Банка" 

## Описание приложения
#### Приложение — это веб-сервис, который предлагает купить тур по определённой цене двумя способами:
1. Обычная оплата по дебетовой карте.
2. Уникальная технология: выдача кредита по данным банковской карты.
#### Само приложение не обрабатывает данные по картам, а пересылает их банковским сервисам:
* сервису платежей, далее Payment Gate;
* кредитному сервису, далее Credit Gate.
#### Приложение в собственной СУБД должно сохранять информацию о том, успешно ли был совершён платёж и каким способом. Данные карт при этом сохранять не допускается.

## ПО необходимое для запуска авто-тестов
* IntelliJ IDEA - среда разработки с поддержкой всех необходимых библиотек и инструментов.
* Docker Desktop - для запуска приложения и эмулятора банковских сервисов.
* Плагины в IntelliJ IDEA: Docker,Gradle,Allure.

## Процедура запуска авто-тестов
1. Клонировать проект: git clone https://github.com/anastasya-777/DiplomProject.git
2. Открыть проект в IntelliJ IDEA
3. Запустить контейнеры командой docker-compose up --build
4. Для запуска сервиса с указанием пути к базе данных использовать следующие команды:
* для mysql java "-Dspring.datasource.url=jdbc:mysql://localhost:3306/app" -jar artifacts/aqa-shop.jar"
* для postgresql java "-Dspring.datasource.url=jdbc:postgresql://localhost:5432/app" -jar artifacts/aqa-shop.jar"
5. SUT открывается по адресу http://localhost:8080/
6. Запуск тестов выполнить с параметрами, указав путь к базе данных в командной строке:
* для mysql ./gradlew clean test "-Ddb.url=jdbc:mysql://localhost:3306/app"
* для postgresql ./gradlew clean test "-Ddb.url=jdbc:postgresql://localhost:5432/app"
7. Для формирования отчета (Allure), после выполнения тестов выполнить команду ./gradlew allureReport
8. После завершения тестирования завершить работу приложения (CTRL+C) и остановить контейнеры командой docker-compose down



