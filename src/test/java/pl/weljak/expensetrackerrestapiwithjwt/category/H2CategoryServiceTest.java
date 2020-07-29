package pl.weljak.expensetrackerrestapiwithjwt.category;

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
import pl.weljak.expensetrackerrestapiwithjwt.domain.user.EtUser;
import pl.weljak.expensetrackerrestapiwithjwt.domain.user.PostgresEtUserRepository;
import pl.weljak.expensetrackerrestapiwithjwt.service.category.CategoryService;
import pl.weljak.expensetrackerrestapiwithjwt.service.user.EtUserService;

import java.util.List;

@Slf4j
@ActiveProfiles("test")
@SpringBootTest
public class H2CategoryServiceTest {
    @Autowired
    private CategoryService categoryService;

    @Autowired
    private EtUserService userService;

    @Autowired
    private PostgresEtUserRepository userRepository;

    @Autowired
    private PostgresCategoryRepository categoryRepository;

    @BeforeEach
    public void doBeforeEachTest() {
        log.info("Starting new test");
        log.info("Adding new user to database");
        userService.createUser("johnTravolta", "John", "Travolta", "John@john.com", "JohnPass");
    }

    @AfterEach
    public void doAfterEachTest() {
        log.info("Clearing database");
        userRepository.deleteAll();
    }

    @Test
    public void categoryServiceShouldCreateNewCategory() {
        // given
        EtUser user = userService.findEtUserByUsername("johnTravolta");
        Category category = categoryService.createCategory(user, "Category1", "New Category");

        // when
        Category res = categoryService.findCategoryByCategoryId(category.getCategoryId());

        // then
        Assertions.assertEquals("Category1", res.getTitle());
        Assertions.assertEquals("New Category", res.getDescription());
        Assertions.assertEquals("johnTravolta", res.getEtUserId().getUsername());
        Assertions.assertEquals("John", res.getEtUserId().getFirstName());
    }

    @Test
    public void categoryServiceShouldFetchAllUserCategories() {
        // given
        EtUser user = userService.findEtUserByUsername("johnTravolta");
        Category category1 = categoryService.createCategory(user, "Category1", "New Category");
        Category category2 = categoryService.createCategory(user, "Category2", "Newer Category");

        //when
        List<Category> res = categoryService.findAllUserCategories(user);

        // then
        Assertions.assertEquals(2, res.size());
        Assertions.assertEquals("Category1", res.get(0).getTitle());
        Assertions.assertEquals("Category2", res.get(1).getTitle());
        Assertions.assertEquals("johnTravolta", res.get(0).getEtUserId().getUsername());
        Assertions.assertEquals("johnTravolta", res.get(1).getEtUserId().getUsername());
    }

    @Test
    public void categoryServiceShouldUpdateOnlyCategoryTitleWhenOnlyNewTitleIsGiven() {
        // given
        EtUser user = userService.findEtUserByUsername("johnTravolta");
        Category category = categoryService.createCategory(user, "Old category title", "Old category description");

        // when
        categoryService.updateCategory(category.getCategoryId(), "New category title", null);
        Category res = categoryService.findCategoryByCategoryId(category.getCategoryId());

        // then
        Assertions.assertEquals("New category title", res.getTitle());
        Assertions.assertEquals("Old category description", res.getDescription());
        Assertions.assertEquals("johnTravolta", res.getEtUserId().getUsername());
    }

    @Test
    public void categoryServiceShouldUpdateOnlyCategoryDescriptionWhenOnlyNewDescriptionIsGiven() {
        // given
        EtUser user = userService.findEtUserByUsername("johnTravolta");
        Category category = categoryService.createCategory(user, "Old category title", "Old category description");

        // when
        categoryService.updateCategory(category.getCategoryId(), null, "New category description");
        Category res = categoryService.findCategoryByCategoryId(category.getCategoryId());

        // then
        Assertions.assertEquals("Old category title", res.getTitle());
        Assertions.assertEquals("New category description", res.getDescription());
        Assertions.assertEquals("johnTravolta", res.getEtUserId().getUsername());
    }

    @Test
    public void categoryServiceShouldUpdateBothCategoryTitleAndDescriptionWhenBothAreGiven() {
        // given
        EtUser user = userService.findEtUserByUsername("johnTravolta");
        Category category = categoryService.createCategory(user, "Old category title", "Old category description");

        // when
        categoryService.updateCategory(category.getCategoryId(), "New category title", "New category description");
        Category res = categoryService.findCategoryByCategoryId(category.getCategoryId());

        // then
        Assertions.assertEquals("New category title", res.getTitle());
        Assertions.assertEquals("New category description", res.getDescription());
        Assertions.assertEquals("johnTravolta", res.getEtUserId().getUsername());
    }

    @Test
    public void categoryServiceShouldDeleteCategory() {
        // given
        EtUser user = userService.findEtUserByUsername("johnTravolta");
        Category category = categoryService.createCategory(user, "Old category title", "Old category description");

        // when
        categoryService.deleteCategoryById(category.getCategoryId());
        Category deletedCategory = categoryService.findCategoryByCategoryId(category.getCategoryId());

        // then
        Assertions.assertNull(deletedCategory);
    }
}
