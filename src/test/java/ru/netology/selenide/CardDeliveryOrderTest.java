package ru.netology.selenide;

import com.codeborne.selenide.Configuration;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.Keys;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selectors.withText;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.open;

public class CardDeliveryOrderTest {

    @BeforeEach
    void openBrowser() {
        open("http://localhost:9999");
    }

    @Test
    void shouldSubmitForm() {
        Configuration.holdBrowserOpen = true;
        $("[data-test-id=city] input").setValue("Челябинск");

        //Создаем строку с датой + 4 дня от текущего
        LocalDateTime localDateTime = (new Date()).toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
        localDateTime = localDateTime.plusDays(4);
        Date currentDatePlusFourDays = Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant());
        DateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");
        String date = dateFormat.format(currentDatePlusFourDays);

        //Очищаем ячейку с датой, после чего заполняем её
        $("[data-test-id=date] input").doubleClick();
        $("[data-test-id=date] input").sendKeys(Keys.BACK_SPACE);
        $("[data-test-id=date] input").setValue(date);

        $("[data-test-id=name] input").setValue("Склодовская-Кюри Мария");
        $("[data-test-id=phone] input").setValue("+79220450812");
        $("[data-test-id=agreement]").click();
        $(".button").click();
        $(withText("Встреча успешно забронирована на")).shouldBe(visible, Duration.ofSeconds(15)).shouldHave(text(date));
    }
}