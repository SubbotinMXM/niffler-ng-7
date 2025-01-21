package guru.qa.niffler.data.dao.impl;

import guru.qa.niffler.data.dao.AuthAuthorityDAO;
import guru.qa.niffler.data.entity.auth.AuthorityEntity;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;

public class AuthAuthorityDAOJdbc implements AuthAuthorityDAO {

    private final Connection connection;

    public AuthAuthorityDAOJdbc(Connection connection) {
        this.connection = connection;
    }

    @Override
    public void createAuthorities(AuthorityEntity... authority) {
        List<AuthorityEntity> userAuthorities = Arrays.stream(authority).toList();
        try (PreparedStatement ps = connection.prepareStatement(
                "INSERT INTO authority (user_id, authority) " +
                "VALUES (?, ?)",
                PreparedStatement.RETURN_GENERATED_KEYS
        )) {
            for (AuthorityEntity auth : userAuthorities) {
                ps.setObject(1, auth.getUser().getId());
                ps.setString(2, auth.getAuthority().name());
                ps.addBatch();
            }
            ps.executeBatch();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}