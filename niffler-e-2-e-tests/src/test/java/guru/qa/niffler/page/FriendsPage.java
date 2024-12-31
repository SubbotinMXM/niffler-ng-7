package guru.qa.niffler.page;

import com.codeborne.selenide.SelenideElement;

import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$x;

public class FriendsPage {

    private final SelenideElement
            emptyFriendsTableText = $x("//p[text()='There are no users yet']");

    public FriendsPage checkFriendDisplayedByUsername(String username){
        $x("//p[text()='" + username + "']").shouldBe(visible);
        return this;
    }

    public FriendsPage checkFriendsTableEmpty(){
        emptyFriendsTableText.shouldBe(visible);
        return this;
    }

    public FriendsPage checkIncomeFriendsInvitationByUsername(String username){
        $x("//h2[text()='Friend requests']/..//p[text()='" + username + "']").shouldBe(visible);
        return this;
    }
}
