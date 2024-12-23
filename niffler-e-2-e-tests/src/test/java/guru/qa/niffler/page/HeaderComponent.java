package guru.qa.niffler.page;

import com.codeborne.selenide.SelenideElement;

import static com.codeborne.selenide.Selenide.$x;

public class HeaderComponent {

    private final SelenideElement
            personIconBtn = $x("//*[@data-testid=\"PersonIcon\"]/../.."),
            profileDropdownBtn = $x("//a[@href=\"/profile\"]")
    ;

    public ProfilePage goToProfilePage(){
        personIconBtn.click();
        profileDropdownBtn.click();
        return new ProfilePage();
    }
}
