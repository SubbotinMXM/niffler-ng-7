package guru.qa.niffler.page;

import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$x;

public class MainPage {

    public HeaderComponent header = new HeaderComponent();

    private final ElementsCollection tableRows = $("#spendings tbody").$$("tr");
    private final SelenideElement
            statisticsHeader = $x("//h2[text()='Statistics']"),
            historyOfSpendingsHeader = $x("//h2[text()='History of Spendings']");

    public EditSpendingPage editSpending(String spendingDescription) {
        tableRows.find(text(spendingDescription)).$("td", 5).click();

        return new EditSpendingPage();
    }

    public void checkThatTableContainsSpending(String spendingDescription) {
        tableRows.find(text(spendingDescription)).shouldBe(visible);
    }

    public void checkHeadersLoaded() {
        statisticsHeader.shouldBe(visible);
        historyOfSpendingsHeader.shouldBe(visible);
    }
}
