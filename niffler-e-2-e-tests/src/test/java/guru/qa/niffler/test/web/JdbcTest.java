package guru.qa.niffler.test.web;

import guru.qa.niffler.model.CategoryJson;
import guru.qa.niffler.model.CurrencyValues;
import guru.qa.niffler.model.SpendJson;
import guru.qa.niffler.model.UserJson;
import guru.qa.niffler.service.SpendDbClient;
import guru.qa.niffler.service.UserDbClient;
import guru.qa.niffler.util.RandomDataUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.util.Date;

@Disabled
public class JdbcTest {

//    @Test
//    void springJdbcTest() {
//        UserDbClient userDbClient = new UserDbClient();
//        String username = RandomDataUtils.randomUsername();
//        String pw = "12345";
//
//        UserJson user = userDbClient.createUserSpringJdbc(
//                new UserJson(
//                        null,
//                        username,
//                        "First Name",
//                        "Surname",
//                        "Full Name",
//                        CurrencyValues.RUB,
//                        null,
//                        null,
//                        new AuthUserJson(
//                                null,
//                                username,
//                                pw,
//                                true,
//                                true,
//                                true,
//                                true,
//                                null
//                        )
//                )
//        );
//
//        assertEquals(username, user.username());
//        System.out.println(user);
//    }
//
//    @Test
//    void txTest() {
//        SpendDbClient spendDbClient = new SpendDbClient();
//        SpendJson spendJson = spendDbClient.createSpend(
//                new SpendJson(
//                        null,
//                        new Date(),
//                        new CategoryJson(
//                                null,
//                                "cat-name-tx",
//                                "cat-name-ttx",
//                                false
//                        ),
//                        CurrencyValues.RUB,
//                        1000.0,
//                        "spend-name-tx",
//                        "MAKSIM"
//                )
//        );
//
//    }
//
//    @Test
//    void xaTransactionsCorrectDataTest() {
//        UserDbClient userDbClient = new UserDbClient();
//        String username = RandomDataUtils.randomUsername();
//        String pw = RandomDataUtils.randomPassword(3, 12);
//
//        UserJson user = userDbClient.createUser(
//                new UserJson(
//                        null,
//                        username,
//                        "First Name",
//                        "Surname",
//                        "Full Name",
//                        CurrencyValues.RUB,
//                        null,
//                        null,
//                        new AuthUserJson(
//                                null,
//                                username,
//                                pw,
//                                true,
//                                true,
//                                true,
//                                true,
//                                null
//                        )
//                )
//        );
//
//        assertEquals(username, user.username());
//    }
//
//    @Test
//    void xaTransactionsInCorrectDataTest() {
//        UserDbClient userDbClient = new UserDbClient();
//        String username = "incorrectData";
//
//        try {
//            UserJson user = userDbClient.createUser(
//                    new UserJson(
//                            null,
//                            username,
//                            "First Name",
//                            "Surname",
//                            "Full Name",
//                            CurrencyValues.RUB,
//                            null,
//                            null,
//                            new AuthUserJson(
//                                    null,
//                                    username,
//                                    null, //Add password as null to test xaTransactions
//                                    true,
//                                    true,
//                                    true,
//                                    true,
//                                    null
//                            )
//                    )
//            );
//        } catch (IllegalArgumentException e) {
//            //NOP
//        } finally {
//            Assertions.assertFalse(userDbClient.findUserByUsername(username).isPresent());
//        }
//    }
//
//    @Test
//    void springSpendJdbcTest() {
//        SpendDbClient spendDbClient = new SpendDbClient();
//
//        spendDbClient.createSpendSpringJdbc(
//                new SpendJson(
//                        null,
//                        new Date(),
//                        new CategoryJson(
//                                null,
//                                "Fast Food Test",
//                                "MAKSIM",
//                                false
//                        ),
//                        CurrencyValues.RUB,
//                        1800.0,
//                        "Fast Food description",
//                        "MAKSIM"
//                )
//        );
//    }

    @Test
    void jdbcTxTest() {
        UserDbClient userDbClient = new UserDbClient();
        String username = RandomDataUtils.randomUsername();

        UserJson user = userDbClient.createUserJdbcWithTx(
                new UserJson(
                        null,
                        username,
                        null,
                        null,
                        null,
                        CurrencyValues.RUB,
                        null,
                        null,
                        null
                )
        );
        Assertions.assertTrue(userDbClient.findUserByUsername(user.username()).isPresent());
    }

    @Test
    void jdbcWithoutTxTest() {
        UserDbClient userDbClient = new UserDbClient();
        String username = RandomDataUtils.randomUsername();

        UserJson user = userDbClient.createUserJdbcWithoutTx(
                new UserJson(
                        null,
                        username,
                        null,
                        null,
                        null,
                        CurrencyValues.RUB,
                        null,
                        null,
                        null
                )
        );
        Assertions.assertTrue(userDbClient.findUserByUsername(user.username()).isPresent());
    }

    @Test // Работает неправильно, тк без транзакции. Тест падает, но сущность создается. Так и задумано, пример неправильного теста
    void springJdbcWithoutTxTest() {
        UserDbClient userDbClient = new UserDbClient();
        String username = RandomDataUtils.randomUsername();

        UserJson user = userDbClient.createUserSpringJdbcWithoutTx(
                new UserJson(
                        null,
                        username,
                        null,
                        null,
                        null,
                        CurrencyValues.RUB,
                        null,
                        null,
                        null
                )
        );
        Assertions.assertTrue(userDbClient.findUserByUsername(user.username()).isPresent());
    }


    @Test
    void springJdbcTxTest() {
        SpendDbClient spendDbClient = new SpendDbClient();
        String categoryName = RandomDataUtils.randomCategoryName();

        spendDbClient.createSpendSpringJdbc(
                new SpendJson(
                        null,
                        new Date(),
                        new CategoryJson(
                                null,
                                categoryName,
                                "duck",
                                false
                        ),
                        CurrencyValues.RUB,
                        1800.0,
                        "Fast Food description",
                        "duck"
                )
        );
    }

    @Test
    void springChainedManagerWithCorrectDataTest() {
        UserDbClient userDbClient = new UserDbClient();
        String username = RandomDataUtils.randomUsername();

        UserJson user = userDbClient.createUser(
                new UserJson(
                        null,
                        username,
                        null,
                        null,
                        "Chained Manager Positive Test",
                        CurrencyValues.RUB,
                        null,
                        null,
                        null

                ));

        Assertions.assertTrue(userDbClient.findUserByUsername(user.username()).isPresent());
    }

    @Test
    void springChainedManagerWithIncorrectDataTest() {
        UserDbClient userDbClient = new UserDbClient();
        String username = RandomDataUtils.randomUsername();
        UserJson user = userDbClient.createUser(
                new UserJson(
                        //Если передать null в качестве username в UDUserDAOSpringJdbc
                        //ps.setString(1, null);
                        //то при создании пользователя произойдет ошибка, но при этом
                        //будут созданы записи в табл user-auth и authorities-auth
                        //т.е. транзакция не откатилась после ошибки в userdata,
                        //что доказывает невозможность отката внутренней транзакции при сбое во внешней
                        null,
                        username,
                        null,
                        null,
                        "Chained Manager Negative Test",
                        CurrencyValues.RUB,
                        null,
                        null,
                        null

                ));

        System.out.println(user);
    }
}