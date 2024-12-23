package guru.qa.niffler.test.web;

import com.codeborne.selenide.Selenide;
import guru.qa.niffler.config.Config;
import guru.qa.niffler.jupiter.BrowserExtension;
import guru.qa.niffler.jupiter.Category;
import guru.qa.niffler.model.CategoryJson;
import guru.qa.niffler.page.LoginPage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(BrowserExtension.class)
public class ProfileTest {

    private static final Config CFG = Config.getInstance();

    @Category(
            username = "MAKSIM",
            archived = false
    )
    @Test
    void archivedCategoryShouldPresentInCategoryList(CategoryJson categoryJson){
        Selenide.open(CFG.frontUrl(), LoginPage.class)
                .login("MAKSIM", "12345")
                .header.goToProfilePage()
                .checkCategoryIsDisplayed(categoryJson.name())
                .archiveCategory(categoryJson.name())
                .checkCategoryIsNotDisplayed(categoryJson.name())
                .clickShowArchivedSwitcher()
                .checkCategoryIsDisplayed(categoryJson.name());
    }

    @Category(
            username = "MAKSIM",
            archived = true
    )
    @Test
    void activeCategoryShouldPresentInCategoryList(CategoryJson categoryJson){
        Selenide.open(CFG.frontUrl(), LoginPage.class)
                .login("MAKSIM", "12345")
                .header.goToProfilePage()
                .checkCategoryIsNotDisplayed(categoryJson.name())
                .clickShowArchivedSwitcher()
                .checkCategoryIsDisplayed(categoryJson.name())
                .unarchiveCategory(categoryJson.name())
                .clickShowArchivedSwitcher()
                .checkCategoryIsDisplayed(categoryJson.name());;
    }

}
