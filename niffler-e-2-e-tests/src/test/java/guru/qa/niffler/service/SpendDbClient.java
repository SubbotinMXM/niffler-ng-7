package guru.qa.niffler.service;

import guru.qa.niffler.config.Config;
import guru.qa.niffler.data.Databases;
import guru.qa.niffler.data.dao.impl.CategoryDaoJdbc;
import guru.qa.niffler.data.dao.impl.SpendDaoJdbc;
import guru.qa.niffler.data.entity.spend.CategoryEntity;
import guru.qa.niffler.data.entity.spend.SpendEntity;
import guru.qa.niffler.model.CategoryJson;
import guru.qa.niffler.model.SpendJson;

import java.util.Optional;

import static java.sql.Connection.TRANSACTION_READ_UNCOMMITTED;

public class SpendDbClient {

    private static final Config CFG = Config.getInstance();

    public SpendJson createSpend(SpendJson spendJson){
        return Databases.transaction(
                TRANSACTION_READ_UNCOMMITTED, connection -> {
                    SpendEntity spendEntity = SpendEntity.fromJson(spendJson);
                    if(spendEntity.getCategory().getId() == null){
                        CategoryEntity categoryEntity = new CategoryDaoJdbc(connection).create(spendEntity.getCategory());
                        spendEntity.setCategory(categoryEntity);
                    }
                    return SpendJson.fromEntity(
                            new SpendDaoJdbc(connection).create(spendEntity)
                    );
                }, CFG.spendJdbcUrl()
        );
    }

    public CategoryJson createCategory(CategoryJson categoryJson) {
        return Databases.transaction(
                TRANSACTION_READ_UNCOMMITTED, connection -> {
                    CategoryEntity categoryEntity = CategoryEntity.fromJson(categoryJson);
                    return CategoryJson.fromEntity(
                            new CategoryDaoJdbc(connection).create(categoryEntity)
                    );
                }, CFG.spendJdbcUrl()
        );
    }

    public void deleteCategory(CategoryJson categoryJson) {
        Databases.transaction(
                TRANSACTION_READ_UNCOMMITTED, connection -> {
                    CategoryEntity categoryEntity = CategoryEntity.fromJson(categoryJson);
                    new CategoryDaoJdbc(connection).deleteCategory(categoryEntity);
                    return null;
                }, CFG.spendJdbcUrl()
        );
    }

    public Optional<CategoryJson> findCategoryByUsernameAndCategoryName(String username, String categoryName) {
        return Databases.transaction(
                TRANSACTION_READ_UNCOMMITTED, connection -> {
                    return new CategoryDaoJdbc(connection)
                            .findCategoryByUsernameAndCategoryName(username, categoryName)
                            .map(CategoryJson::fromEntity);
                }, CFG.spendJdbcUrl()
        );
    }
}
