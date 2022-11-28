package ru.netology.selenide;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.Configuration;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.Keys;

import java.sql.Timestamp;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

import static com.codeborne.selenide.Selectors.withText;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.open;

public class CardDeliveryOrderTest {

    @BeforeEach
    void openBrowser() {
        open("http://localhost:9999");
    }

    public String generateDate(int days) {
        return LocalDate.now().plusDays(days).format(DateTimeFormatter.ofPattern("dd.MM.yyyy"));
    }

    @Test
    void shouldSubmitForm() {
        Configuration.holdBrowserOpen = true;
        String date = generateDate(4);
        $("[data-test-id=city] input").setValue("Челябинск");

        //Очищаем ячейку с датой, после чего заполняем её
        $("[data-test-id=date] input").doubleClick();
        $("[data-test-id=date] input").sendKeys(Keys.BACK_SPACE);
        $("[data-test-id=date] input").setValue(date);

        $("[data-test-id=name] input").setValue("Склодовская-Кюри Мария");
        $("[data-test-id=phone] input").setValue("+79220450812");
        $("[data-test-id=agreement]").click();
        $(".button").click();
        $(".notification__content")
            .shouldHave(Condition.text("Встреча успешно забронирована на " + date), Duration.ofSeconds(15))
            .shouldBe(Condition.visible);
    }

    @Test
    void shouldInteractWithComplexElements() {
        Configuration.holdBrowserOpen = true;
        int daysToAppointment = 7;
        $("[data-test-id=city] input").setValue("Че");
        $(withText("Челябинск")).click();

        $("[data-test-id=date] button").click();
        long timestampMarkedDate = Long.parseLong($(".calendar__day_state_current").getAttribute("data-day"));
        LocalDate markedDate = Instant.ofEpochMilli(timestampMarkedDate).atZone(ZoneId.systemDefault()).toLocalDate();
        LocalDate appointmentDate = LocalDate.now().plusDays(daysToAppointment);
        if (markedDate.getMonth() != appointmentDate.getMonth()) {
            $(".calendar__arrow_direction_right[data-step='1'][role=button]").click();
        }

        Timestamp timestamp = Timestamp.valueOf(appointmentDate.atStartOfDay());
        long timestampInMilliseconds = timestamp.getTime();
        String attributeValue = Long.toString(timestampInMilliseconds);

        $("[data-test-id=date] button").click();
        $(".calendar__day[data-day='" + attributeValue + "']").click();
        $("[data-test-id=name] input").setValue("Склодовская-Кюри Мария");
        $("[data-test-id=phone] input").setValue("+79220450812");
        $("[data-test-id=agreement]").click();
        $(".button").click();

        String date = generateDate(daysToAppointment);
        $(".notification__content")
            .shouldHave(Condition.text("Встреча успешно забронирована на " + date), Duration.ofSeconds(15))
            .shouldBe(Condition.visible);
    }
}
