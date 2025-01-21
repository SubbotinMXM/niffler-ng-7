package guru.qa.niffler.test.web;

import guru.qa.niffler.model.*;
import guru.qa.niffler.service.SpendDbClient;
import guru.qa.niffler.service.UserDbClient;
import guru.qa.niffler.util.RandomDataUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.assertEquals;


public class JdbcTest {

    @Test
    void springJdbcTest() {
        UserDbClient userDbClient = new UserDbClient();
        String username = RandomDataUtils.randomUsername();
        String pw = "12345";

        UserJson user = userDbClient.createUserSpringJdbc(
                new UserJson(
                        null,
                        username,
                        "First Name",
                        "Surname",
                        "Full Name",
                        CurrencyValues.RUB,
                        null,
                        null,
                        new AuthUserJson(
                                null,
                                username,
                                pw,
                                true,
                                true,
                                true,
                                true,
                                null
                        )
                )
        );

        assertEquals(username, user.username());
        System.out.println(user);
    }

    @Test
    void txTest() {
        SpendDbClient spendDbClient = new SpendDbClient();
        SpendJson spendJson = spendDbClient.createSpend(
                new SpendJson(
                        null,
                        new Date(),
                        new CategoryJson(
                                null,
                                "cat-name-tx",
                                "cat-name-ttx",
                                false
                        ),
                        CurrencyValues.RUB,
                        1000.0,
                        "spend-name-tx",
                        "MAKSIM"
                )
        );

    }

    @Test
    void xaTransactionsCorrectDataTest() {
        UserDbClient userDbClient = new UserDbClient();
        String username = RandomDataUtils.randomUsername();
        String pw = RandomDataUtils.randomPassword(3, 12);

        UserJson user = userDbClient.createUser(
                new UserJson(
                        null,
                        username,
                        "First Name",
                        "Surname",
                        "Full Name",
                        CurrencyValues.RUB,
                        null,
                        null,
                        new AuthUserJson(
                                null,
                                username,
                                pw,
                                true,
                                true,
                                true,
                                true,
                                null
                        )
                )
        );

        assertEquals(username, user.username());
    }

    @Test
    void xaTransactionsInCorrectDataTest() {
        UserDbClient userDbClient = new UserDbClient();
        String username = "incorrectData";

        try {
            UserJson user = userDbClient.createUser(
                    new UserJson(
                            null,
                            username,
                            "First Name",
                            "Surname",
                            "Full Name",
                            CurrencyValues.RUB,
                            null,
                            null,
                            new AuthUserJson(
                                    null,
                                    username,
                                    null, //Add password as null to test xaTransactions
                                    true,
                                    true,
                                    true,
                                    true,
                                    null
                            )
                    )
            );
        } catch (IllegalArgumentException e) {
            //NOP
        } finally {
            Assertions.assertFalse(userDbClient.findUserByUsername(username).isPresent());
        }
    }

    @Test
    void springSpendJdbcTest() {
        SpendDbClient spendDbClient = new SpendDbClient();

        spendDbClient.createSpendSpringJdbc(
                new SpendJson(
                        null,
                        new Date(),
                        new CategoryJson(
                                null,
                                "Fast Food Test",
                                "MAKSIM",
                                false
                        ),
                        CurrencyValues.RUB,
                        1800.0,
                        "Fast Food description",
                        "MAKSIM"
                )
        );
    }
}