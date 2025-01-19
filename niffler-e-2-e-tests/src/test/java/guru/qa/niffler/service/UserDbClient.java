package guru.qa.niffler.service;

import guru.qa.niffler.config.Config;
import guru.qa.niffler.data.Databases.*;
import guru.qa.niffler.data.dao.impl.*;
import guru.qa.niffler.data.entity.auth.AuthUserEntity;
import guru.qa.niffler.data.entity.auth.Authority;
import guru.qa.niffler.data.entity.auth.AuthorityEntity;
import guru.qa.niffler.data.entity.userdata.UserEntity;
import guru.qa.niffler.model.UserJson;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Arrays;
import java.util.Optional;

import static guru.qa.niffler.data.Databases.*;
import static java.sql.Connection.TRANSACTION_READ_UNCOMMITTED;

public class UserDbClient {

    private static final Config CFG = Config.getInstance();
    private static final PasswordEncoder ENCODER = PasswordEncoderFactories.createDelegatingPasswordEncoder();

    public UserJson createUserSpringJdbc(UserJson user) {
        AuthUserEntity aue = new AuthUserEntity();
        aue.setUsername(user.username());
        aue.setPassword(ENCODER.encode("12345"));
        aue.setEnabled(true);
        aue.setAccountNonLocked(true);
        aue.setAccountNonExpired(true);
        aue.setCredentialsNonExpired(true);

        AuthUserEntity createdAuthUser = new AuthUserDaoSpringJdbc(dataSource(CFG.authJdbcUrl()))
                .createUser(aue);

        AuthorityEntity[] authorityEntities = Arrays.stream(Authority.values()).map(
                e -> {
                    AuthorityEntity ae = new AuthorityEntity();
                    ae.setId(createdAuthUser.getId());
                    ae.setAuthority(e);
                    return ae;
                }
        ).toArray(AuthorityEntity[]::new);

        new AuthAuthorityDaoSpringJdbc(dataSource(CFG.authJdbcUrl()))
                .createAuthorities(authorityEntities);

        return UserJson.fromEntity(new UserdataUserDaoSpringJdbc(dataSource(CFG.userdataJdbcUrl()))
                .createUser(UserEntity.fromJson(user)));
    }

    public UserJson createUser(UserJson userJson) {
        XaFunction<UserJson> xaAuthF = new XaFunction<>(
                connection -> {
                    //1 - создаем запись в табл user, niffler-auth
                    AuthUserEntity aue = new AuthUserDaoJdbc(connection).createUser(AuthUserEntity.fromJson(userJson));
                    //2 - создаем 2 записи read и write в табл authorities, niffler-auth
                    new AuthAuthorityDaoJdbc(connection).createAuthorities(aue.getAuthorities().toArray(new AuthorityEntity[0]));
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