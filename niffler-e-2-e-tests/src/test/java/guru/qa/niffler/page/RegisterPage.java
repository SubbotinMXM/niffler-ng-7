package guru.qa.niffler.page;

import com.codeborne.selenide.SelenideElement;

import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$x;

public class RegisterPage {

    private static final String PASSWORDS_NOT_EQUAL_ERROR = "Passwords should be equal";

    private static final String CONGRATULATIONS_REGISTER_TEXT = "Congratulations! You've registered!";

    private final SelenideElement
            usernameInput = $x("//input[@id='username']"),
            passwordInput = $x("//input[@id='password']"),
            passwordSubmitInput = $x("//input[@id='passwordSubmit']"),
            signUpBtn = $x("//button[contains(text(),'Sign Up')]"),
            congratulationsText = $x("//p[text()=\"" + CONGRATULATIONS_REGISTER_TEXT + "\"]"),
            notEqualPasswords = $x("//span[text()='" + PASSWORDS_NOT_EQUAL_ERROR + "']");

    public RegisterPage register(String username, String password, String passwordSubmit) {
        usernameInput.val(username);
        passwordInput.val(password);
        passwordSubmitInput.val(passwordSubmit);
        signUpBtn.click();
        return this;
    }

    public RegisterPage checkRegisterSucceedText() {
        congratulationsText.shouldBe(visible);
        return this;
    }

    public RegisterPage checkRegisterExistingUserError(String username) {
        $x("//span[text()='Username `" + username + "` already exists']");
        return this;
    }

    public RegisterPage checkRegisterNotEqualPasswordsError() {
        notEqualPasswords.shouldBe(visible);
        return this;
    }
}
