package ru.topacademy.test;
import com.codeborne.selenide.logevents.SelenideLogger;
import io.qameta.allure.selenide.AllureSelenide;
import org.junit.jupiter.api.*;
import ru.topacademy.Data.DataSql;
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
        DataSql.clearTables();
    }

    // Сценарии на проверку статуса оплаты тура дебетовой картой

    @Test
    @DisplayName("1. Оплата тура с валидной дебетовой картой со статусом APPROVED")
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
        assertEquals("APPROVED", DataSql.findPayStatus());
    }


    @Test
    @DisplayName("2.Оплата тура с валидной дебетовой картой со статусом DECLINED")
    public void testCashValidCardDeclined() {
        var paymentPage = open("http://localhost:8080",  PaymentPage.class);
        paymentPage.buyWithCash();
        paymentPage.setCardNumber(DataHelper.getCardNumberDeclined());
        paymentPage.setCardMonth(DataHelper.getMonth());
        paymentPage.setCardYear(DataHelper.getYear());
        paymentPage.setCardUser(DataHelper.getUser());
        paymentPage.setCardCVC(DataHelper.getCvc());
        paymentPage.clickContinueButton();
        paymentPage.messageError();
        assertEquals("DECLINED", DataSql.findPayStatus());
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
        assertEquals(0, DataSql.getOrderEntityCount());
    }

    @Test
    @DisplayName("2. Попытка оплаты тура с не полностью заполненным номером дебетовой карты")
    public void testCashCardNotFilled() {
        var paymentPage = open("http://localhost:8080", PaymentPage.class);
        paymentPage.buyWithCash();
        paymentPage.setCardNumber(DataHelper.getCardNumberNotFilled());
        paymentPage.setCardMonth(DataHelper.getMonth());
        paymentPage.setCardYear(DataHelper.getYear());
        paymentPage.setCardUser(DataHelper.getUser());
        paymentPage.setCardCVC(DataHelper.getCvc());
        paymentPage.clickContinueButton();
        paymentPage.incorrectFormat();
        assertEquals(0, DataSql.getOrderEntityCount());
    }

    @Test
    @DisplayName("3. Попытка оплаты тура с невалидными данными владельца дебетовой карты(кириллица)")
    public void testCashInvalidOwnerName() {
        var paymentPage = open("http://localhost:8080", PaymentPage.class);
        paymentPage.buyWithCash();
        paymentPage.setCardNumber(DataHelper.getCardNumberApproved());
        paymentPage.setCardUser (DataHelper.getRussianUser());
        paymentPage.setCardMonth(DataHelper.getMonth());
        paymentPage.setCardYear(DataHelper.getYear());
        paymentPage.setCardCVC(DataHelper.getCvc());
        paymentPage.clickContinueButton();
        paymentPage.requiredField();
        assertEquals(0, DataSql.getOrderEntityCount());
    }

    @Test
    @DisplayName("4. Попытка оплаты тура с невалидным сроком действия дебетовой карты(13 месяц)")
    public void testCashInvalidExpirationDate() {
        var paymentPage = open("http://localhost:8080", PaymentPage.class);
        paymentPage.buyWithCash();
        paymentPage.setCardNumber(DataHelper.getCardNumberApproved());
        paymentPage.setCardMonth(DataHelper.getInvalidMonth13());
        paymentPage.setCardYear(DataHelper.getYear());
        paymentPage.setCardUser(DataHelper.getUser());
        paymentPage.setCardCVC(DataHelper.getCvc());
        paymentPage.clickContinueButton();
        paymentPage.invalidCardExpirationDate();
        assertEquals(0, DataSql.getOrderEntityCount());
    }

    @Test
    @DisplayName("5. Попытка оплаты тура с невалидным CVV кодом дебетовой карты(1 цифра)")
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
        assertEquals(0, DataSql.getOrderEntityCount());
    }

    @Test
    @DisplayName("6. Попытка оплаты тура с невалидным месяцем дебетовой карты(00)")
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
        assertEquals(0, DataSql.getOrderEntityCount());
    }

    @Test
    @DisplayName("7. Попытка оплаты тура с невалидным годом дебетовой карты(истекший)")
    public void testCashInvalidYear() {
        var paymentPage = open("http://localhost:8080", PaymentPage.class);
        paymentPage.buyWithCash();
        paymentPage.setCardNumber(DataHelper.getCardNumberApproved());
        paymentPage.setCardMonth(DataHelper.getMonth());
        paymentPage.setCardYear(DataHelper.getPreviousYear());
        paymentPage.setCardUser (DataHelper.getUser ());
        paymentPage.setCardCVC(DataHelper.getCvc());
        paymentPage.clickContinueButton();
        paymentPage.cardExpired();
        assertEquals(0, DataSql.getOrderEntityCount());
    }

    @Test
    @DisplayName("8. Попытка оплаты тура с не заполненным только номером дебетовой карты")
    public void testCashOnlyCardNumber() {
        var paymentPage = open("http://localhost:8080", PaymentPage.class);
        paymentPage.buyWithCash();
        paymentPage.setCardNumber(DataHelper.getCardNumberEmpty());
        paymentPage.setCardMonth(DataHelper.getMonth());
        paymentPage.setCardYear(DataHelper.getYear());
        paymentPage.setCardUser(DataHelper.getUser());
        paymentPage.setCardCVC(DataHelper.getCvc());
        paymentPage.clickContinueButton();
        paymentPage.incorrectFormat();
        assertEquals(0, DataSql.getOrderEntityCount());
    }

    @Test
    @DisplayName("9. Попытка оплаты тура с не заполненным только именем владельца дебетовой карты")
    public void testCashOnlyOwnerName() {
        var paymentPage = open("http://localhost:8080", PaymentPage.class);
        paymentPage.buyWithCash();
        paymentPage.setCardMonth(DataHelper.getMonth());
        paymentPage.setCardYear(DataHelper.getYear());
        paymentPage.setCardNumber(DataHelper.getCardNumberApproved());
        paymentPage.setCardUser (DataHelper.getEmptyUser());
        paymentPage.setCardCVC(DataHelper.getCvc());
        paymentPage.clickContinueButton();
        paymentPage.requiredField();
        assertEquals(0, DataSql.getOrderEntityCount());
    }

    @Test
    @DisplayName("10. Попытка оплаты тура с не заполненным только сроком действия дебетовой карты(год)")
    public void testCashOnlyExpirationDate() {
        var paymentPage = open("http://localhost:8080", PaymentPage.class);
        paymentPage.buyWithCash();
        paymentPage.setCardNumber(DataHelper.getCardNumberApproved());
        paymentPage.setCardMonth(DataHelper.getMonth());
        paymentPage.setCardYear(DataHelper.getEmptyYear());
        paymentPage.setCardUser(DataHelper.getUser());
        paymentPage.setCardCVC(DataHelper.getCvc());
        paymentPage.clickContinueButton();
        paymentPage.incorrectFormat();
        assertEquals(0, DataSql.getOrderEntityCount());
    }

    @Test
    @DisplayName("11. Попытка оплаты тура с не заполненным только кодом безопасности дебетовой карты")
    public void testCashOnlyCvc() {
        var paymentPage = open("http://localhost:8080", PaymentPage.class);
        paymentPage.buyWithCash();
        paymentPage.setCardNumber(DataHelper.getCardNumberApproved());
        paymentPage.setCardMonth(DataHelper.getMonth());
        paymentPage.setCardYear(DataHelper.getYear());
        paymentPage.setCardUser(DataHelper.getUser());
        paymentPage.setCardCVC(DataHelper.getEmptyCvc());
        paymentPage.clickContinueButton();
        paymentPage.incorrectFormat();
        assertEquals(0, DataSql.getOrderEntityCount());
    }

    @Test
    @DisplayName("12. Попытка оплаты тура с невалидным годом дебетовой карты (более 5 лет вперед)")
    public void testCashInvalidYearFuture() {
        var paymentPage = open("http://localhost:8080", PaymentPage.class);
        paymentPage.buyWithCash();
        paymentPage.setCardNumber(DataHelper.getCardNumberApproved());
        paymentPage.setCardMonth(DataHelper.getMonth());
        paymentPage.setCardYear(DataHelper.getCurrentYearPlus6());
        paymentPage.setCardUser(DataHelper.getUser());
        paymentPage.setCardCVC(DataHelper.getCvc());
        paymentPage.clickContinueButton();
        paymentPage.invalidCardExpirationDate();
        assertEquals(0, DataSql.getOrderEntityCount());
    }



    @Test
    @DisplayName("13. Попытка оплаты тура с невалидным форматом имени владельца дебетовой карты(3 слова)")
    public void testCashInvalidOwnerFormat() {
        var paymentPage = open("http://localhost:8080", PaymentPage.class);
        paymentPage.buyWithCash();
        paymentPage.setCardNumber(DataHelper.getCardNumberApproved());
        paymentPage.setCardUser (DataHelper.getUserFullName());
        paymentPage.setCardMonth(DataHelper.getMonth());
        paymentPage.setCardYear(DataHelper.getYear());
        paymentPage.setCardCVC(DataHelper.getCvc());
        paymentPage.clickContinueButton();
        paymentPage.requiredField();
        assertEquals(0, DataSql.getOrderEntityCount());
    }



    @Test
    @DisplayName("14. Попытка оплаты тура с невалидным форматом CVV кода дебетовой карты(2 цифры)")
    public void testCashInvalidCvcFormat() {
        var paymentPage = open("http://localhost:8080", PaymentPage.class);
        paymentPage.buyWithCash();
        paymentPage.setCardNumber(DataHelper.getCardNumberApproved());
        paymentPage.setCardMonth(DataHelper.getMonth());
        paymentPage.setCardYear(DataHelper.getYear());
        paymentPage.setCardUser(DataHelper.getUser());
        paymentPage.setCardCVC(DataHelper.get2Cvc());
        paymentPage.clickContinueButton();
        paymentPage.incorrectFormat();
        assertEquals(0, DataSql.getOrderEntityCount());
    }

    @Test
    @DisplayName("15. Попытка оплаты тура с невалидными данными владельца дебетовой карты(символы)")
    public void testCashInvalidOwnerSpecialCharacters() {
        var paymentPage = open("http://localhost:8080", PaymentPage.class);
        paymentPage.buyWithCash();
        paymentPage.setCardNumber(DataHelper.getCardNumberApproved());
        paymentPage.setCardUser(DataHelper.getSpecialCharactersUser());
        paymentPage.setCardMonth(DataHelper.getMonth());
        paymentPage.setCardYear(DataHelper.getYear());
        paymentPage.setCardCVC(DataHelper.getCvc());
        paymentPage.clickContinueButton();
        paymentPage.requiredField();
        assertEquals(0, DataSql.getOrderEntityCount());
    }

    @Test
    @DisplayName("16. Попытка оплаты тура с невалидными данными владельца дебетовой карты (цифра)")
    public void testCashInvalidOwnerNumber() {
        var paymentPage = open("http://localhost:8080", PaymentPage.class);
        paymentPage.buyWithCash();
        paymentPage.setCardNumber(DataHelper.getCardNumberApproved());
        paymentPage.setCardUser(DataHelper.getNumberUser());
        paymentPage.setCardMonth(DataHelper.getMonth());
        paymentPage.setCardYear(DataHelper.getYear());
        paymentPage.setCardCVC(DataHelper.getCvc());
        paymentPage.clickContinueButton();
        paymentPage.requiredField();
        assertEquals(0, DataSql.getOrderEntityCount());
    }

    @Test
    @DisplayName("17. Попытка оплаты тура с невалидными данными владельца дебетовой карты (1 буква)")
    public void testCashInvalidOwnerCyrillic() {
        var paymentPage = open("http://localhost:8080", PaymentPage.class);
        paymentPage.buyWithCash();
        paymentPage.setCardNumber(DataHelper.getCardNumberApproved());
        paymentPage.setCardUser (DataHelper.getUser1());
        paymentPage.setCardMonth(DataHelper.getMonth());
        paymentPage.setCardYear(DataHelper.getYear());
        paymentPage.setCardCVC(DataHelper.getCvc());
        paymentPage.clickContinueButton();
        paymentPage.requiredField();
        assertEquals(0, DataSql.getOrderEntityCount());
    }

    @Test
    @DisplayName("18. Попытка оплаты тура с не заполненными полями формы для дебетовой карты")
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
        assertEquals(0, DataSql.getOrderEntityCount());
    }

    // Сценарии на проверку статуса оплаты тура кредитной картой
    @Test
    @DisplayName("1. Оплата тура с валидной кредитной картой со статусом APPROVED")
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
        assertEquals("APPROVED", DataSql.findCreditStatus());
    }

    @Test
    @DisplayName("2.Оплата тура с валидной кредитной картой со статусом DECLINED")
    public void testCreditValidCardDeclined() {
        var pageTour = open("http://localhost:8080", PaymentPage.class);
        pageTour.buyInCredit();
        pageTour.setCardNumber(DataHelper.getCardNumberDeclined());
        pageTour.setCardMonth(DataHelper.getMonth());
        pageTour.setCardYear(DataHelper.getYear());
        pageTour.setCardUser(DataHelper.getUser());
        pageTour.setCardCVC(DataHelper.getCvc());
        pageTour.clickContinueButton();
        pageTour.messageError();
        assertEquals("DECLINED", DataSql.findCreditStatus());
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
        assertEquals(0, DataSql.getOrderEntityCount());
    }

    @Test
    @DisplayName("2. Попытка оплаты тура с не полностью заполненным номером кредитной карты")
    public void testCreditCardNotFilled() {
        var paymentPage = open("http://localhost:8080", PaymentPage.class);
        paymentPage.buyInCredit();
        paymentPage.setCardNumber(DataHelper.getCardNumberNotFilled());
        paymentPage.setCardMonth(DataHelper.getMonth());
        paymentPage.setCardYear(DataHelper.getYear());
        paymentPage.setCardUser(DataHelper.getUser());
        paymentPage.setCardCVC(DataHelper.getCvc());
        paymentPage.clickContinueButton();
        paymentPage.incorrectFormat();
        assertEquals(0, DataSql.getOrderEntityCount());
    }

    @Test
    @DisplayName("3. Попытка оплаты тура с невалидными данными владельца кредитной карты(кириллица)")
    public void testCreditInvalidOwnerName() {
        var paymentPage = open("http://localhost:8080", PaymentPage.class);
        paymentPage.buyInCredit();
        paymentPage.setCardNumber(DataHelper.getCardNumberApproved());
        paymentPage.setCardUser (DataHelper.getRussianUser());
        paymentPage.setCardMonth(DataHelper.getMonth());
        paymentPage.setCardYear(DataHelper.getYear());
        paymentPage.setCardCVC(DataHelper.getCvc());
        paymentPage.clickContinueButton();
        paymentPage.requiredField();
        assertEquals(0, DataSql.getOrderEntityCount());
    }

    @Test
    @DisplayName("4. Попытка оплаты тура с невалидным сроком действия кредитной карты(13 месяц)")
    public void testCreditInvalidExpirationDate() {
        var paymentPage = open("http://localhost:8080", PaymentPage.class);
        paymentPage.buyInCredit();
        paymentPage.setCardNumber(DataHelper.getCardNumberApproved());
        paymentPage.setCardMonth(DataHelper.getInvalidMonth13());
        paymentPage.setCardYear(DataHelper.getYear());
        paymentPage.setCardUser (DataHelper.getUser ());
        paymentPage.setCardCVC(DataHelper.getCvc());
        paymentPage.clickContinueButton();
        paymentPage.invalidCardExpirationDate();
        assertEquals(0, DataSql.getOrderEntityCount());
    }

    @Test
    @DisplayName("5. Попытка оплаты тура с невалидным CVV кодом кредитной карты(1 цифра)")
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
        assertEquals(0, DataSql.getOrderEntityCount());
    }

    @Test
    @DisplayName("6. Попытка оплаты тура с невалидным месяцем кредитной карты(00)")
    public void testCreditInvalidMonth() {
        var paymentPage = open("http://localhost:8080", PaymentPage.class);
        paymentPage.buyInCredit();
        paymentPage.setCardNumber(DataHelper.getCardNumberApproved());
        paymentPage.setCardMonth(DataHelper.getInvalidMonth());
        paymentPage.setCardYear(DataHelper.getYear());
        paymentPage.setCardUser(DataHelper.getUser());
        paymentPage.setCardCVC(DataHelper.getCvc());
        paymentPage.clickContinueButton();
        paymentPage.cardExpired();
        assertEquals(0, DataSql.getOrderEntityCount());
    }

    @Test
    @DisplayName("7. Попытка оплаты тура с невалидным годом кредитной карты(истекший)")
    public void testCreditInvalidYear() {
        var paymentPage = open("http://localhost:8080", PaymentPage.class);
        paymentPage.buyInCredit();
        paymentPage.setCardNumber(DataHelper.getCardNumberApproved());
        paymentPage.setCardMonth(DataHelper.getMonth());
        paymentPage.setCardYear(DataHelper.getPreviousYear());
        paymentPage.setCardUser (DataHelper.getUser ());
        paymentPage.setCardCVC(DataHelper.getCvc());
        paymentPage.clickContinueButton();
        paymentPage.cardExpired();
        assertEquals(0, DataSql.getOrderEntityCount());
    }

    @Test
    @DisplayName("8. Попытка оплаты тура с не заполненным только номером кредитной карты")
    public void testCreditOnlyCardNumber() {
        var paymentPage = open("http://localhost:8080", PaymentPage.class);
        paymentPage.buyInCredit();
        paymentPage.setCardNumber(DataHelper.getCardNumberEmpty());
        paymentPage.setCardMonth(DataHelper.getMonth());
        paymentPage.setCardYear(DataHelper.getYear());
        paymentPage.setCardUser(DataHelper.getUser());
        paymentPage.setCardCVC(DataHelper.getCvc());
        paymentPage.clickContinueButton();
        paymentPage.incorrectFormat();
        assertEquals(0, DataSql.getOrderEntityCount());
    }

    @Test
    @DisplayName("9. Попытка оплаты тура с не заполненным только именем владельца кредитной карты")
    public void testCreditOnlyOwnerName() {
        var paymentPage = open("http://localhost:8080", PaymentPage.class);
        paymentPage.buyInCredit();
        paymentPage.setCardMonth(DataHelper.getMonth());
        paymentPage.setCardYear(DataHelper.getYear());
        paymentPage.setCardNumber(DataHelper.getCardNumberApproved());
        paymentPage.setCardUser (DataHelper.getEmptyUser());
        paymentPage.setCardCVC(DataHelper.getCvc());
        paymentPage.clickContinueButton();
        paymentPage.requiredField();
        assertEquals(0, DataSql.getOrderEntityCount());
    }

    @Test
    @DisplayName("10. Попытка оплаты тура с не заполненным только сроком действия кредитной карты(год)")
    public void testCreditOnlyExpirationDate() {
        var paymentPage = open("http://localhost:8080", PaymentPage.class);
        paymentPage.buyInCredit();
        paymentPage.setCardNumber(DataHelper.getCardNumberApproved());
        paymentPage.setCardMonth(DataHelper.getMonth());
        paymentPage.setCardYear(DataHelper.getEmptyYear());
        paymentPage.setCardUser(DataHelper.getUser());
        paymentPage.setCardCVC(DataHelper.getCvc());
        paymentPage.clickContinueButton();
        paymentPage.incorrectFormat();
        assertEquals(0, DataSql.getOrderEntityCount());
    }

    @Test
    @DisplayName("11. Попытка оплаты тура с не заполненным только кодом безопасности кредитной карты")
    public void testCreditOnlyCvc() {
        var paymentPage = open("http://localhost:8080", PaymentPage.class);
        paymentPage.buyInCredit();
        paymentPage.setCardNumber(DataHelper.getCardNumberApproved());
        paymentPage.setCardMonth(DataHelper.getMonth());
        paymentPage.setCardYear(DataHelper.getYear());
        paymentPage.setCardUser(DataHelper.getUser());
        paymentPage.setCardCVC(DataHelper.getEmptyCvc());
        paymentPage.clickContinueButton();
        paymentPage.incorrectFormat();
        assertEquals(0, DataSql.getOrderEntityCount());
    }

    @Test
    @DisplayName("12. Попытка оплаты тура с невалидным годом кредитной карты (более 5 лет вперед)")
    public void testCreditInvalidYearFuture() {
        var paymentPage = open("http://localhost:8080", PaymentPage.class);
        paymentPage.buyInCredit();
        paymentPage.setCardNumber(DataHelper.getCardNumberApproved());
        paymentPage.setCardMonth(DataHelper.getMonth());
        paymentPage.setCardYear(DataHelper.getCurrentYearPlus6());
        paymentPage.setCardUser(DataHelper.getUser());
        paymentPage.setCardCVC(DataHelper.getCvc());
        paymentPage.clickContinueButton();
        paymentPage.invalidCardExpirationDate();
        assertEquals(0, DataSql.getOrderEntityCount());
    }



    @Test
    @DisplayName("13. Попытка оплаты тура с невалидным форматом имени владельца кредитной карты(3 слова)")
    public void testCreditInvalidOwnerFormat() {
        var paymentPage = open("http://localhost:8080", PaymentPage.class);
        paymentPage.buyInCredit();
        paymentPage.setCardNumber(DataHelper.getCardNumberApproved());
        paymentPage.setCardUser (DataHelper.getUserFullName());
        paymentPage.setCardMonth(DataHelper.getMonth());
        paymentPage.setCardYear(DataHelper.getYear());
        paymentPage.setCardCVC(DataHelper.getCvc());
        paymentPage.clickContinueButton();
        paymentPage.requiredField();
        assertEquals(0, DataSql.getOrderEntityCount());
    }



    @Test
    @DisplayName("14. Попытка оплаты тура с невалидным форматом CVV кода кредитной карты(2 цифры)")
    public void testCreditInvalidCvcFormat() {
        var paymentPage = open("http://localhost:8080", PaymentPage.class);
        paymentPage.buyInCredit();
        paymentPage.setCardNumber(DataHelper.getCardNumberApproved());
        paymentPage.setCardMonth(DataHelper.getMonth());
        paymentPage.setCardYear(DataHelper.getYear());
        paymentPage.setCardUser(DataHelper.getUser());
        paymentPage.setCardCVC(DataHelper.get2Cvc());
        paymentPage.clickContinueButton();
        paymentPage.incorrectFormat();
        assertEquals(0, DataSql.getOrderEntityCount());
    }

    @Test
    @DisplayName("15. Попытка оплаты тура с невалидными данными владельца кредитной карты(символы)")
    public void testCreditInvalidOwnerSpecialCharacters() {
        var paymentPage = open("http://localhost:8080", PaymentPage.class);
        paymentPage.buyInCredit();
        paymentPage.setCardNumber(DataHelper.getCardNumberApproved());
        paymentPage.setCardUser(DataHelper.getSpecialCharactersUser());
        paymentPage.setCardMonth(DataHelper.getMonth());
        paymentPage.setCardYear(DataHelper.getYear());
        paymentPage.setCardCVC(DataHelper.getCvc());
        paymentPage.clickContinueButton();
        paymentPage.requiredField();
        assertEquals(0, DataSql.getOrderEntityCount());
    }

    @Test
    @DisplayName("16. Попытка оплаты тура с невалидными данными владельца кредитной карты (цифра)")
    public void testCreditInvalidOwnerNumber() {
        var paymentPage = open("http://localhost:8080", PaymentPage.class);
        paymentPage.buyInCredit();
        paymentPage.setCardNumber(DataHelper.getCardNumberApproved());
        paymentPage.setCardUser(DataHelper.getNumberUser());
        paymentPage.setCardMonth(DataHelper.getMonth());
        paymentPage.setCardYear(DataHelper.getYear());
        paymentPage.setCardCVC(DataHelper.getCvc());
        paymentPage.clickContinueButton();
        paymentPage.requiredField();
        assertEquals(0, DataSql.getOrderEntityCount());
    }

    @Test
    @DisplayName("17. Попытка оплаты тура с невалидными данными владельца кредитной карты (1 буква)")
    public void testCreditInvalidOwnerCyrillic() {
        var paymentPage = open("http://localhost:8080", PaymentPage.class);
        paymentPage.buyInCredit();
        paymentPage.setCardNumber(DataHelper.getCardNumberApproved());
        paymentPage.setCardUser (DataHelper.getUser1());
        paymentPage.setCardMonth(DataHelper.getMonth());
        paymentPage.setCardYear(DataHelper.getYear());
        paymentPage.setCardCVC(DataHelper.getCvc());
        paymentPage.clickContinueButton();
        paymentPage.requiredField();
        assertEquals(0, DataSql.getOrderEntityCount());
    }

    @Test
    @DisplayName("18. Попытка оплаты тура с не заполненными полями формы для кредитной карты")
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
        assertEquals(0, DataSql.getOrderEntityCount());
    }
}
