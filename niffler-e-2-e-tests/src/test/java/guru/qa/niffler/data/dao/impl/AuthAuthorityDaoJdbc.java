package guru.qa.niffler.data.dao.impl;

import guru.qa.niffler.data.dao.AuthAuthorityDao;
import guru.qa.niffler.data.entity.auth.AuthUserEntity;
import guru.qa.niffler.data.entity.auth.AuthorityEntity;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

public class AuthAuthorityDaoJdbc implements AuthAuthorityDao {

    private final Connection connection;

    public AuthAuthorityDaoJdbc(Connection connection) {
        this.connection = connection;
    }

    @Override
    public void createAuthorities(AuthUserEntity authUser) {
        List<AuthorityEntity> userAuthorities = authUser.getAuthorities();
        try (PreparedStatement ps = connection.prepareStatement(
                "INSERT INTO authority (user_id, authority) " +
                "VALUES (?, ?)",
                PreparedStatement.RETURN_GENERATED_KEYS
        )) {
            ps.setObject(1, userAuthorities.getFirst().getUser().getId());
            ps.setString(2, userAuthorities.getFirst().getAuthority().name());
            ps.addBatch();

            ps.setObject(1, userAuthorities.getLast().getUser().getId());
            ps.setString(2, userAuthorities.getLast().getAuthority().name());
            ps.addBatch();
            ps.executeBatch();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}