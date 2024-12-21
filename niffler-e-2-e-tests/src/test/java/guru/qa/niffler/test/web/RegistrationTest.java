package guru.qa.niffler.test.web;

import com.codeborne.selenide.Selenide;
import guru.qa.niffler.config.Config;
import guru.qa.niffler.jupiter.BrowserExtension;
import guru.qa.niffler.page.LoginPage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static guru.qa.niffler.util.DataUtil.generateRandomUsername;

@ExtendWith(BrowserExtension.class)
public class RegistrationTest {

    private static final Config CFG = Config.getInstance();

    @Test
    void shouldRegisterNewUser(){
        Selenide.open(CFG.frontUrl(), LoginPage.class)
                .createNewAccount()
                .register(generateRandomUsername(),  "12345", "12345")
                .checkRegisterSucceedText();
    }

    @Test
    void shouldNotRegisterUserWithExistingUsername(){
        Selenide.open(CFG.frontUrl(), LoginPage.class)
                .createNewAccount()
                .register("MAKSIM", "12345", "12345")
                .checkRegisterExistingUserError("MAKSIM");
    }

    @Test
    void shouldShowErrorIfPasswordAndConfirmPasswordNodEqual(){
        Selenide.open(CFG.frontUrl(), LoginPage.class)
                .createNewAccount()
                .register("MAKSIM", "12345", "123456")
                .checkRegisterNotEqualPasswordsError();
    }
}
