package pl.weljak.expensetrackerrestapiwithjwt.service.category;

import pl.weljak.expensetrackerrestapiwithjwt.domain.categories.Category;
import pl.weljak.expensetrackerrestapiwithjwt.domain.user.EtUser;

import java.util.List;

public interface CategoryService {
    Category createCategory(EtUser etUser, String title, String description);

    Category findCategoryByCategoryId(String categoryId);

    List<Category> findAllUserCategories(EtUser etUser);

    Category updateCategory(String categoryId, String title, String description);
}
