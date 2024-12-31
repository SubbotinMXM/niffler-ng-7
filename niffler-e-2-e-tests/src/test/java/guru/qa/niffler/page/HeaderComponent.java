package guru.qa.niffler.page;

import com.codeborne.selenide.SelenideElement;

import static com.codeborne.selenide.Selenide.$x;

public class HeaderComponent {

    private final SelenideElement
            personIconBtn = $x("//*[@data-testid='PersonIcon']/../.."),
            profileDropdownBtn = $x("//a[@href='/profile']"),
            friendsDropdownBtn = $x("//a[@href='/people/friends']"),
            allPeopleDropdownBtn = $x("//a[@href='/people/all']");

    public ProfilePage goToProfilePage(){
        personIconBtn.click();
        profileDropdownBtn.click();
        return new ProfilePage();
    }

    public FriendsPage goToFriendsPage(){
        personIconBtn.click();
        friendsDropdownBtn.click();
        return new FriendsPage();
    }

    public AllPeople goToAllPeoplePage(){
        personIconBtn.click();
        allPeopleDropdownBtn.click();
        return new AllPeople();
    }
}
