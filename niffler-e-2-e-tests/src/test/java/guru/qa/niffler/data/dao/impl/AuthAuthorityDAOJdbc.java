package guru.qa.niffler.data.dao.impl;

import guru.qa.niffler.config.Config;
import guru.qa.niffler.data.dao.AuthAuthorityDAO;
import guru.qa.niffler.data.entity.auth.AuthorityEntity;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;

import static guru.qa.niffler.data.tpl.Connections.holder;

public class AuthAuthorityDAOJdbc implements AuthAuthorityDAO {

    private static final Config CFG = Config.getInstance();

    @Override
    public void createAuthorities(AuthorityEntity... authority) {
        List<AuthorityEntity> userAuthorities = Arrays.stream(authority).toList();
        try (PreparedStatement ps = holder(CFG.authJdbcUrl()).connection().prepareStatement(
                "INSERT INTO authority (user_id, authority) " +
                "VALUES (?, ?)",
                PreparedStatement.RETURN_GENERATED_KEYS
        )) {
            for (AuthorityEntity auth : userAuthorities) {
                ps.setObject(1, auth.getUserId());
                ps.setString(2, auth.getAuthority().name());
                ps.addBatch();
            }
            ps.executeBatch();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}