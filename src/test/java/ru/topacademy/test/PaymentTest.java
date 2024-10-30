package ru.topacademy.test;
import com.codeborne.selenide.logevents.SelenideLogger;
import io.qameta.allure.selenide.AllureSelenide;
import org.junit.jupiter.api.*;
import ru.topacademy.Data.DataMySql;
import ru.topacademy.Data.DataHelper;
import ru.topacademy.Pages.PaymentPage;

import static com.codeborne.selenide.Selenide.open;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class PaymentTest {

    @BeforeAll
    static void setUpAll() {
        SelenideLogger.addListener("allure", new AllureSelenide());
    }

    @AfterAll
    static void tearDownAll() {
        SelenideLogger.removeListener("allure");
    }

    @BeforeEach
    void clearDatabaseTables() {
        DataMySql.clearTables();
    }

    // Позитивные сценарии (оплата тура дебетовой картой)

    @Test
    @DisplayName("1. Оплата тура с валидной дебетовой картой")
    public void testCashValidCard() {
        var paymentPage = open("http://localhost:8080", PaymentPage.class);
        paymentPage.buyWithCash();
        paymentPage.setCardNumber(DataHelper.getCardNumberApproved());
        paymentPage.setCardMonth(DataHelper.getMonth());
        paymentPage.setCardYear(DataHelper.getYear());
        paymentPage.setCardUser(DataHelper.getUser());
        paymentPage.setCardCVC(DataHelper.getCvc());
        paymentPage.clickContinueButton();
        paymentPage.messageSuccess();
        assertEquals("APPROVED", DataMySql.findPayStatus());
    }

    @Test
    @DisplayName("2. Повторная оплата тура с валидной дебетовой картой")
    public void testRepeatCashValidCard() {
        testCashValidCard();
    }

    @Test
    @DisplayName("3. Оплата тура с дебетовой картой с достаточным балансом")
    public void testCashCardWithSufficientBalance() {
        var paymentPage = open("http://localhost:8080", PaymentPage.class);
        paymentPage.buyWithCash();
        paymentPage.setCardNumber(DataHelper.getCardNumberApproved());
        paymentPage.setCardUser (DataHelper.getUser ());
        paymentPage.setCardMonth(DataHelper.getMonth());
        paymentPage.setCardYear(DataHelper.getYear());
        paymentPage.setCardCVC(DataHelper.getCvc());
        paymentPage.clickContinueButton();
        paymentPage.messageSuccess();
        assertEquals("APPROVED", DataMySql.findPayStatus());
    }

    @Test
    @DisplayName("4. Оплата тура с дебетовой картой с недостаточным балансом, но с возможностью списания средств")
    public void testCashCardWithInsufficientBalanceButPossible() {
        var paymentPage = open("http://localhost:8080", PaymentPage.class);
        paymentPage.buyWithCash();
        paymentPage.setCardNumber(DataHelper.getCardNumberApproved());
        paymentPage.setCardUser (DataHelper.getUser ());
        paymentPage.setCardMonth(DataHelper.getMonth());
        paymentPage.setCardYear(DataHelper.getYear());
        paymentPage.setCardCVC(DataHelper.getCvc());
        paymentPage.clickContinueButton();
        paymentPage.messageSuccess();
        assertEquals("APPROVED", DataMySql.findPayStatus());
    }

    @Test
    @DisplayName("5. Оплата тура с дебетовой картой с ограничениями на проведение операций")
    public void testCashCardWithOperationRestrictions() {
        var paymentPage = open("http://localhost:8080", PaymentPage.class);
        paymentPage.buyWithCash();
        paymentPage.setCardNumber(DataHelper.getCardNumberApproved());
        paymentPage.setCardUser (DataHelper.getUser ());
        paymentPage.setCardMonth(DataHelper.getMonth());
        paymentPage.setCardYear(DataHelper.getYear());
        paymentPage.setCardCVC(DataHelper.getCvc());
        paymentPage.clickContinueButton();
        paymentPage.messageSuccess();
        assertEquals("APPROVED", DataMySql.findPayStatus());
    }

    // Негативные сценарии (валидации дебетовой карты)

    @Test
    @DisplayName("1. Попытка оплаты тура по несуществующему номеру дебетовой карты")
    public void testCashInvalidCardNumber() {
        var paymentPage = open("http://localhost:8080", PaymentPage.class);
        paymentPage.buyWithCash();
        paymentPage.setCardNumber(DataHelper.getCardNumberNothing());
        paymentPage.setCardMonth(DataHelper.getMonth());
        paymentPage.setCardYear(DataHelper.getYear());
        paymentPage.setCardUser(DataHelper.getUser());
        paymentPage.setCardCVC(DataHelper.getCvc());
        paymentPage.clickContinueButton();
        paymentPage.messageError();
        assertEquals(0, DataMySql.getOrderEntityCount());
    }

    @Test
    @DisplayName("2. Попытка оплаты тура с не полностью заполненным номером дебетовой карты")
    public void testCashCardNotFilled() {
        var paymentPage = open("http://localhost:8080", PaymentPage.class);
        paymentPage.buyWithCash();
        paymentPage.setCardNumber("444444444444444");
        paymentPage.setCardMonth(DataHelper.getMonth());
        paymentPage.setCardYear(DataHelper.getYear());
        paymentPage.setCardUser(DataHelper.getUser());
        paymentPage.setCardCVC(DataHelper.getCvc());
        paymentPage.clickContinueButton();
        paymentPage.incorrectFormat();
        assertEquals(0, DataMySql.getOrderEntityCount());
    }

    @Test
    @DisplayName("3. Попытка оплаты тура с невалидными данными владельца дебетовой карты")
    public void testCashInvalidOwnerName() {
        var paymentPage = open("http://localhost:8080", PaymentPage.class);
        paymentPage.buyWithCash();
        paymentPage.setCardNumber(DataHelper.getCardNumberApproved());
        paymentPage.setCardUser ("Иванов Иван");
        paymentPage.setCardMonth(DataHelper.getMonth());
        paymentPage.setCardYear(DataHelper.getYear());
        paymentPage.setCardCVC(DataHelper.getCvc());
        paymentPage.clickContinueButton();
        paymentPage.incorrectFormat();
        assertEquals(0, DataMySql.getOrderEntityCount());
    }

    @Test
    @DisplayName("4. Попытка оплаты тура с невалидным сроком действия дебетовой карты")
    public void testCashInvalidExpirationDate() {
        var paymentPage = open("http://localhost:8080", PaymentPage.class);
        paymentPage.buyWithCash();
        paymentPage.setCardNumber(DataHelper.getCardNumberApproved());
        paymentPage.setCardMonth("13");
        paymentPage.setCardYear(DataHelper.getYear());
        paymentPage.setCardUser(DataHelper.getUser());
        paymentPage.setCardCVC(DataHelper.getCvc());
        paymentPage.clickContinueButton();
        paymentPage.invalidCardExpirationDate();
        assertEquals(0, DataMySql.getOrderEntityCount());
    }

    @Test
    @DisplayName("5. Попытка оплаты тура с невалидным CVV кодом дебетовой карты")
    public void testCashInvalidCvcCode() {
        var paymentPage = open("http://localhost:8080", PaymentPage.class);
        paymentPage.buyWithCash();
        paymentPage.setCardNumber(DataHelper.getCardNumberApproved());
        paymentPage.setCardMonth(DataHelper.getMonth());
        paymentPage.setCardYear(DataHelper.getYear());
        paymentPage.setCardUser(DataHelper.getUser());
        paymentPage.setCardCVC(DataHelper.get1Cvc());
        paymentPage.clickContinueButton();
        paymentPage.incorrectFormat();
        assertEquals(0, DataMySql.getOrderEntityCount());
    }

    @Test
    @DisplayName("6. Попытка оплаты тура с невалидным месяцем дебетовой карты")
    public void testCashInvalidMonth() {
        var paymentPage = open("http://localhost:8080", PaymentPage.class);
        paymentPage.buyWithCash();
        paymentPage.setCardNumber(DataHelper.getCardNumberApproved());
        paymentPage.setCardMonth(DataHelper.getInvalidMonth());
        paymentPage.setCardYear(DataHelper.getYear());
        paymentPage.setCardUser(DataHelper.getUser());
        paymentPage.setCardCVC(DataHelper.getCvc());
        paymentPage.clickContinueButton();
        paymentPage.invalidCardExpirationDate();
        assertEquals(0, DataMySql.getOrderEntityCount());
    }

    @Test
    @DisplayName("7. Попытка оплаты тура с невалидным годом дебетовой карты")
    public void testCashInvalidYear() {
        var paymentPage = open("http://localhost:8080", PaymentPage.class);
        paymentPage.buyWithCash();
        paymentPage.setCardNumber(DataHelper.getCardNumberApproved());
        paymentPage.setCardMonth(DataHelper.getMonth());
        paymentPage.setCardYear("23");
        paymentPage.setCardUser (DataHelper.getUser ());
        paymentPage.setCardCVC(DataHelper.getCvc());
        paymentPage.clickContinueButton();
        paymentPage.cardExpired();
        assertEquals(0, DataMySql.getOrderEntityCount());
    }

    @Test
    @DisplayName("8. Попытка оплаты тура с заполненным только номером дебетовой карты")
    public void testCashOnlyCardNumber() {
        var paymentPage = open("http://localhost:8080", PaymentPage.class);
        paymentPage.buyWithCash();
        paymentPage.setCardNumber("4444 4444 4444 4442");
        paymentPage.setCardMonth(DataHelper.getEmptyMonth());
        paymentPage.setCardYear(DataHelper.getEmptyYear());
        paymentPage.setCardUser(DataHelper.getEmptyUser());
        paymentPage.setCardCVC(DataHelper.getEmptyCvc());
        paymentPage.clickContinueButton();
        paymentPage.incorrectFormat();
        assertEquals(0, DataMySql.getOrderEntityCount());
    }

    @Test
    @DisplayName("9. Попытка оплаты тура с заполненным только именем владельца дебетовой карты")
    public void testCashOnlyOwnerName() {
        var paymentPage = open("http://localhost:8080", PaymentPage.class);
        paymentPage.buyWithCash();
        paymentPage.setCardMonth(DataHelper.getEmptyMonth());
        paymentPage.setCardYear(DataHelper.getEmptyYear());
        paymentPage.setCardNumber(DataHelper.getCardNumberEmpty());
        paymentPage.setCardUser ("Ivanov Ivan");
        paymentPage.setCardCVC(DataHelper.getEmptyCvc());
        paymentPage.clickContinueButton();
        paymentPage.incorrectFormat();
        assertEquals(0, DataMySql.getOrderEntityCount());
    }

    @Test
    @DisplayName("10. Попытка оплаты тура с заполненным только сроком действия дебетовой карты")
    public void testCashOnlyExpirationDate() {
        var paymentPage = open("http://localhost:8080", PaymentPage.class);
        paymentPage.buyWithCash();
        paymentPage.setCardNumber(DataHelper.getCardNumberEmpty());
        paymentPage.setCardMonth(DataHelper.getMonth());
        paymentPage.setCardYear(DataHelper.getYear());
        paymentPage.setCardUser(DataHelper.getEmptyUser());
        paymentPage.setCardCVC(DataHelper.getEmptyCvc());
        paymentPage.clickContinueButton();
        paymentPage.incorrectFormat();
        assertEquals(0, DataMySql.getOrderEntityCount());
    }

    @Test
    @DisplayName("11. Попытка оплаты тура с заполненным только кодом безопасности дебетовой карты")
    public void testCashOnlyCvc() {
        var paymentPage = open("http://localhost:8080", PaymentPage.class);
        paymentPage.buyWithCash();
        paymentPage.setCardNumber(DataHelper.getCardNumberEmpty());
        paymentPage.setCardMonth(DataHelper.getEmptyMonth());
        paymentPage.setCardYear(DataHelper.getEmptyYear());
        paymentPage.setCardUser(DataHelper.getEmptyUser());
        paymentPage.setCardCVC("123");
        paymentPage.clickContinueButton();
        paymentPage.incorrectFormat();
        assertEquals(0, DataMySql.getOrderEntityCount());
    }

    @Test
    @DisplayName("12. Попытка оплаты тура с невалидным годом дебетовой карты (более 5 лет вперед)")
    public void testCashInvalidYearFuture() {
        var paymentPage = open("http://localhost:8080", PaymentPage.class);
        paymentPage.buyWithCash();
        paymentPage.setCardNumber(DataHelper.getCardNumberApproved());
        paymentPage.setCardMonth(DataHelper.getMonth());
        paymentPage.setCardYear("45");
        paymentPage.setCardUser(DataHelper.getUser());
        paymentPage.setCardCVC(DataHelper.getCvc());
        paymentPage.clickContinueButton();
        paymentPage.invalidCardExpirationDate();
        assertEquals(0, DataMySql.getOrderEntityCount());
    }

    @Test
    @DisplayName("13. Попытка оплаты тура с невалидным форматом номера дебетовой карты")
    public void testCashInvalidCardFormat() {
        var paymentPage = open("http://localhost:8080", PaymentPage.class);
        paymentPage.buyWithCash();
        paymentPage.setCardNumber(DataHelper.getCardNumberNotFilled());
        paymentPage.setCardMonth(DataHelper.getMonth());
        paymentPage.setCardYear(DataHelper.getYear());
        paymentPage.setCardUser(DataHelper.getUser());
        paymentPage.setCardCVC(DataHelper.getCvc());
        paymentPage.clickContinueButton();
        paymentPage.incorrectFormat();
        assertEquals(0, DataMySql.getOrderEntityCount());
    }

    @Test
    @DisplayName("14. Попытка оплаты тура с невалидным форматом имени владельца дебетовой карты")
    public void testCashInvalidOwnerFormat() {
        var paymentPage = open("http://localhost:8080", PaymentPage.class);
        paymentPage.buyWithCash();
        paymentPage.setCardNumber(DataHelper.getCardNumberApproved());
        paymentPage.setCardUser ("Ivanov Ivan Ivan");
        paymentPage.setCardMonth(DataHelper.getMonth());
        paymentPage.setCardYear(DataHelper.getYear());
        paymentPage.setCardCVC(DataHelper.getCvc());
        paymentPage.clickContinueButton();
        paymentPage.incorrectFormat();
        assertEquals(0, DataMySql.getOrderEntityCount());
    }

    @Test
    @DisplayName("15. Попытка оплаты тура с невалидным форматом срока действия дебетовой карты")
    public void testCashInvalidExpirationFormat() {
        var paymentPage = open("http://localhost:8080", PaymentPage.class);
        paymentPage.buyWithCash();
        paymentPage.setCardNumber(DataHelper.getCardNumberApproved());
        paymentPage.setCardMonth(DataHelper.getMonth());
        paymentPage.setCardYear(DataHelper.getCurrentYearPlus6());
        paymentPage.setCardUser(DataHelper.getUser());
        paymentPage.setCardCVC(DataHelper.getCvc());
        paymentPage.clickContinueButton();
        paymentPage.invalidCardExpirationDate();
        assertEquals(0, DataMySql.getOrderEntityCount());
    }

    @Test
    @DisplayName("16. Попытка оплаты тура с невалидным форматом CVV кода дебетовой карты")
    public void testCashInvalidCvcFormat() {
        var paymentPage = open("http://localhost:8080", PaymentPage.class);
        paymentPage.buyWithCash();
        paymentPage.setCardNumber(DataHelper.getCardNumberApproved());
        paymentPage.setCardMonth(DataHelper.getMonth());
        paymentPage.setCardYear(DataHelper.getYear());
        paymentPage.setCardUser(DataHelper.getUser());
        paymentPage.setCardCVC("12");
        paymentPage.clickContinueButton();
        paymentPage.incorrectFormat();
        assertEquals(0, DataMySql.getOrderEntityCount());
    }

    @Test
    @DisplayName("17. Попытка оплаты тура с невалидными данными владельца дебетовой карты")
    public void testCashInvalidOwnerSpecialCharacters() {
        var paymentPage = open("http://localhost:8080", PaymentPage.class);
        paymentPage.buyWithCash();
        paymentPage.setCardNumber(DataHelper.getCardNumberApproved());
        paymentPage.setCardUser ("%;%:?*(@!$");
        paymentPage.setCardMonth(DataHelper.getMonth());
        paymentPage.setCardYear(DataHelper.getYear());
        paymentPage.setCardCVC(DataHelper.getCvc());
        paymentPage.clickContinueButton();
        paymentPage.incorrectFormat();
        assertEquals(0, DataMySql.getOrderEntityCount());
    }

    @Test
    @DisplayName("18. Попытка оплаты тура с невалидными данными владельца дебетовой карты (цифра)")
    public void testCashInvalidOwnerNumber() {
        var paymentPage = open("http://localhost:8080", PaymentPage.class);
        paymentPage.buyWithCash();
        paymentPage.setCardNumber(DataHelper.getCardNumberApproved());
        paymentPage.setCardUser ("1");
        paymentPage.setCardMonth(DataHelper.getMonth());
        paymentPage.setCardYear(DataHelper.getYear());
        paymentPage.setCardCVC(DataHelper.getCvc());
        paymentPage.clickContinueButton();
        paymentPage.incorrectFormat();
        assertEquals(0, DataMySql.getOrderEntityCount());
    }

    @Test
    @DisplayName("19. Попытка оплаты тура с невалидными данными владельца дебетовой карты (кириллица 1 буква)")
    public void testCashInvalidOwnerCyrillic() {
        var paymentPage = open("http://localhost:8080", PaymentPage.class);
        paymentPage.buyWithCash();
        paymentPage.setCardNumber(DataHelper.getCardNumberApproved());
        paymentPage.setCardUser ("А");
        paymentPage.setCardMonth(DataHelper.getMonth());
        paymentPage.setCardYear(DataHelper.getYear());
        paymentPage.setCardCVC(DataHelper.getCvc());
        paymentPage.clickContinueButton();
        paymentPage.incorrectFormat();
        assertEquals(0, DataMySql.getOrderEntityCount());
    }

    @Test
    @DisplayName("20. Попытка оплаты тура с не заполненными полями формы для дебетовой карты")
    public void testCashEmptyFields() {
        var paymentPage = open("http://localhost:8080", PaymentPage.class);
        paymentPage.buyWithCash();
        paymentPage.setCardNumber(DataHelper.getCardNumberEmpty());
        paymentPage.setCardMonth(DataHelper.getEmptyMonth());
        paymentPage.setCardYear(DataHelper.getEmptyYear());
        paymentPage.setCardUser(DataHelper.getEmptyUser());
        paymentPage.setCardCVC(DataHelper.getEmptyCvc());
        paymentPage.clickContinueButton();
        paymentPage.incorrectFormat();
        assertEquals(0, DataMySql.getOrderEntityCount());
    }

    // Позитивные сценарии (оплата тура кредитной картой)

    @Test
    @DisplayName("1. Оплата тура с валидной кредитной картой")
    public void testCreditValidCard() {
        var paymentPage = open("http://localhost:8080", PaymentPage.class);
        paymentPage.buyInCredit();
        paymentPage.setCardNumber(DataHelper.getCardNumberApproved());
        paymentPage.setCardMonth(DataHelper.getMonth());
        paymentPage.setCardYear(DataHelper.getYear());
        paymentPage.setCardUser(DataHelper.getUser());
        paymentPage.setCardCVC(DataHelper.getCvc());
        paymentPage.clickContinueButton();
        paymentPage.messageSuccess();
        assertEquals("APPROVED", DataMySql.findCreditStatus());
    }

    @Test
    @DisplayName("2. Повторная оплата тура с валидной кредитной картой")
    public void testRepeatCreditValidCard() {
        testCreditValidCard();
    }

    @Test
    @DisplayName("3. Оплата тура с кредитной картой с достаточным лимитом")
    public void testCreditCardWithSufficientLimit() {
        var paymentPage = open("http://localhost:8080", PaymentPage.class);
        paymentPage.buyInCredit();
        paymentPage.setCardNumber(DataHelper.getCardNumberApproved());
        paymentPage.setCardUser (DataHelper.getUser ());
        paymentPage.setCardMonth(DataHelper.getMonth());
        paymentPage.setCardYear(DataHelper.getYear());
        paymentPage.setCardCVC(DataHelper.getCvc());
        paymentPage.clickContinueButton();
        paymentPage.messageSuccess();
        assertEquals("APPROVED", DataMySql.findCreditStatus());
    }

    @Test
    @DisplayName("4. Оплата тура с кредитной картой с недостаточным лимитом, но с возможностью превышения лимита")
    public void testCreditCardWithInsufficientLimitButPossible() {
        var paymentPage = open("http://localhost:8080", PaymentPage.class);
        paymentPage.buyInCredit();
        paymentPage.setCardNumber(DataHelper.getCardNumberApproved());
        paymentPage.setCardUser (DataHelper.getUser ());
        paymentPage.setCardMonth(DataHelper.getMonth());
        paymentPage.setCardYear(DataHelper.getYear());
        paymentPage.setCardCVC(DataHelper.getCvc());
        paymentPage.clickContinueButton();
        paymentPage.messageSuccess();
        assertEquals("APPROVED", DataMySql.findCreditStatus());
    }

    @Test
    @DisplayName("5. Оплата тура с кредитной картой с ограничениями на проведение операций")
    public void testCreditCardWithOperationRestrictions() {
        var paymentPage = open("http://localhost:8080", PaymentPage.class);
        paymentPage.buyInCredit();
        paymentPage.setCardNumber(DataHelper.getCardNumberApproved());
        paymentPage.setCardUser (DataHelper.getUser ());
        paymentPage.setCardMonth(DataHelper.getMonth());
        paymentPage.setCardYear(DataHelper.getYear());
        paymentPage.setCardCVC(DataHelper.getCvc());
        paymentPage.clickContinueButton();
        paymentPage.messageSuccess();
        assertEquals("APPROVED", DataMySql.findCreditStatus());
    }

    // Негативные сценарии (валидации кредитной карты)

    @Test
    @DisplayName("1. Попытка оплаты тура по несуществующему номеру кредитной карты")
    public void testCreditInvalidCardNumber() {
        var paymentPage = open("http://localhost:8080", PaymentPage.class);
        paymentPage.buyInCredit();
        paymentPage.setCardNumber(DataHelper.getCardNumberNothing());
        paymentPage.setCardMonth(DataHelper.getMonth());
        paymentPage.setCardYear(DataHelper.getYear());
        paymentPage.setCardUser(DataHelper.getUser());
        paymentPage.setCardCVC(DataHelper.getCvc());
        paymentPage.clickContinueButton();
        paymentPage.messageError();
        assertEquals(0, DataMySql.getOrderEntityCount());
    }

    @Test
    @DisplayName("2. Попытка оплаты тура с не полностью заполненным номером кредитной карты")
    public void testCreditCardNotFilled() {
        var paymentPage = open("http://localhost:8080", PaymentPage.class);
        paymentPage.buyInCredit();
        paymentPage.setCardNumber("444444444444444");
        paymentPage.setCardMonth(DataHelper.getMonth());
        paymentPage.setCardYear(DataHelper.getYear());
        paymentPage.setCardUser(DataHelper.getUser());
        paymentPage.setCardCVC(DataHelper.getCvc());
        paymentPage.clickContinueButton();
        paymentPage.incorrectFormat();
        assertEquals(0, DataMySql.getOrderEntityCount());
    }

    @Test
    @DisplayName("3. Попытка оплаты тура с невалидными данными владельца кредитной карты")
    public void testCreditInvalidOwnerName() {
        var paymentPage = open("http://localhost:8080", PaymentPage.class);
        paymentPage.buyInCredit();
        paymentPage.setCardNumber(DataHelper.getCardNumberApproved());
        paymentPage.setCardUser ("Иванов Иван");
        paymentPage.setCardMonth(DataHelper.getMonth());
        paymentPage.setCardYear(DataHelper.getYear());
        paymentPage.setCardCVC(DataHelper.getCvc());
        paymentPage.clickContinueButton();
        paymentPage.incorrectFormat();
        assertEquals(0, DataMySql.getOrderEntityCount());
    }

    @Test
    @DisplayName("4. Попытка оплаты тура с невалидным сроком действия кредитной карты")
    public void testCreditInvalidExpirationDate() {
        var paymentPage = open("http://localhost:8080", PaymentPage.class);
        paymentPage.buyInCredit();
        paymentPage.setCardNumber(DataHelper.getCardNumberApproved());
        paymentPage.setCardMonth("13");
        paymentPage.setCardYear(DataHelper.getYear());
        paymentPage.setCardUser (DataHelper.getUser ());
        paymentPage.setCardCVC(DataHelper.getCvc());
        paymentPage.clickContinueButton();
        paymentPage.invalidCardExpirationDate();
        assertEquals(0, DataMySql.getOrderEntityCount());
    }

    @Test
    @DisplayName("5. Попытка оплаты тура с невалидным CVV кодом кредитной карты")
    public void testCreditInvalidCvcCode() {
        var paymentPage = open("http://localhost:8080", PaymentPage.class);
        paymentPage.buyInCredit();
        paymentPage.setCardNumber(DataHelper.getCardNumberApproved());
        paymentPage.setCardMonth(DataHelper.getMonth());
        paymentPage.setCardYear(DataHelper.getYear());
        paymentPage.setCardUser(DataHelper.getUser());
        paymentPage.setCardCVC(DataHelper.get1Cvc());
        paymentPage.clickContinueButton();
        paymentPage.incorrectFormat();
        assertEquals(0, DataMySql.getOrderEntityCount());
    }

    @Test
    @DisplayName("6. Попытка оплаты тура с невалидным месяцем кредитной карты")
    public void testCreditInvalidMonth() {
        var paymentPage = open("http://localhost:8080", PaymentPage.class);
        paymentPage.buyInCredit();
        paymentPage.setCardNumber(DataHelper.getCardNumberApproved());
        paymentPage.setCardMonth(DataHelper.getInvalidMonth());
        paymentPage.setCardYear(DataHelper.getYear());
        paymentPage.setCardUser(DataHelper.getUser());
        paymentPage.setCardCVC(DataHelper.getCvc());
        paymentPage.clickContinueButton();
        paymentPage.invalidCardExpirationDate();
        assertEquals(0, DataMySql.getOrderEntityCount());
    }

    @Test
    @DisplayName("7. Попытка оплаты тура с невалидным годом кредитной карты")
    public void testCreditInvalidYear() {
        var paymentPage = open("http://localhost:8080", PaymentPage.class);
        paymentPage.buyInCredit();
        paymentPage.setCardNumber(DataHelper.getCardNumberApproved());
        paymentPage.setCardMonth(DataHelper.getMonth());
        paymentPage.setCardYear("23");
        paymentPage.setCardUser (DataHelper.getUser ());
        paymentPage.setCardCVC(DataHelper.getCvc());
        paymentPage.clickContinueButton();
        paymentPage.invalidCardExpirationDate();
        assertEquals(0, DataMySql.getOrderEntityCount());
    }

    @Test
    @DisplayName("8. Попытка оплаты тура с заполненным только номером кредитной карты")
    public void testCreditOnlyCardNumber() {
        var paymentPage = open("http://localhost:8080", PaymentPage.class);
        paymentPage.buyInCredit();
        paymentPage.setCardNumber("4444 4444 4444 4442");
        paymentPage.clickContinueButton();
        paymentPage.incorrectFormat();
        assertEquals(0, DataMySql.getOrderEntityCount());
    }

    @Test
    @DisplayName("9. Попытка оплаты тура с заполненным только именем владельца кредитной карты")
    public void testCreditOnlyOwnerName() {
        var paymentPage = open("http://localhost:8080", PaymentPage.class);
        paymentPage.buyInCredit();
        paymentPage.setCardMonth(DataHelper.getEmptyMonth());
        paymentPage.setCardYear(DataHelper.getEmptyYear());
        paymentPage.setCardNumber(DataHelper.getCardNumberEmpty());
        paymentPage.setCardUser ("Ivanov Ivan");
        paymentPage.setCardCVC(DataHelper.getEmptyCvc());
        paymentPage.clickContinueButton();
        paymentPage.incorrectFormat();
        assertEquals(0, DataMySql.getOrderEntityCount());
    }

    @Test
    @DisplayName("10. Попытка оплаты тура с заполненным только сроком действия кредитной карты")
    public void testCreditOnlyExpirationDate() {
        var paymentPage = open("http://localhost:8080", PaymentPage.class);
        paymentPage.buyInCredit();
        paymentPage.setCardNumber(DataHelper.getCardNumberEmpty());
        paymentPage.setCardMonth(DataHelper.getMonth());
        paymentPage.setCardYear(DataHelper.getYear());
        paymentPage.setCardUser(DataHelper.getEmptyUser());
        paymentPage.setCardCVC(DataHelper.getEmptyCvc());
        paymentPage.clickContinueButton();
        paymentPage.incorrectFormat();
        assertEquals(0, DataMySql.getOrderEntityCount());
    }

    @Test
    @DisplayName("11. Попытка оплаты тура с заполненным только кодом безопасности кредитной карты")
    public void testCreditOnlyCvc() {
        var paymentPage = open("http://localhost:8080", PaymentPage.class);
        paymentPage.buyInCredit();
        paymentPage.setCardNumber(DataHelper.getCardNumberEmpty());
        paymentPage.setCardMonth(DataHelper.getEmptyMonth());
        paymentPage.setCardYear(DataHelper.getEmptyYear());
        paymentPage.setCardUser(DataHelper.getEmptyUser());
        paymentPage.setCardCVC("123");
        paymentPage.clickContinueButton();
        paymentPage.incorrectFormat();
        assertEquals(0, DataMySql.getOrderEntityCount());
    }

    @Test
    @DisplayName("12. Попытка оплаты тура с невалидным годом кредитной карты (более 5 лет вперед)")
    public void testCreditInvalidYearFuture() {
        var paymentPage = open("http://localhost:8080", PaymentPage.class);
        paymentPage.buyInCredit();
        paymentPage.setCardNumber(DataHelper.getCardNumberApproved());
        paymentPage.setCardMonth(DataHelper.getMonth());
        paymentPage.setCardYear("45");
        paymentPage.setCardUser(DataHelper.getUser());
        paymentPage.setCardCVC(DataHelper.getCvc());
        paymentPage.clickContinueButton();
        paymentPage.invalidCardExpirationDate();
        assertEquals(0, DataMySql.getOrderEntityCount());
    }

    @Test
    @DisplayName("13. Попытка оплаты тура с невалидным форматом номера кредитной карты")
    public void testCreditInvalidCardFormat() {
        var paymentPage = open("http://localhost:8080", PaymentPage.class);
        paymentPage.buyInCredit();
        paymentPage.setCardNumber(DataHelper.getCardNumberNotFilled());
        paymentPage.setCardMonth(DataHelper.getMonth());
        paymentPage.setCardYear(DataHelper.getYear());
        paymentPage.setCardUser(DataHelper.getUser());
        paymentPage.setCardCVC(DataHelper.getCvc());
        paymentPage.clickContinueButton();
        paymentPage.incorrectFormat();
        assertEquals(0, DataMySql.getOrderEntityCount());
    }

    @Test
    @DisplayName("14. Попытка оплаты тура с невалидным форматом имени владельца кредитной карты")
    public void testCreditInvalidOwnerFormat() {
        var paymentPage = open("http://localhost:8080", PaymentPage.class);
        paymentPage.buyInCredit();
        paymentPage.setCardNumber(DataHelper.getCardNumberApproved());
        paymentPage.setCardUser ("Ivanov Ivan Ivan");
        paymentPage.setCardMonth(DataHelper.getMonth());
        paymentPage.setCardYear(DataHelper.getYear());
        paymentPage.setCardCVC(DataHelper.getCvc());
        paymentPage.clickContinueButton();
        paymentPage.incorrectFormat();
        assertEquals(0, DataMySql.getOrderEntityCount());
    }

    @Test
    @DisplayName("15. Попытка оплаты тура с невалидным форматом срока действия кредитной карты")
    public void testCreditInvalidExpirationFormat() {
        var paymentPage = open("http://localhost:8080", PaymentPage.class);
        paymentPage.buyInCredit();
        paymentPage.setCardNumber(DataHelper.getCardNumberApproved());
        paymentPage.setCardMonth(DataHelper.getMonth());
        paymentPage.setCardYear(DataHelper.getCurrentYearPlus6());
        paymentPage.setCardUser(DataHelper.getUser());
        paymentPage.setCardCVC(DataHelper.getCvc());
        paymentPage.clickContinueButton();
        paymentPage.invalidCardExpirationDate();
        assertEquals(0, DataMySql.getOrderEntityCount());
    }

    @Test
    @DisplayName("16. Попытка оплаты тура с невалидным форматом CVV кода кредитной карты")
    public void testCreditInvalidCvcFormat() {
        var paymentPage = open("http://localhost:8080", PaymentPage.class);
        paymentPage.buyInCredit();
        paymentPage.setCardNumber(DataHelper.getCardNumberApproved());
        paymentPage.setCardMonth(DataHelper.getMonth());
        paymentPage.setCardYear(DataHelper.getYear());
        paymentPage.setCardUser(DataHelper.getUser());
        paymentPage.setCardCVC("12");
        paymentPage.clickContinueButton();
        paymentPage.incorrectFormat();
        assertEquals(0, DataMySql.getOrderEntityCount());
    }

    @Test
    @DisplayName("17. Попытка оплаты тура с невалидными данными владельца кредитной карты")
    public void testCreditInvalidOwnerSpecialCharacters() {
        var paymentPage = open("http://localhost:8080", PaymentPage.class);
        paymentPage.buyInCredit();
        paymentPage.setCardNumber(DataHelper.getCardNumberApproved());
        paymentPage.setCardUser ("%;%:?*(@!$");
        paymentPage.setCardMonth(DataHelper.getMonth());
        paymentPage.setCardYear(DataHelper.getYear());
        paymentPage.setCardCVC(DataHelper.getCvc());
        paymentPage.clickContinueButton();
        paymentPage.incorrectFormat();
        assertEquals(0, DataMySql.getOrderEntityCount());
    }

    @Test
    @DisplayName("18. Попытка оплаты тура с невалидными данными владельца кредитной карты (цифра)")
    public void testCreditInvalidOwnerNumber() {
        var paymentPage = open("http://localhost:8080", PaymentPage.class);
        paymentPage.buyInCredit();
        paymentPage.setCardNumber(DataHelper.getCardNumberApproved());
        paymentPage.setCardUser ("1");
        paymentPage.setCardMonth(DataHelper.getMonth());
        paymentPage.setCardYear(DataHelper.getYear());
        paymentPage.setCardCVC(DataHelper.getCvc());
        paymentPage.clickContinueButton();
        paymentPage.incorrectFormat();
        assertEquals(0, DataMySql.getOrderEntityCount());
    }

    @Test
    @DisplayName("19. Попытка оплаты тура с невалидными данными владельца кредитной карты (кириллица)")
    public void testCreditInvalidOwnerCyrillic() {
        var paymentPage = open("http://localhost:8080", PaymentPage.class);
        paymentPage.buyInCredit();
        paymentPage.setCardNumber(DataHelper.getCardNumberApproved());
        paymentPage.setCardUser ("А");
        paymentPage.setCardMonth(DataHelper.getMonth());
        paymentPage.setCardYear(DataHelper.getYear());
        paymentPage.setCardCVC(DataHelper.getCvc());
        paymentPage.clickContinueButton();
        paymentPage.incorrectFormat();
        assertEquals(0, DataMySql.getOrderEntityCount());
    }

    @Test
    @DisplayName("20. Попытка оплаты тура с не заполненными полями формы для кредитной карты")
    public void testCreditEmptyFields() {
        var paymentPage = open("http://localhost:8080", PaymentPage.class);
        paymentPage.buyInCredit();
        paymentPage.setCardNumber(DataHelper.getCardNumberEmpty());
        paymentPage.setCardMonth(DataHelper.getEmptyMonth());
        paymentPage.setCardYear(DataHelper.getEmptyYear());
        paymentPage.setCardUser(DataHelper.getEmptyUser());
        paymentPage.setCardCVC(DataHelper.getEmptyCvc());
        paymentPage.clickContinueButton();
        paymentPage.incorrectFormat();
        assertEquals(0, DataMySql.getOrderEntityCount());
    }
}
