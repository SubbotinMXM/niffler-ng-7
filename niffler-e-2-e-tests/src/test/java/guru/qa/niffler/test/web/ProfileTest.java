package guru.qa.niffler.test.web;

import com.codeborne.selenide.Selenide;
import guru.qa.niffler.config.Config;
import guru.qa.niffler.jupiter.annotation.Category;
import guru.qa.niffler.jupiter.annotation.meta.User;
import guru.qa.niffler.jupiter.annotation.meta.WebTest;
import guru.qa.niffler.model.CategoryJson;
import guru.qa.niffler.page.LoginPage;
import org.junit.jupiter.api.Test;

@WebTest
public class ProfileTest {

    private static final Config CFG = Config.getInstance();

    @User(
            username = "MAKSIM",
            categories = @Category(
                    archived = false
            )
    )
    @Test
    void archivedCategoryShouldPresentInCategoryList(CategoryJson categoryJson) {
        Selenide.open(CFG.frontUrl(), LoginPage.class)
                .login("MAKSIM", "12345")
                .header.goToProfilePage()
                .checkCategoryIsDisplayed(categoryJson.name())
                .archiveCategory(categoryJson.name())
                .checkCategoryIsNotDisplayed(categoryJson.name())
                .clickShowArchivedSwitcher()
                .checkCategoryIsDisplayed(categoryJson.name());
    }

    @User(
            username = "MAKSIM",
            categories = @Category(
                    archived = true
            )
    )
    @Test
    void activeCategoryShouldPresentInCategoryList(CategoryJson categoryJson) {
        Selenide.open(CFG.frontUrl(), LoginPage.class)
                .login("MAKSIM", "12345")
                .header.goToProfilePage()
                .checkCategoryIsNotDisplayed(categoryJson.name())
                .clickShowArchivedSwitcher()
                .checkCategoryIsDisplayed(categoryJson.name())
                .unarchiveCategory(categoryJson.name())
                .clickShowArchivedSwitcher()
                .checkCategoryIsDisplayed(categoryJson.name());
    }
}
