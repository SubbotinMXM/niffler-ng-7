package guru.qa.niffler.service;

import guru.qa.niffler.config.Config;
import guru.qa.niffler.data.dao.CategoryDAO;
import guru.qa.niffler.data.dao.SpendDAO;
import guru.qa.niffler.data.dao.impl.CategoryDAOJdbc;
import guru.qa.niffler.data.dao.impl.CategoryDAOSpringJdbc;
import guru.qa.niffler.data.dao.impl.SpendDAOJdbc;
import guru.qa.niffler.data.dao.impl.SpendDAOSpringJdbc;
import guru.qa.niffler.data.entity.spend.CategoryEntity;
import guru.qa.niffler.data.entity.spend.SpendEntity;
import guru.qa.niffler.data.tpl.DataSources;
import guru.qa.niffler.data.tpl.JdbcTransactionTemplate;
import guru.qa.niffler.model.SpendJson;
import org.springframework.jdbc.support.JdbcTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

public class SpendDbClient {

    private static final Config CFG = Config.getInstance();
    private final CategoryDAO categoryDAO = new CategoryDAOJdbc();
    private final SpendDAO spendDao = new SpendDAOJdbc();
    private final CategoryDAO categorySpringDAO = new CategoryDAOSpringJdbc();
    private final SpendDAO spendSpringDao = new SpendDAOSpringJdbc();

    private final JdbcTransactionTemplate jdbcTxTemplate = new JdbcTransactionTemplate(
            CFG.spendJdbcUrl()
    );

    private final TransactionTemplate txTemplate = new TransactionTemplate(
            new JdbcTransactionManager(
                    DataSources.dataSource(CFG.spendJdbcUrl())
            )
    );

    public SpendJson createSpend(SpendJson spend) {
        return jdbcTxTemplate.execute(() -> {
                    SpendEntity spendEntity = SpendEntity.fromJson(spend);

                    if (spendEntity.getCategory().getId() == null) {
                        CategoryEntity categoryEntity = categoryDAO.createCategory(spendEntity.getCategory());
                        spendEntity.setCategory(categoryEntity);
                    }

                    return SpendJson.fromEntity(
                            spendDao.createSpend(spendEntity)
                    );
                }
        );
    }

    public SpendJson createSpendSpringJdbc(SpendJson spendJson) {
        return txTemplate.execute(status -> {
                    CategoryEntity category = categorySpringDAO
                            .createCategory(CategoryEntity.fromJson(spendJson.category()));

                    SpendEntity spendEntity = SpendEntity.fromJson(spendJson);
                    spendEntity.setCategory(category);

                    return SpendJson.fromEntity(spendSpringDao
                            .createSpend(spendEntity));
                }

        );
    }

}