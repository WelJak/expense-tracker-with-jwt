package pl.weljak.expensetrackerrestapiwithjwt.transaction;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import pl.weljak.expensetrackerrestapiwithjwt.domain.categories.Category;
import pl.weljak.expensetrackerrestapiwithjwt.domain.categories.PostgresCategoryRepository;
import pl.weljak.expensetrackerrestapiwithjwt.domain.transactions.PostgresTransactionRepository;
import pl.weljak.expensetrackerrestapiwithjwt.domain.transactions.Transaction;
import pl.weljak.expensetrackerrestapiwithjwt.domain.user.EtUser;
import pl.weljak.expensetrackerrestapiwithjwt.domain.user.PostgresEtUserRepository;
import pl.weljak.expensetrackerrestapiwithjwt.domain.user.UserRole;
import pl.weljak.expensetrackerrestapiwithjwt.service.transaction.TransactionService;

import java.util.Collections;
import java.util.List;

@Slf4j
@ActiveProfiles("test")
@SpringBootTest
public class H2TransactionServiceTest {
    @Autowired
    private PostgresTransactionRepository transactionRepository;

    @Autowired
    private PostgresCategoryRepository categoryRepository;

    @Autowired
    private PostgresEtUserRepository userRepository;

    @Autowired
    private TransactionService transactionService;

    @BeforeEach
    public void doBeforeEachTest() {
        log.info("Starting new test");
        EtUser etUser = new EtUser("testId", "johnTravolta", "John", "Travolta", "john@john.com", "password", UserRole.ROLE_USER, Collections.emptyList());
        userRepository.save(etUser);
        Category category = new Category("catId", etUser, "test title", "test description", Collections.emptyList());
        categoryRepository.save(category);
    }

    @AfterEach
    public void doAfterEachTest() {
        userRepository.deleteAll(); // delete CASCADE
    }

    @Test
    public void transactionServiceShouldCreateNewTransaction() {
        // given
        EtUser etUser = userRepository.findByUserId("testId");
        Category category = categoryRepository.findByCategoryId("catId");

        // when
        Transaction transaction = transactionService.createTransaction(category, etUser, 10000L, "test note");
        Transaction res = transactionService.findTransactionById(transaction.getTransactionId());

        // then
        Assertions.assertNotNull(res);
        Assertions.assertEquals("catId", res.getCategory().getCategoryId());
        Assertions.assertEquals("testId", res.getEtUser().getUserId());
        Assertions.assertEquals(10000L, res.getAmount());
        Assertions.assertEquals("test note", res.getNote());
    }

    @Test
    public void transactionServiceShouldDeleteTransaction() {
        // given
        EtUser etUser = userRepository.findByUserId("testId");
        Category category = categoryRepository.findByCategoryId("catId");
        Transaction transaction = transactionService.createTransaction(category, etUser, 10000L, "test note");

        // when
        transactionService.deleteTransaction(transaction.getTransactionId());
        Transaction res = transactionService.findTransactionById(transaction.getTransactionId());

        // then
        Assertions.assertNull(res);
    }

    @Test
    public void transactionServiceShouldUpdateOnlyTransactionAmountWhenOneIsGiven() {
        // given
        EtUser etUser = userRepository.findByUserId("testId");
        Category category = categoryRepository.findByCategoryId("catId");
        Transaction transaction = transactionService.createTransaction(category, etUser, 10000L, "test note");

        // when
        transactionService.updateTransaction(transaction.getTransactionId(), 20000L, null, null);
        Transaction res = transactionService.findTransactionById(transaction.getTransactionId());

        // then
        Assertions.assertNotNull(res);
        Assertions.assertEquals(20000L, res.getAmount());
        Assertions.assertEquals("test note", res.getNote());
        Assertions.assertEquals(category.getCategoryId(), res.getCategory().getCategoryId());
    }

    @Test
    public void transactionServiceShouldUpdateOnlyTransactionNoteWhenOneIsGiven() {
        // given
        EtUser etUser = userRepository.findByUserId("testId");
        Category category = categoryRepository.findByCategoryId("catId");
        Transaction transaction = transactionService.createTransaction(category, etUser, 10000L, "test note");

        // when
        transactionService.updateTransaction(transaction.getTransactionId(), null, "new test note", null);
        Transaction res = transactionService.findTransactionById(transaction.getTransactionId());

        // then
        Assertions.assertNotNull(res);
        Assertions.assertEquals(10000L, res.getAmount());
        Assertions.assertEquals("new test note", res.getNote());
        Assertions.assertEquals(category.getCategoryId(), res.getCategory().getCategoryId());
    }

    @Test
    public void transactionServiceShouldUpdateOnlyTransactionCategoryWhenOneIsGiven() {
        // given
        EtUser etUser = userRepository.findByUserId("testId");
        Category category = categoryRepository.findByCategoryId("catId");
        Transaction transaction = transactionService.createTransaction(category, etUser, 10000L, "test note");
        Category newCategory = new Category("newCatId", etUser, "new title", "new description", Collections.emptyList());
        categoryRepository.save(newCategory);

        // when
        transactionService.updateTransaction(transaction.getTransactionId(), null, null, newCategory);
        Transaction res = transactionService.findTransactionById(transaction.getTransactionId());

        // then
        Assertions.assertNotNull(res);
        Assertions.assertEquals(10000L, res.getAmount());
        Assertions.assertEquals("test note", res.getNote());
        Assertions.assertEquals("newCatId", res.getCategory().getCategoryId());
    }

    @Test
    public void transactionServiceShouldUpdateOnlyTransactionAmountAndNoteWhenGiven() {
        // given
        EtUser etUser = userRepository.findByUserId("testId");
        Category category = categoryRepository.findByCategoryId("catId");
        Transaction transaction = transactionService.createTransaction(category, etUser, 10000L, "test note");

        // when
        transactionService.updateTransaction(transaction.getTransactionId(), 1337L, "new test note", null);
        Transaction res = transactionService.findTransactionById(transaction.getTransactionId());

        // then
        Assertions.assertNotNull(res);
        Assertions.assertEquals(1337L, res.getAmount());
        Assertions.assertEquals("new test note", res.getNote());
        Assertions.assertEquals(category.getCategoryId(), res.getCategory().getCategoryId());
    }

    @Test
    public void transactionServiceShouldUpdateOnlyTransactionAmountAndCategoryWhenGiven() {
        // given
        EtUser etUser = userRepository.findByUserId("testId");
        Category category = categoryRepository.findByCategoryId("catId");
        Transaction transaction = transactionService.createTransaction(category, etUser, 10000L, "test note");
        Category newCategory = new Category("newCatId", etUser, "new title", "new description", Collections.emptyList());
        categoryRepository.save(newCategory);

        // when
        transactionService.updateTransaction(transaction.getTransactionId(), 1337L, null, newCategory);
        Transaction res = transactionService.findTransactionById(transaction.getTransactionId());

        // then
        Assertions.assertNotNull(res);
        Assertions.assertEquals(1337L, res.getAmount());
        Assertions.assertEquals("test note", res.getNote());
        Assertions.assertEquals("newCatId", res.getCategory().getCategoryId());
    }

    @Test
    public void transactionServiceShouldUpdateOnlyNoteAndCategoryWhenGiven() {
        // given
        EtUser etUser = userRepository.findByUserId("testId");
        Category category = categoryRepository.findByCategoryId("catId");
        Transaction transaction = transactionService.createTransaction(category, etUser, 10000L, "test note");
        Category newCategory = new Category("newCatId", etUser, "new title", "new description", Collections.emptyList());
        categoryRepository.save(newCategory);

        // when
        transactionService.updateTransaction(transaction.getTransactionId(), null, "new test note", newCategory);
        Transaction res = transactionService.findTransactionById(transaction.getTransactionId());

        // then
        Assertions.assertNotNull(res);
        Assertions.assertEquals(10000L, res.getAmount());
        Assertions.assertEquals("new test note", res.getNote());
        Assertions.assertEquals("newCatId", res.getCategory().getCategoryId());
    }

    @Test
    public void transactionServiceShouldUpdateNoteAmountAndCategoryWhenGiven() {
        // given
        EtUser etUser = userRepository.findByUserId("testId");
        Category category = categoryRepository.findByCategoryId("catId");
        Transaction transaction = transactionService.createTransaction(category, etUser, 10000L, "test note");
        Category newCategory = new Category("newCatId", etUser, "new title", "new description", Collections.emptyList());
        categoryRepository.save(newCategory);

        // when
        transactionService.updateTransaction(transaction.getTransactionId(), 1337L, "new test note", newCategory);
        Transaction res = transactionService.findTransactionById(transaction.getTransactionId());

        // then
        Assertions.assertNotNull(res);
        Assertions.assertEquals(1337L, res.getAmount());
        Assertions.assertEquals("new test note", res.getNote());
        Assertions.assertEquals("newCatId", res.getCategory().getCategoryId());
    }

    @Test
    public void transactionServiceShouldFindAllTransactionsByUserId() {
        // given
        EtUser etUser = userRepository.findByUserId("testId");
        Category category = categoryRepository.findByCategoryId("catId");
        Transaction transaction1 = transactionService.createTransaction(category, etUser, 10000L, "test note");
        Transaction transaction2 = transactionService.createTransaction(category, etUser, 20L, "test note 2");
        Category newCategory = new Category("newCatId", etUser, "new title", "new description", Collections.emptyList());
        categoryRepository.save(newCategory);
        Transaction transaction3 = transactionService.createTransaction(newCategory, etUser, 500L, "test note 3");

        // when
        List<Transaction> transactions = transactionService.findAllTransactionsByEtUser(etUser);

        // then
        Assertions.assertEquals(3, transactions.size());

        Assertions.assertEquals("catId", transactions.get(0).getCategory().getCategoryId());
        Assertions.assertEquals("catId", transactions.get(1).getCategory().getCategoryId());
        Assertions.assertEquals("newCatId", transactions.get(2).getCategory().getCategoryId());

        Assertions.assertEquals("testId", transactions.get(0).getEtUser().getUserId());
        Assertions.assertEquals("testId", transactions.get(1).getEtUser().getUserId());
        Assertions.assertEquals("testId", transactions.get(2).getEtUser().getUserId());

        Assertions.assertEquals("test note", transactions.get(0).getNote());
        Assertions.assertEquals("test note 2", transactions.get(1).getNote());
        Assertions.assertEquals("test note 3", transactions.get(2).getNote());

        Assertions.assertEquals(10000L, transactions.get(0).getAmount());
        Assertions.assertEquals(20L, transactions.get(1).getAmount());
        Assertions.assertEquals(500L, transactions.get(2).getAmount());
    }

    @Test
    public void transactionServiceShouldFindAllTransactionByCategory() {
        // given
        EtUser etUser = userRepository.findByUserId("testId");
        Category category = categoryRepository.findByCategoryId("catId");
        Transaction transaction1 = transactionService.createTransaction(category, etUser, 10000L, "test note");
        Transaction transaction2 = transactionService.createTransaction(category, etUser, 20L, "test note 2");
        Category newCategory = new Category("newCatId", etUser, "new title", "new description", Collections.emptyList());
        categoryRepository.save(newCategory);
        Transaction transaction3 = transactionService.createTransaction(newCategory, etUser, 500L, "test note 3");

        // when
        List<Transaction> transactions = transactionService.findAllTransactionByCategory(category);

        // then
        Assertions.assertEquals(2, transactions.size());

        Assertions.assertEquals("catId", transactions.get(0).getCategory().getCategoryId());
        Assertions.assertEquals("catId", transactions.get(1).getCategory().getCategoryId());

        Assertions.assertEquals("testId", transactions.get(0).getEtUser().getUserId());
        Assertions.assertEquals("testId", transactions.get(1).getEtUser().getUserId());

        Assertions.assertEquals("test note", transactions.get(0).getNote());
        Assertions.assertEquals("test note 2", transactions.get(1).getNote());

        Assertions.assertEquals(10000L, transactions.get(0).getAmount());
        Assertions.assertEquals(20L, transactions.get(1).getAmount());

    }
}
