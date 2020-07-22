package pl.weljak.expensetrackerrestapiwithjwt.service.category;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.weljak.expensetrackerrestapiwithjwt.domain.categories.Category;
import pl.weljak.expensetrackerrestapiwithjwt.domain.categories.PostgresCategoryRepository;
import pl.weljak.expensetrackerrestapiwithjwt.domain.user.EtUser;
import pl.weljak.expensetrackerrestapiwithjwt.domain.user.PostgresEtUserRepository;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class PostgresCategoryService implements CategoryService {
    private final PostgresCategoryRepository categoryRepository;
    private final PostgresEtUserRepository userRepo;

    @Override
    @Transactional
    public Category createCategory(EtUser etUser, String title, String description) {
        log.info("Creating new category for user: {}, title: {}, desc:{}", etUser.getUserId(), title, description);
        Category category = new Category(UUID.randomUUID().toString(), etUser, title, description, Collections.emptyList());
        categoryRepository.save(category);
        return category;
    }

    @Override
    public Category findCategoryByCategoryId(String categoryId) {
        log.info("Fetching category with id: {}", categoryId);
        return categoryRepository.findByCategoryId(categoryId);
    }

    @Override
    public List<Category> findAllUserCategories(EtUser etUser) {
        log.info("Getting all user's categories for user with id: {}", etUser.getUserId());
        return categoryRepository.findAllByEtUserId(etUser);
    }

    @Override
    @Transactional
    public Category updateCategory(String categoryId, String title, String description) {
        log.info("Updating category: {}", categoryId);
        Category toUpdate = categoryRepository.findByCategoryId(categoryId);
        toUpdate.setTitle(Optional.ofNullable(title).orElseGet(toUpdate::getTitle));
        toUpdate.setDescription(Optional.ofNullable(description).orElseGet(toUpdate::getDescription));
        categoryRepository.save(toUpdate);
        return toUpdate;
    }
}
