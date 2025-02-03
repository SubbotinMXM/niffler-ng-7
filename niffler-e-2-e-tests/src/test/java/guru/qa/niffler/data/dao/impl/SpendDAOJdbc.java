package guru.qa.niffler.data.dao.impl;

import guru.qa.niffler.config.Config;
import guru.qa.niffler.data.dao.SpendDAO;
import guru.qa.niffler.data.entity.spend.CategoryEntity;
import guru.qa.niffler.data.entity.spend.SpendEntity;
import guru.qa.niffler.model.CurrencyValues;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static guru.qa.niffler.data.tpl.Connections.holder;

public class SpendDAOJdbc implements SpendDAO {

    private static final Config CFG = Config.getInstance();

    @Override
    public SpendEntity createSpend(SpendEntity spendEntity) {
        try (PreparedStatement ps = holder(CFG.spendJdbcUrl()).connection().prepareStatement(
                "INSERT INTO spend (username, spend_date, currency, amount, description, category_id) " +
                "VALUES (?, ?, ?, ?, ?, ?)",
                Statement.RETURN_GENERATED_KEYS
        )) {
            ps.setString(1, spendEntity.getUsername());
            ps.setDate(2, spendEntity.getSpendDate());
            ps.setString(3, spendEntity.getCurrency().name());
            ps.setDouble(4, spendEntity.getAmount());
            ps.setString(5, spendEntity.getDescription());
            ps.setObject(6, spendEntity.getCategory().getId());

            ps.executeUpdate();

            final UUID generatedKey;
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    generatedKey = rs.getObject("id", UUID.class);
                } else {
                    throw new SQLException("Can`t find id in ResultSet");
                }
            }
            spendEntity.setId(generatedKey);
            return spendEntity;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Optional<SpendEntity> findSpendById(UUID id) {
        try (PreparedStatement ps = holder(CFG.spendJdbcUrl()).connection().prepareStatement(
                "SELECT * FROM spend WHERE id = ?"
        )) {
            ps.setObject(1, id);
            ps.execute();

            try (ResultSet rs = ps.getResultSet()) {
                if (rs.next()) {
                    SpendEntity se = new SpendEntity();
                    CategoryEntity ce = new CategoryEntity();
                    ce.setId(rs.getObject("category_id", UUID.class));
                    se.setId(rs.getObject("id", UUID.class));
                    se.setUsername(rs.getString("username"));
                    se.setCurrency(CurrencyValues.valueOf(rs.getString("currency")));
                    se.setSpendDate(rs.getDate("spend_date"));
                    se.setAmount(rs.getDouble("amount"));
                    se.setDescription(rs.getString("description"));
                    se.setCategory(ce);
                    return Optional.of(
                            se
                    );
                } else {
                    return Optional.empty();
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<SpendEntity> findAllByUsername(String username) {
        List<SpendEntity> spendEntities = new ArrayList<>();

        try (PreparedStatement ps = holder(CFG.spendJdbcUrl()).connection().prepareStatement(
                "SELECT * FROM spend WHERE username = ?")) {

            ps.setString(1, username);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    SpendEntity se = new SpendEntity();
                    CategoryEntity ce = new CategoryEntity();
                    ce.setId(rs.getObject("category_id", UUID.class));
                    se.setId(rs.getObject("id", UUID.class));
                    se.setUsername(rs.getString("username"));
                    se.setCurrency(CurrencyValues.valueOf(rs.getString("currency")));
                    se.setSpendDate(rs.getDate("spend_date"));
                    se.setAmount(rs.getDouble("amount"));
                    se.setDescription(rs.getString("description"));
                    se.setCategory(ce);
                    spendEntities.add(se);
                }
                return spendEntities;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error fetching spend data", e);
        }
    }

    @Override
    public void deleteSpend(SpendEntity spendEntity) {
        try (PreparedStatement ps = holder(CFG.spendJdbcUrl()).connection().prepareStatement(
                "DELETE FROM spend WHERE id = ?")) {
            ps.setObject(1, spendEntity.getId());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<SpendEntity> findAll() {
        try (PreparedStatement ps = holder(CFG.spendJdbcUrl()).connection().prepareStatement(
                "SELECT * FROM spend"
        )) {

            List<SpendEntity> spending = new ArrayList<>();

            ps.execute();
            try (ResultSet rs = ps.getResultSet()) {
                while (rs.next()) {
                    SpendEntity se = new SpendEntity();

                    se.setId(rs.getObject("id", UUID.class));
                    se.setUsername(rs.getString("username"));
                    se.setSpendDate(rs.getDate("spend_date"));
                    se.setCurrency(CurrencyValues.valueOf(rs.getString("currency")));
                    se.setAmount(rs.getDouble("amount"));
                    se.setDescription(rs.getString("description"));

                    CategoryEntity ce = new CategoryEntity();
                    ce.setId(rs.getObject("category_id", UUID.class));
                    se.setCategory(ce);

                    spending.add(se);
                }
            }
            return spending;

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
