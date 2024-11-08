package ru.topacademy.Data;

import com.github.javafaker.Faker;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public class DataHelper {
    // Создаем экземпляр Faker для генерации случайных данных на английском языке
    private static final Faker faker = new Faker(new Locale("en"));

    private DataHelper() {
        // Приватный конструктор, чтобы предотвратить создание экземпляров класса
    }

    // Возвращает заранее заданный номер карты, который будет одобрен
    public static String getCardNumberApproved() {
        return "4444 4444 4444 4441";
    }

    // Возвращает заранее заданный номер карты, который будет отклонен
    public static String getCardNumberDeclined() {
        return "4444 4444 4444 4442";
    }

    // Генерирует случайный номер карты (16 цифр), который не имеет конкретного статуса
    public static String getCardNumberNothing() {
        return faker.number().digits(16);
    }

    // Генерирует случайный номер карты с произвольной длиной (менее 16 цифр)
    public static String getCardNumberNotFilled() {
        int randomNumberLength = faker.random().nextInt(16);
        return faker.number().digits(randomNumberLength);
    }

    // Возвращает пустую строку, имитируя не заполненное поле номера карты
    public static String getCardNumberEmpty() {
        return "";
    }

    // Возвращает месяц, который был один месяц назад от текущей даты (формат MM)
    public static String getMonthOneMonthAgo() {
        LocalDate currentDate = LocalDate.now();
        LocalDate oneMonthAgo = currentDate.minusMonths(1);
        return oneMonthAgo.format(DateTimeFormatter.ofPattern("MM"));
    }

    // Возвращает текущий год (последние две цифры)
    public static String getCurrentYear() {
        LocalDate currentDate = LocalDate.now();
        int currentYear = currentDate.getYear();
        return String.format("%02d", currentYear % 100);
    }

    // Возвращает год, который будет через 6 лет (последние две цифры)
    public static String getCurrentYearPlus6() {
        int currentYear = Integer.parseInt(getCurrentYear());
        int yearPlus6 = currentYear + 6;
        return String.format("%02d", yearPlus6 % 100);
    }

    // Возвращает предыдущий год (последние две цифры)
    public static String getPreviousYear() {
        int currentYear = Integer.parseInt(getCurrentYear());
        int previousYear = currentYear - 1;
        return String.format("%02d", previousYear % 100);
    }

    // Генерирует случайный месяц (01-12)
    public static String getMonth() {
        return String.format("%02d", faker.number().numberBetween(1, 13));
    }

    // Возвращает недопустимый месяц (00)
    public static String getInvalidMonth() {
        return "00";
    }

    // Возвращает пустую строку, имитируя не заполненное поле месяца
    public static String getEmptyMonth() {
        return "";
    }

    // Возвращает пустую строку, имитируя не заполненное поле года
    public static String getEmptyYear() {
        return "";
    }

    // Генерирует случайный год (последние две цифры) в диапазоне 24-29
    public static String getYear() {
        return String.format("%02d", faker.number().numberBetween(24, 29));
    }

    // Генерирует полное имя пользователя
    public static String getUser() {
        return faker.name().fullName();
    }

    // Генерирует случайную цифру (0-9), представляющую номер пользователя
    public static String getNumberUser() {
        return faker.number().digit();
    }

    // Возвращает строку со специальными символами
    public static String getSpecialCharactersUser() {
        return "(%;%:?*(@!$)";
    }

    // Возвращает пустую строку, имитируя не заполненное поле имени пользователя
    public static String getEmptyUser() {
        return "";
    }

    // Генерирует случайный CVC-код (3 цифры)
    public static String getCvc() {
        return faker.number().digits(3);
    }

    // Генерирует CVC-код с 1 цифрой
    public static String get1Cvc() {
        return faker.number().digits(1);
    }

    // Генерирует CVC-код с 2 цифрами
    public static String get2Cvc() {
        return faker.number().digits(2);
    }

    // Возвращает пустую строку, имитируя не заполненное поле CVC
    public static String getEmptyCvc() {
        return "";
    }



}
