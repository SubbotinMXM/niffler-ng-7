package guru.qa.niffler.service;

import guru.qa.niffler.config.Config;
import guru.qa.niffler.data.Databases.XaFunction;
import guru.qa.niffler.data.dao.impl.AuthAuthorityDaoJdbc;
import guru.qa.niffler.data.dao.impl.AuthUserDaoJdbc;
import guru.qa.niffler.data.dao.impl.UserdataUserDaoJdbc;
import guru.qa.niffler.data.entity.auth.AuthUserEntity;
import guru.qa.niffler.data.entity.userdata.UserEntity;
import guru.qa.niffler.model.UserJson;

import java.util.Optional;

import static guru.qa.niffler.data.Databases.transaction;
import static guru.qa.niffler.data.Databases.xaTransaction;
import static java.sql.Connection.TRANSACTION_READ_UNCOMMITTED;

public class UserDbClient {

    private static final Config CFG = Config.getInstance();

    public UserJson createUser(UserJson userJson) {

        XaFunction<UserJson> xaAuthF = new XaFunction<>(
                connection -> {
                    //1 - создаем запись в табл user, niffler-auth
                    AuthUserEntity aue = new AuthUserDaoJdbc(connection).createUser(AuthUserEntity.fromJson(userJson));
                    //2 - создаем 2 записи read и write в табл authorities, niffler-auth
                    new AuthAuthorityDaoJdbc(connection).createAuthorities(aue);
                    return UserJson.fromAuthEntity(aue);
                },
                CFG.authJdbcUrl());

        XaFunction<UserJson> xaUserDataF = new XaFunction<>(connection -> {
            //3- создаем запись в табл user, niffler-userdata
            UserEntity ue = new UserdataUserDaoJdbc(connection).createUser(UserEntity.fromJson(userJson));
            return UserJson.fromEntity(ue);
        },
                CFG.userdataJdbcUrl());

        return xaTransaction(TRANSACTION_READ_UNCOMMITTED, xaAuthF, xaUserDataF);
    }

    public Optional<UserEntity> findUserByUsername(String username) {

        return transaction(TRANSACTION_READ_UNCOMMITTED, connection -> {
                    Optional<UserEntity> user = new UserdataUserDaoJdbc(connection)
                            .findByUsername(username);
                    return user;
                },
                CFG.userdataJdbcUrl());
    }
}