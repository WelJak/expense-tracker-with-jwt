package pl.weljak.expensetrackerrestapiwithjwt.category;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import pl.weljak.expensetrackerrestapiwithjwt.domain.categories.Category;
import pl.weljak.expensetrackerrestapiwithjwt.domain.categories.PostgresCategoryRepository;
import pl.weljak.expensetrackerrestapiwithjwt.domain.user.EtUser;
import pl.weljak.expensetrackerrestapiwithjwt.domain.user.PostgresEtUserRepository;
import pl.weljak.expensetrackerrestapiwithjwt.domain.user.UserRole;
import pl.weljak.expensetrackerrestapiwithjwt.service.user.EtUserService;
import pl.weljak.expensetrackerrestapiwithjwt.utils.Endpoints;
import pl.weljak.expensetrackerrestapiwithjwt.webapi.category.request.CreateCategoryRequest;
import pl.weljak.expensetrackerrestapiwithjwt.webapi.category.request.UpdateCategoryRequest;
import pl.weljak.expensetrackerrestapiwithjwt.webapi.category.response.CategoryDetailsResponse;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Slf4j
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class H2CategoryControllerTest {
    @Autowired
    private PostgresCategoryRepository categoryRepository;

    @Autowired
    private PostgresEtUserRepository userRepository;

    @Autowired
    private EtUserService userService;

    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @Autowired
    private MockMvc mockMvc;

    private final Gson gson = new Gson();

    private static final String authHeader = "Authorization";

    private static final String BEARER = "Bearer ";

    @BeforeEach
    public void doBeforeEachTest() {
        log.info("Staring new test");
        EtUser etUser = new EtUser("testId", "johnTravolta", "John", "Travolta", "john@john.com", bCryptPasswordEncoder.encode("user"), UserRole.ROLE_USER, Collections.emptyList());
        userRepository.save(etUser);
        Category category = new Category("catId", etUser, "testTitle", "testDesc", Collections.emptyList());
        categoryRepository.save(category);
    }

    @AfterEach
    public void doAfterEachTest() {
        userRepository.deleteAll();
    }

    @Test
    public void categoryControllerShouldCreateNewCategory() throws Exception {
        // given
        CreateCategoryRequest createCategoryRequest = new CreateCategoryRequest("new category", "description");
        String jwtToken = userService.validateUser("johnTravolta", "user");

        // when
        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.post(Endpoints.CATEGORY_CREATE_ENDPOINT).contentType(MediaType.APPLICATION_JSON).content(gson.toJson(createCategoryRequest)).header(authHeader, BEARER + jwtToken))
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andReturn();

        CategoryDetailsResponse createCategoryResponse = jsonToResponseObject(result, CategoryDetailsResponse.class);

        Category category = categoryRepository.findByCategoryId(createCategoryResponse.getId());

        // then
        Assertions.assertNotNull(category);
        Assertions.assertEquals("johnTravolta", category.getEtUserId().getUsername());
        Assertions.assertEquals("new category", category.getTitle());
        Assertions.assertEquals("description", category.getDescription());
    }

    @Test
    public void categoryControllerShouldFetchCategoryDetails() throws Exception {
        // given
        final String categoryId = "catId";
        final String CATEGORY_DETAILS_ENDPOINT = Endpoints.CATEGORY_DETAILS_ENDPOINT.replace("{id}", categoryId);
        final String jwtToken = userService.validateUser("johnTravolta", "user");

        // when
        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.get(CATEGORY_DETAILS_ENDPOINT).contentType(MediaType.APPLICATION_JSON).header(authHeader, BEARER + jwtToken))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();

        CategoryDetailsResponse categoryDetailsResponse = jsonToResponseObject(result, CategoryDetailsResponse.class);

        // then
        Assertions.assertNotNull(categoryDetailsResponse);
        Assertions.assertEquals("catId", categoryDetailsResponse.getId());
        Assertions.assertEquals("testTitle", categoryDetailsResponse.getTitle());
        Assertions.assertEquals("testDesc", categoryDetailsResponse.getDescription());
        Assertions.assertEquals("testId", categoryDetailsResponse.getUserId());
    }

    @Test
    public void categoryControllerShouldFetchAllUserCategoriesDetails() throws Exception {
        // given
        final String jwtToken = userService.validateUser("johnTravolta", "user");
        EtUser etUser = userService.findEtUserById("testId");
        Category category = new Category("testCat", etUser, "new test title", "new test description", Collections.emptyList());
        categoryRepository.save(category);

        // when
        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.get(Endpoints.CATEGORIES_ENDPOINT).contentType(MediaType.APPLICATION_JSON).header(authHeader, BEARER + jwtToken))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();

        List<CategoryDetailsResponse> categoryDetailsResponses = jsonToResponseObjectList(result, CategoryDetailsResponse.class);

        // then
        Assertions.assertEquals(2, categoryDetailsResponses.size());

        Assertions.assertEquals("catId", categoryDetailsResponses.get(0).getId());
        Assertions.assertEquals("testCat", categoryDetailsResponses.get(1).getId());

        Assertions.assertEquals("testTitle", categoryDetailsResponses.get(0).getTitle());
        Assertions.assertEquals("new test title", categoryDetailsResponses.get(1).getTitle());

        Assertions.assertEquals("testDesc", categoryDetailsResponses.get(0).getDescription());
        Assertions.assertEquals("new test description", categoryDetailsResponses.get(1).getDescription());
    }

    @Test
    public void categoryControllerShouldUpdateOnlyCategoryTitleWhenOnlyOneIsGiven() throws Exception {
        // given
        final String jwtToken = userService.validateUser("johnTravolta", "user");
        final UpdateCategoryRequest updateCategoryRequest = new UpdateCategoryRequest("catId", "newest title", null);

        // when
        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.put(Endpoints.CATEGORY_UPDATE_ENDPOINT).contentType(MediaType.APPLICATION_JSON).content(gson.toJson(updateCategoryRequest)).header(authHeader, BEARER + jwtToken))
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andReturn();

        CategoryDetailsResponse categoryDetailsResponse = jsonToResponseObject(result, CategoryDetailsResponse.class);

        // then
        Assertions.assertNotNull(categoryDetailsResponse);
        Assertions.assertEquals("newest title", categoryDetailsResponse.getTitle());
        Assertions.assertEquals("catId", categoryDetailsResponse.getId());
        Assertions.assertEquals("testDesc", categoryDetailsResponse.getDescription());
    }

    @Test
    public void categoryControllerShouldUpdateOnlyCategoryDescriptionWhenOnlyOneIsGiven() throws Exception {
        // given
        final String jwtToken = userService.validateUser("johnTravolta", "user");
        final UpdateCategoryRequest updateCategoryRequest = new UpdateCategoryRequest("catId", null, "newest description");

        // when
        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.put(Endpoints.CATEGORY_UPDATE_ENDPOINT).contentType(MediaType.APPLICATION_JSON).content(gson.toJson(updateCategoryRequest)).header(authHeader, BEARER + jwtToken))
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andReturn();

        CategoryDetailsResponse categoryDetailsResponse = jsonToResponseObject(result, CategoryDetailsResponse.class);

        // then
        Assertions.assertNotNull(categoryDetailsResponse);
        Assertions.assertEquals("testTitle", categoryDetailsResponse.getTitle());
        Assertions.assertEquals("catId", categoryDetailsResponse.getId());
        Assertions.assertEquals("newest description", categoryDetailsResponse.getDescription());
    }

    @Test
    public void categoryControllerShouldUpdateCategoryTitleAndDescriptionWhenGiven() throws Exception {
        // given
        final String jwtToken = userService.validateUser("johnTravolta", "user");
        final UpdateCategoryRequest updateCategoryRequest = new UpdateCategoryRequest("catId", "newest title", "newest description");

        // when
        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.put(Endpoints.CATEGORY_UPDATE_ENDPOINT).contentType(MediaType.APPLICATION_JSON).content(gson.toJson(updateCategoryRequest)).header(authHeader, BEARER + jwtToken))
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andReturn();

        CategoryDetailsResponse categoryDetailsResponse = jsonToResponseObject(result, CategoryDetailsResponse.class);

        // then
        Assertions.assertNotNull(categoryDetailsResponse);
        Assertions.assertEquals("newest title", categoryDetailsResponse.getTitle());
        Assertions.assertEquals("catId", categoryDetailsResponse.getId());
        Assertions.assertEquals("newest description", categoryDetailsResponse.getDescription());
    }

    @Test
    public void categoryControllerShouldDeleteCategory() throws Exception {
        // given
        final String jwtToken = userService.validateUser("johnTravolta", "user");
        final String categoryId = "catId";
        final String DELETE_CATEGORY_ENDPOINT = Endpoints.CATEGORY_DELETES_ENDPOING.replace("{id}", categoryId);

        // when
        mockMvc.perform(MockMvcRequestBuilders.delete(DELETE_CATEGORY_ENDPOINT).header(authHeader, BEARER + jwtToken))
                .andExpect(MockMvcResultMatchers.status().isNoContent());

        Category res = categoryRepository.findByCategoryId("catId");

        // then
        Assertions.assertNull(res);
    }

    private <T> T jsonToResponseObject(MvcResult result, Class<T> response) {
        try {
            JsonElement payloadJson = gson.fromJson(result.getResponse().getContentAsString(), JsonObject.class).get("payload");
            return gson.fromJson(payloadJson, (Type) response);
        } catch (UnsupportedEncodingException uee) {
            uee.printStackTrace();
            throw new RuntimeException(uee);
        }
    }

    private <T> List<T> jsonToResponseObjectList(MvcResult result, Class<T> response) {
        List<T> jsonToObjectList = new ArrayList<>();
        try {
            JsonArray jsonArray = gson.fromJson(result.getResponse().getContentAsString(), JsonObject.class).get("payload").getAsJsonArray();
            for (JsonElement jsonElement : jsonArray) {
                T object = gson.fromJson(jsonElement, response);
                jsonToObjectList.add(object);
            }
            return jsonToObjectList;
        } catch (UnsupportedEncodingException uee) {
            uee.printStackTrace();
            throw new RuntimeException(uee);
        }
    }
}
