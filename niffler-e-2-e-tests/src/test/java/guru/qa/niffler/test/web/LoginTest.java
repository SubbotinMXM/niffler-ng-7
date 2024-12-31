package guru.qa.niffler.test.web;

import com.codeborne.selenide.Selenide;
import guru.qa.niffler.config.Config;
import guru.qa.niffler.jupiter.annotation.DisabledByIssue;
import guru.qa.niffler.jupiter.annotation.meta.WebTest;
import guru.qa.niffler.page.LoginPage;
import org.junit.jupiter.api.Test;

@WebTest
public class LoginTest {

    private static final Config CFG = Config.getInstance();

    @Test
    void mainPageShouldBeDisplayedAfterSuccessLogin(){
        Selenide.open(CFG.frontUrl(), LoginPage.class)
                .login("MAKSIM", "12345")
                .checkHeadersLoaded();
    }

    @DisabledByIssue("2") //Тест не запустится, тк на моем гитхабе есть issue с номером 2. Соответственно отрабатывает IssueExtension
    // Если эту issue на гитхабе закрыть, то тест снова начнет запускаться, даже если не убирать аннотацию
    @Test
    void userShouldStayOnLoginPageAfterLoginWithBadCredentials(){
        Selenide.open(CFG.frontUrl(), LoginPage.class)
                .loginWithBaCreds("MAKSIM", "123456")
                .checkBadCredsError();
    }
}
