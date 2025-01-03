package guru.qa.niffler.test.web;

import guru.qa.niffler.config.Config;
import guru.qa.niffler.jupiter.annotation.meta.WebTest;
import guru.qa.niffler.page.LoginPage;
import org.junit.jupiter.api.Test;

import static com.codeborne.selenide.Selenide.open;
import static guru.qa.niffler.jupiter.extension.UserQueueExtension.StaticUser;
import static guru.qa.niffler.jupiter.extension.UserQueueExtension.Type.*;
import static guru.qa.niffler.jupiter.extension.UserQueueExtension.UserType;


@WebTest
public class FriendsTest {

    private static final Config CFG = Config.getInstance();

    @Test
    void friendShouldBePresentedInFriendsTable(@UserType(WITH_FRIEND) StaticUser user) {
        open(CFG.frontUrl(), LoginPage.class)
                .login(user.username(), user.password())
                .header.goToFriendsPage()
                .checkFriendDisplayedByUsername(user.friend());
    }

    @Test
    void friendsTableShouldBeEmptyForNewUser(@UserType(EMPTY) StaticUser user) {
        open(CFG.frontUrl(), LoginPage.class)
                .login(user.username(), user.password())
                .header.goToFriendsPage()
                .checkFriendsTableEmpty();
    }

    @Test
    void incomeInvitationShouldBePresentInFriendsTable(@UserType(WITH_INCOME_REQUEST) StaticUser user) {
        open(CFG.frontUrl(), LoginPage.class)
                .login(user.username(), user.password())
                .header.goToFriendsPage()
                .checkIncomeFriendsInvitationByUsername(user.income());
    }

    @Test
    void outcomeInvitationShouldBePresentInAllPeopleTable(@UserType(WITH_OUTCOME_REQUEST) StaticUser user) {
        open(CFG.frontUrl(), LoginPage.class)
                .login(user.username(), user.password())
                .header.goToAllPeoplePage()
                .checkOutcomeInvitationByUsername(user.outcome());
    }
}
