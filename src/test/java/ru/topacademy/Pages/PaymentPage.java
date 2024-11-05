package ru.topacademy.Pages;

import com.codeborne.selenide.SelenideElement;
import com.codeborne.selenide.ex.ElementNotFound;
import org.openqa.selenium.By;

import java.time.Duration;

import static com.codeborne.selenide.Condition.*;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$$;

public class PaymentPage {

    private SelenideElement buyButton = $$(".button__text").find(exactText("Купить"));
    private SelenideElement buyCreditButton = $$(".button__text").find(exactText("Купить в кредит"));
    private SelenideElement cardNumberField = $$(".input__top").find(exactText("Номер карты")).parent().find("input");
    private SelenideElement monthField = $$(".input__inner").findBy(text("Месяц")).$(".input__control");
    private SelenideElement yearField = $$(".input__inner").findBy(text("Год")).$(".input__control");
    private SelenideElement userField = $$(".input__inner").findBy(text("Владелец")).$(".input__control");
    private SelenideElement cvcField = $$(".input__inner").findBy(text("CVC/CVV")).$(".input__control");
    private SelenideElement payCard = $$(".heading").find(exactText("Оплата по карте"));

    private SelenideElement payCreditCard = $$(".heading").find(exactText("Кредит по данным карты"));
    private SelenideElement messageSuccess = $$(".notification__title").find(exactText("Успешно"));
    private SelenideElement messageError = $$(".notification__content").find(exactText("Ошибка! Банк отказал в проведении операции."));

    private SelenideElement continueButton = $$(".button__content").find(text("Продолжить"));
    private SelenideElement cardExpired = $$("span.input__sub").find(exactText("Истёк срок действия карты"));
    private SelenideElement invalidCardExpirationDate = $$("span.input__sub").find(exactText("Неверно указан срок действия карты"));
    private SelenideElement incorrectFormat = $$("span.input__sub").find(exactText("Неверный формат"));
    private SelenideElement requiredField = $$(".input__inner span.input__sub").find(exactText("Поле обязательно для заполнения"));


    // Метод для нажатия кнопки "Купить" и проверки видимости страницы оплаты
    public void buyWithCash() {
        buyButton.click();
        payCard.shouldBe(visible);
    }

    // Метод для нажатия кнопки "Купить в кредит" и проверки видимости страницы кредита
    public void buyInCredit() {
        buyCreditButton.click();
        payCreditCard.shouldBe(visible);
    }

    // Метод для ввода номера карты
    public void setCardNumber(String number) {
        cardNumberField.setValue(number);
    }

    // Метод для ввода месяца карты
    public void setCardMonth(String month) {
        monthField.setValue(month);
    }

    // Метод для ввода года карты
    public void setCardYear(String year) {
        yearField.setValue(year);
    }

    // Метод для ввода имени владельца карты
    public void setCardUser (String user) {
        userField.setValue(user);
    }

    // Метод для ввода CVC/CVV кода карты
    public void setCardCVC(String cvc) {
        cvcField.setValue(cvc);
    }

    // Метод для нажатия кнопки "Продолжить"
    public void clickContinueButton() {
        continueButton.click();
    }

    // Метод для проверки видимости сообщения об успешной операции
    public void messageSuccess() {
        messageSuccess.shouldBe(visible, Duration.ofSeconds(15));
    }

    // Метод для проверки видимости сообщения об ошибке
    public void messageError() {
        messageError.shouldBe(visible, Duration.ofSeconds(15));
    }

    // Метод для проверки видимости сообщения об истекшем сроке действия карты
    public void cardExpired() {
        cardExpired.shouldBe(visible, Duration.ofSeconds(15));
    }

    // Метод для проверки видимости сообщения о неверном сроке действия карты
    public void invalidCardExpirationDate() {
        invalidCardExpirationDate.shouldBe(visible, Duration.ofSeconds(15));
    }

    // Метод для проверки видимости сообщения о неверном формате
    public void incorrectFormat() {
        incorrectFormat.shouldBe(visible, Duration.ofSeconds(15));
    }

    // Метод для проверки видимости сообщения об обязательном поле
    public void requiredField() {
        requiredField.shouldBe(visible, Duration.ofSeconds(15));
    }

    // Метод для получения текста сообщения об ошибке
    public String getErrorMessage() {
        SelenideElement errorMessageElement = $(By.className("error-message"));

        try {
            // Проверяем, виден ли элемент, и возвращаем текст, если он виден
            errorMessageElement.shouldBe(visible, Duration.ofSeconds(15));
            return errorMessageElement.getText();
        } catch (ElementNotFound e) {
            // Если элемент не найден, возвращаем сообщение об ошибке
            return "Сообщение об ошибке не найдено";
        }
    }


    // Метод для заполнения формы платежа и нажатия кнопки "Продолжить"
    public void fillForm(String cardNumber, String month, String year, String owner, String cvv) {
        cardNumberField.setValue(cardNumber);
        monthField.setValue(month);
        yearField.setValue(year);
        userField.setValue(owner);
        cvcField.setValue(cvv);
        continueButton.click();
    }

    // Метод для проверки видимости сообщения об успешной операции
    public void success() {
        messageSuccess.shouldBe(visible, Duration.ofSeconds(15));
    }

}

