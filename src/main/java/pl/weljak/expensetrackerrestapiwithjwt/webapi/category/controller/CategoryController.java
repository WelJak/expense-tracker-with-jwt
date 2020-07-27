package pl.weljak.expensetrackerrestapiwithjwt.webapi.category.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import pl.weljak.expensetrackerrestapiwithjwt.domain.categories.Category;
import pl.weljak.expensetrackerrestapiwithjwt.security.userdetails.UserPrinciple;
import pl.weljak.expensetrackerrestapiwithjwt.service.category.CategoryService;
import pl.weljak.expensetrackerrestapiwithjwt.utils.Endpoints;
import pl.weljak.expensetrackerrestapiwithjwt.utils.EtResponse;
import pl.weljak.expensetrackerrestapiwithjwt.utils.EtResponseUtils;
import pl.weljak.expensetrackerrestapiwithjwt.webapi.category.request.CreateCategoryRequest;
import pl.weljak.expensetrackerrestapiwithjwt.webapi.category.request.UdpateCategoryRequest;
import pl.weljak.expensetrackerrestapiwithjwt.webapi.category.response.CategoryDetailsResponse;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
public class CategoryController {
    private final CategoryService categoryService;

    @PostMapping(Endpoints.CATEGORY_CREATE_ENDPOINT)
    public ResponseEntity<EtResponse> createCategory(@AuthenticationPrincipal UserPrinciple userPrinciple, @RequestBody CreateCategoryRequest createCategoryRequest) {
        log.info("Creating new category for user: {}", userPrinciple.getEtUserId());
        Category category = categoryService.createCategory(userPrinciple.getCurrentEtUser(), createCategoryRequest.getTitle(), createCategoryRequest.getDescription());
        return EtResponseUtils.success(Endpoints.CATEGORY_CREATE_ENDPOINT, toCategoryDetailsResponse(category), "Category created", HttpStatus.CREATED);
    }

    @GetMapping(Endpoints.CATEGORY_DETAILS_ENDPOINT)
    public ResponseEntity<EtResponse> findCategoryById(@AuthenticationPrincipal UserPrinciple userPrinciple, @PathVariable String id) {
        log.info("Fetching category details for category with id: {} and user with id: {}", id, userPrinciple.getEtUserId());
        Category category = categoryService.findCategoryByCategoryId(id);
        return EtResponseUtils.success(Endpoints.CATEGORY_DETAILS_ENDPOINT, toCategoryDetailsResponse(category), "Fetched category details", HttpStatus.OK);
    }

    @GetMapping(Endpoints.CATEGORIES_ENDPOINT)
    public ResponseEntity<EtResponse> findUsersAllCategories(@AuthenticationPrincipal UserPrinciple userPrinciple) {
        log.info("Fetching all categories created by user: {}", userPrinciple.getEtUserId());
        List<Category> categories = categoryService.findAllUserCategories(userPrinciple.getCurrentEtUser());
        return EtResponseUtils.success(Endpoints.CATEGORIES_ENDPOINT, toCategoryDetailsList(categories), "Got all user's categories", HttpStatus.OK);
    }

    @PutMapping(Endpoints.CATEGORY_UPDATE_ENDPOINT)
    public ResponseEntity<EtResponse> updateCategory(@AuthenticationPrincipal UserPrinciple userPrinciple, @RequestBody UdpateCategoryRequest udpateCategoryRequest) {
        Category category = categoryService.updateCategory(udpateCategoryRequest.getId(), udpateCategoryRequest.getTitle(), udpateCategoryRequest.getDescription());
        return EtResponseUtils.success(Endpoints.CATEGORY_UPDATE_ENDPOINT, toCategoryDetailsResponse(category), "Category updated", HttpStatus.CREATED);
    }

    @DeleteMapping(Endpoints.CATEGORY_DELETES_ENDPOING)
    public ResponseEntity<EtResponse> deleteCategoryById(@AuthenticationPrincipal UserPrinciple userPrinciple, @PathVariable String id) {
        log.info("Deleting category with id: {} Commissioned by: {}", id, userPrinciple.getEtUserId());
        categoryService.deleteCategoryById(id);
        return EtResponseUtils.noContent();
    }

    private CategoryDetailsResponse toCategoryDetailsResponse(Category category) {
        return new CategoryDetailsResponse(
                category.getCategoryId(),
                category.getEtUserId().getUserId(),
                category.getTitle(),
                category.getDescription()
        );
    }

    private List<CategoryDetailsResponse> toCategoryDetailsList(List<Category> categories) {
        List<CategoryDetailsResponse> categoryDetailsList = new ArrayList<>();
        for (Category category : categories) {
            categoryDetailsList.add(toCategoryDetailsResponse(category));
        }
        return  categoryDetailsList;
    }
}
