package pl.weljak.expensetrackerrestapiwithjwt.transaction;

import com.google.gson.*;
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
import pl.weljak.expensetrackerrestapiwithjwt.domain.transactions.PostgresTransactionRepository;
import pl.weljak.expensetrackerrestapiwithjwt.domain.transactions.Transaction;
import pl.weljak.expensetrackerrestapiwithjwt.domain.user.EtUser;
import pl.weljak.expensetrackerrestapiwithjwt.domain.user.PostgresEtUserRepository;
import pl.weljak.expensetrackerrestapiwithjwt.domain.user.UserRole;
import pl.weljak.expensetrackerrestapiwithjwt.service.user.EtUserService;
import pl.weljak.expensetrackerrestapiwithjwt.utils.Endpoints;
import pl.weljak.expensetrackerrestapiwithjwt.webapi.transaction.request.CreateTransactionRequest;
import pl.weljak.expensetrackerrestapiwithjwt.webapi.transaction.request.UpdateTransactionRequest;
import pl.weljak.expensetrackerrestapiwithjwt.webapi.transaction.response.TransactionDetailsResponse;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Type;
import java.time.Clock;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Slf4j
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class H2TransactionControllerTest {
    @Autowired
    private PostgresEtUserRepository userRepository;

    @Autowired
    private EtUserService userService;

    @Autowired
    private PostgresCategoryRepository categoryRepository;

    @Autowired
    private PostgresTransactionRepository transactionRepository;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @Autowired
    private Clock clock;

    private final Gson gson = new GsonBuilder().registerTypeAdapter(LocalDateTime.class, (JsonDeserializer<LocalDateTime>) (json, type, jsonDeserializationContext) -> LocalDateTime.parse(json.getAsJsonPrimitive().getAsString())).create();

    private static final String authHeader = "Authorization";

    private static final String BEARER = "Bearer ";

    @BeforeEach
    public void doBeforeEachTest() {
        log.info("Starting new test");
        EtUser etUser = new EtUser("testId", "johnTravolta", "John", "Travolta", "john@john.com", bCryptPasswordEncoder.encode("user"), UserRole.ROLE_USER, Collections.emptyList());
        userRepository.save(etUser);
        Category category = new Category("catId", etUser, "testTitle", "testDesc", Collections.emptyList());
        categoryRepository.save(category);
        Transaction transaction = new Transaction("transId", category, etUser, 1000L, "testNote", LocalDateTime.now(clock));
        transactionRepository.save(transaction);
    }

    @AfterEach
    public void doAfterEachTest() {
        userRepository.deleteAll();
    }

    @Test
    public void transactionControllerShouldCreateTransaction() throws Exception {
        // given
        final String jwtToken = userService.validateUser("johnTravolta", "user");
        final CreateTransactionRequest createTransactionRequest = new CreateTransactionRequest("catId", 100000L, "new note");

        // when
        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.post(Endpoints.TRANSACTION_CREATE_ENDPOINT).contentType(MediaType.APPLICATION_JSON).content(gson.toJson(createTransactionRequest)).header(authHeader, BEARER + jwtToken))
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andReturn();

        TransactionDetailsResponse transactionDetailsResponse = jsonToResponseObject(result, TransactionDetailsResponse.class);
        Transaction res = transactionRepository.findByTransactionId(transactionDetailsResponse.getId());

        // then
        Assertions.assertNotNull(res);
        Assertions.assertEquals("catId", res.getCategory().getCategoryId());
        Assertions.assertEquals("testId", res.getEtUser().getUserId());
        Assertions.assertEquals(100000L, res.getAmount());
        Assertions.assertEquals("new note", res.getNote());
    }

    @Test
    public void transactionControllerShouldFindTransactionDetailsByTransactionId() throws Exception {
        // given
        final String jwtToken = userService.validateUser("johnTravolta", "user");
        final String transactionDetailsEndpoint = Endpoints.TRANSACTION_DETAILS_ENDPOINT.replace("{id}", "transId");

        // when
        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.get(transactionDetailsEndpoint).contentType(MediaType.APPLICATION_JSON).header(authHeader, BEARER + jwtToken))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();

        TransactionDetailsResponse res = jsonToResponseObject(result, TransactionDetailsResponse.class);

        // then
        Assertions.assertNotNull(res);
        Assertions.assertEquals("catId", res.getCategoryId());
        Assertions.assertEquals("testId", res.getUserId());
        Assertions.assertEquals(1000L, res.getAmount());
        Assertions.assertEquals("testNote", res.getNote());
    }

    @Test
    public void transactionControllerShouldDeleteTransaction() throws Exception {
        // given
        final String jwtToken = userService.validateUser("johnTravolta", "user");
        final String deleteTransactionEndpoint = Endpoints.TRANSACTIONS_DELETE_ENDPOINT.replace("{id}", "transId");

        // when
        mockMvc.perform(MockMvcRequestBuilders.delete(deleteTransactionEndpoint).contentType(MediaType.APPLICATION_JSON).header(authHeader, BEARER + jwtToken))
                .andExpect(MockMvcResultMatchers.status().isNoContent());

        Transaction res = transactionRepository.findByTransactionId("transId");

        // then
        Assertions.assertNull(res);
    }

    @Test
    public void transactionControllerShouldUpdateOnlyTransactionAmountWhenOnlyOneIsGiven() throws Exception {
        // given
        final String jwtToken = userService.validateUser("johnTravolta", "user");
        final UpdateTransactionRequest updateTransactionRequest = new UpdateTransactionRequest("transId", 2000L, null, null);

        // when
        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.put(Endpoints.TRANSACTION_UPDATE_ENDPOINT).contentType(MediaType.APPLICATION_JSON).content(gson.toJson(updateTransactionRequest)).header(authHeader, BEARER + jwtToken))
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andReturn();

        TransactionDetailsResponse transactionDetailsResponse = jsonToResponseObject(result, TransactionDetailsResponse.class);

        Transaction res = transactionRepository.findByTransactionId(transactionDetailsResponse.getId());

        // then
        Assertions.assertNotNull(res);
        Assertions.assertEquals("transId", res.getTransactionId());
        Assertions.assertEquals(2000L, res.getAmount());
        Assertions.assertEquals("testNote", res.getNote());
        Assertions.assertEquals("catId", res.getCategory().getCategoryId());
    }

    @Test
    public void transactionControllerShouldUpdateOnlyTransactionNoteWhenOnlyOneIsGiven() throws Exception {
        // given
        final String jwtToken = userService.validateUser("johnTravolta", "user");
        final UpdateTransactionRequest updateTransactionRequest = new UpdateTransactionRequest("transId", null, "new note", null);

        // when
        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.put(Endpoints.TRANSACTION_UPDATE_ENDPOINT).contentType(MediaType.APPLICATION_JSON).content(gson.toJson(updateTransactionRequest)).header(authHeader, BEARER + jwtToken))
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andReturn();

        TransactionDetailsResponse transactionDetailsResponse = jsonToResponseObject(result, TransactionDetailsResponse.class);

        Transaction res = transactionRepository.findByTransactionId(transactionDetailsResponse.getId());

        // then
        Assertions.assertNotNull(res);
        Assertions.assertEquals("transId", res.getTransactionId());
        Assertions.assertEquals(1000L, res.getAmount());
        Assertions.assertEquals("new note", res.getNote());
        Assertions.assertEquals("catId", res.getCategory().getCategoryId());
    }

    @Test
    public void transactionControllerShouldUpdateOnlyTransactionCategoryWhenOnlyOneIsGiven() throws Exception {
        // given
        final String jwtToken = userService.validateUser("johnTravolta", "user");
        final EtUser etUser = userRepository.findByUserId("testId");
        Category category = new Category("newCatId", etUser, "test category", "description", Collections.emptyList());
        categoryRepository.save(category);
        final UpdateTransactionRequest updateTransactionRequest = new UpdateTransactionRequest("transId", null, null, "newCatId");

        // when
        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.put(Endpoints.TRANSACTION_UPDATE_ENDPOINT).contentType(MediaType.APPLICATION_JSON).content(gson.toJson(updateTransactionRequest)).header(authHeader, BEARER + jwtToken))
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andReturn();

        TransactionDetailsResponse transactionDetailsResponse = jsonToResponseObject(result, TransactionDetailsResponse.class);

        Transaction res = transactionRepository.findByTransactionId(transactionDetailsResponse.getId());

        // then
        Assertions.assertNotNull(res);
        Assertions.assertEquals("transId", res.getTransactionId());
        Assertions.assertEquals(1000L, res.getAmount());
        Assertions.assertEquals("testNote", res.getNote());
        Assertions.assertEquals("newCatId", res.getCategory().getCategoryId());
    }

    @Test
    public void transactionControllerShouldUpdateOnlyAmountAndNoteWhenGiven() throws Exception {
        // given
        final String jwtToken = userService.validateUser("johnTravolta", "user");
        final UpdateTransactionRequest updateTransactionRequest = new UpdateTransactionRequest("transId", 2000L, "newest note", null);

        // when
        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.put(Endpoints.TRANSACTION_UPDATE_ENDPOINT).contentType(MediaType.APPLICATION_JSON).content(gson.toJson(updateTransactionRequest)).header(authHeader, BEARER + jwtToken))
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andReturn();

        TransactionDetailsResponse transactionDetailsResponse = jsonToResponseObject(result, TransactionDetailsResponse.class);

        Transaction res = transactionRepository.findByTransactionId(transactionDetailsResponse.getId());

        // then
        Assertions.assertNotNull(res);
        Assertions.assertEquals("transId", res.getTransactionId());
        Assertions.assertEquals(2000L, res.getAmount());
        Assertions.assertEquals("newest note", res.getNote());
        Assertions.assertEquals("catId", res.getCategory().getCategoryId());
    }

    @Test
    public void transactionControllerShouldUpdateOnlyAmountAndCategoryWhenGiven() throws Exception {
        // given
        final String jwtToken = userService.validateUser("johnTravolta", "user");
        final EtUser etUser = userRepository.findByUserId("testId");
        Category category = new Category("newCatId", etUser, "test category", "description", Collections.emptyList());
        categoryRepository.save(category);
        final UpdateTransactionRequest updateTransactionRequest = new UpdateTransactionRequest("transId", 2000L, null, "newCatId");

        // when
        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.put(Endpoints.TRANSACTION_UPDATE_ENDPOINT).contentType(MediaType.APPLICATION_JSON).content(gson.toJson(updateTransactionRequest)).header(authHeader, BEARER + jwtToken))
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andReturn();

        TransactionDetailsResponse transactionDetailsResponse = jsonToResponseObject(result, TransactionDetailsResponse.class);

        Transaction res = transactionRepository.findByTransactionId(transactionDetailsResponse.getId());

        // then
        Assertions.assertNotNull(res);
        Assertions.assertEquals("transId", res.getTransactionId());
        Assertions.assertEquals(2000L, res.getAmount());
        Assertions.assertEquals("testNote", res.getNote());
        Assertions.assertEquals("newCatId", res.getCategory().getCategoryId());
    }

    @Test
    public void transactionControllerShouldUpdateOnlyNoteAndCategoryWhenGiven() throws Exception {
        // given
        final String jwtToken = userService.validateUser("johnTravolta", "user");
        final EtUser etUser = userRepository.findByUserId("testId");
        Category category = new Category("newCatId", etUser, "test category", "description", Collections.emptyList());
        categoryRepository.save(category);
        final UpdateTransactionRequest updateTransactionRequest = new UpdateTransactionRequest("transId", null, "newest note", "newCatId");

        // when
        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.put(Endpoints.TRANSACTION_UPDATE_ENDPOINT).contentType(MediaType.APPLICATION_JSON).content(gson.toJson(updateTransactionRequest)).header(authHeader, BEARER + jwtToken))
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andReturn();

        TransactionDetailsResponse transactionDetailsResponse = jsonToResponseObject(result, TransactionDetailsResponse.class);

        Transaction res = transactionRepository.findByTransactionId(transactionDetailsResponse.getId());

        // then
        Assertions.assertNotNull(res);
        Assertions.assertEquals("transId", res.getTransactionId());
        Assertions.assertEquals(1000L, res.getAmount());
        Assertions.assertEquals("newest note", res.getNote());
        Assertions.assertEquals("newCatId", res.getCategory().getCategoryId());
    }

    @Test
    public void transactionControllerShouldUpdateAmountNoteAndCategoryWhenGiven() throws Exception {
        // given
        final String jwtToken = userService.validateUser("johnTravolta", "user");
        final EtUser etUser = userRepository.findByUserId("testId");
        Category category = new Category("newCatId", etUser, "test category", "description", Collections.emptyList());
        categoryRepository.save(category);
        final UpdateTransactionRequest updateTransactionRequest = new UpdateTransactionRequest("transId", 2000L, "newest note", "newCatId");

        // when
        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.put(Endpoints.TRANSACTION_UPDATE_ENDPOINT).contentType(MediaType.APPLICATION_JSON).content(gson.toJson(updateTransactionRequest)).header(authHeader, BEARER + jwtToken))
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andReturn();

        TransactionDetailsResponse transactionDetailsResponse = jsonToResponseObject(result, TransactionDetailsResponse.class);

        Transaction res = transactionRepository.findByTransactionId(transactionDetailsResponse.getId());

        // then
        Assertions.assertNotNull(res);
        Assertions.assertEquals("transId", res.getTransactionId());
        Assertions.assertEquals(2000L, res.getAmount());
        Assertions.assertEquals("newest note", res.getNote());
        Assertions.assertEquals("newCatId", res.getCategory().getCategoryId());
    }

    @Test
    public void transactionControllerShouldFetchAllUserTransactions() throws Exception {
        // given
        final String jwtToken = userService.validateUser("johnTravolta", "user");
        final EtUser etUser = userRepository.findByUserId("testId");
        Category category = new Category("newCatId", etUser, "test category", "description", Collections.emptyList());
        categoryRepository.save(category);
        Transaction transaction = new Transaction("newTransId", category, etUser, 25L, "new transaction note", LocalDateTime.now(clock));
        transactionRepository.save(transaction);

        // when
        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.get(Endpoints.TRANSACTIONS_ENDPOINT).contentType(MediaType.APPLICATION_JSON).header(authHeader, BEARER + jwtToken))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();

        List<TransactionDetailsResponse> res = jsonToResponseObjectList(result, TransactionDetailsResponse.class);

        // then
        Assertions.assertEquals(2, res.size());

        Assertions.assertEquals("catId", res.get(0).getCategoryId());
        Assertions.assertEquals("newCatId", res.get(1).getCategoryId());

        Assertions.assertEquals("transId", res.get(0).getId());
        Assertions.assertEquals("newTransId", res.get(1).getId());
    }

    @Test
    public void transactionControllerShouldFetchAllTransactionsDetailsByCategory() throws Exception {
        // given
        final String jwtToken = userService.validateUser("johnTravolta", "user");
        final String TRANSACTIONS_BY_CATEGORY_ENDPOINT = Endpoints.TRANSACTIONS_BY_CATEGORY_ENDPOINT.replace("{category}", "newCatId");
        final EtUser etUser = userRepository.findByUserId("testId");
        Category category = new Category("newCatId", etUser, "test category", "description", Collections.emptyList());
        categoryRepository.save(category);
        Transaction transaction1 = new Transaction("newTransId1", category, etUser, 25L, "new transaction note", LocalDateTime.now(clock));
        transactionRepository.save(transaction1);
        Transaction transaction2 = new Transaction("newTransId2", category, etUser, 2533L, "new transaction note 2", LocalDateTime.now(clock));
        transactionRepository.save(transaction2);

        // when
        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.get(TRANSACTIONS_BY_CATEGORY_ENDPOINT).contentType(MediaType.APPLICATION_JSON).header(authHeader, BEARER + jwtToken))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();

        List<TransactionDetailsResponse> res = jsonToResponseObjectList(result, TransactionDetailsResponse.class);

        // then
        Assertions.assertEquals(2, res.size());

        Assertions.assertEquals("newCatId", res.get(0).getCategoryId());
        Assertions.assertEquals("newCatId", res.get(1).getCategoryId());

        Assertions.assertEquals("newTransId1", res.get(0).getId());
        Assertions.assertEquals("newTransId2", res.get(1).getId());

        Assertions.assertEquals(25L, res.get(0).getAmount());
        Assertions.assertEquals(2533L, res.get(1).getAmount());

        Assertions.assertEquals("new transaction note", res.get(0).getNote());
        Assertions.assertEquals("new transaction note 2", res.get(1).getNote());
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
