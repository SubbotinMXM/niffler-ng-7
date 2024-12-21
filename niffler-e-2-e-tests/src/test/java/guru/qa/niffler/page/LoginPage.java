package guru.qa.niffler.page;

import com.codeborne.selenide.SelenideElement;

import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$x;


public class LoginPage {

    private static final String BAD_LOGIN_CREDS_ERROR = "Неверные учетные данные пользователя";

    private final SelenideElement
            usernameInput = $("input[name='username']"),
            passwordInput = $("input[name='password']"),
            submitButton = $("button[type='submit']"),
            createNewAccountBtn = $x("//a[@href='/register']"),
            badCredsError = $x("//p[text()='" + BAD_LOGIN_CREDS_ERROR + "']");

    public MainPage login(String username, String password) {
        usernameInput.setValue(username);
        passwordInput.setValue(password);
        submitButton.click();

        return new MainPage();
    }

    public LoginPage loginWithBaCreds(String username, String password) {
        usernameInput.setValue(username);
        passwordInput.setValue(password);
        submitButton.click();

        return this;
    }

    public RegisterPage createNewAccount(){
        createNewAccountBtn.click();
        return new RegisterPage();
    }

    public LoginPage checkBadCredsError(){
        badCredsError.shouldBe(visible);
        return this;
    }
}
