package guru.qa.niffler.page;

import static com.codeborne.selenide.Selenide.$x;

public class AllPeople {

    public AllPeople checkOutcomeInvitationByUsername(String username){
        $x("//p[text()='" + username + "']/../../..//span[text()='Waiting...']");
        return this;
    }
}
