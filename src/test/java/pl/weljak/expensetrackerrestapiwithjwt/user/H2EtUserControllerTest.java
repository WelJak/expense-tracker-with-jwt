package pl.weljak.expensetrackerrestapiwithjwt.user;

import com.google.gson.Gson;
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
import pl.weljak.expensetrackerrestapiwithjwt.domain.user.EtUser;
import pl.weljak.expensetrackerrestapiwithjwt.domain.user.PostgresEtUserRepository;
import pl.weljak.expensetrackerrestapiwithjwt.domain.user.UserRole;
import pl.weljak.expensetrackerrestapiwithjwt.service.user.EtUserService;
import pl.weljak.expensetrackerrestapiwithjwt.utils.Endpoints;
import pl.weljak.expensetrackerrestapiwithjwt.webapi.etuser.request.EtLoginRequest;
import pl.weljak.expensetrackerrestapiwithjwt.webapi.etuser.request.EtRegistrationRequest;
import pl.weljak.expensetrackerrestapiwithjwt.webapi.etuser.response.EtLoginResponse;
import pl.weljak.expensetrackerrestapiwithjwt.webapi.etuser.response.EtUserDetailsResponse;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Type;
import java.util.Collections;

@Slf4j
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class H2EtUserControllerTest {
    @Autowired
    private PostgresEtUserRepository userRepository;

    @Autowired
    private EtUserService userService;

    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @Autowired
    private MockMvc mockMvc;

    private Gson gson = new Gson();

    private static final String authHeader = "Authorization";

    private static final String BEARER = "Bearer ";

    @BeforeEach
    public void doBeforeEachTest() {
        log.info("Staring new test");
        EtUser etUser = new EtUser("testId", "johnTravolta", "John", "Travolta", "john@john.com", bCryptPasswordEncoder.encode("user"), UserRole.ROLE_USER, Collections.emptyList());
        EtUser admin = new EtUser("adminId", "admin", "admin", "admin", "admin@gmail.com", bCryptPasswordEncoder.encode("admin"), UserRole.ROLE_ADMIN, Collections.emptyList());
        userRepository.save(etUser);
        userRepository.save(admin);
    }

    @AfterEach
    public void doAfterEachTest() {
        userRepository.deleteAll();
    }

    @Test
    public void etUserControllerShouldAuthenticateUser() throws Exception {
        // given
        EtLoginRequest loginRequest = new EtLoginRequest("johnTravolta", "user");

        // when
        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.get(Endpoints.AUTH_LOGIN_ENDPOINT).contentType(MediaType.APPLICATION_JSON).content(gson.toJson(loginRequest)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();

        EtLoginResponse loginResponse = jsonToResponseObject(result, EtLoginResponse.class);

        // then
        Assertions.assertNotNull(loginResponse.getToken());
        Assertions.assertEquals("Bearer", loginResponse.getTokenType());
    }

    @Test
    public void etUserControllerShouldFetchCurrentUserDetails() throws Exception {
        // given
        final String jwtToken = userService.validateUser("johnTravolta", "user");

        // when
        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.get(Endpoints.USER_DETAILS_ENDPOINT).contentType(MediaType.APPLICATION_JSON).header(authHeader, BEARER + jwtToken))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();

        EtUserDetailsResponse userDetailsResponse = jsonToResponseObject(result, EtUserDetailsResponse.class);

        // then
        Assertions.assertEquals("johnTravolta", userDetailsResponse.getUsername());
        Assertions.assertEquals("John", userDetailsResponse.getFirstName());
        Assertions.assertEquals("Travolta", userDetailsResponse.getLastName());
        Assertions.assertEquals("john@john.com", userDetailsResponse.getEmail());
    }

    @Test
    public void etUserControllerShouldFetchCertainUserDetailsForAdmin() throws Exception {
        // given
        final String jwtToken = userService.validateUser("admin", "admin");
        final String userId = "testId";
        final String USER_DETAILS_ENDPOINT = Endpoints.USERS_DETAILS_ENDPOINT.replace("{id}", userId);

        // when
        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.get(USER_DETAILS_ENDPOINT).contentType(MediaType.APPLICATION_JSON).header(authHeader, BEARER + jwtToken))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();

        EtUserDetailsResponse userDetailsResponse = jsonToResponseObject(result, EtUserDetailsResponse.class);

        // then
        Assertions.assertEquals("johnTravolta", userDetailsResponse.getUsername());
        Assertions.assertEquals("John", userDetailsResponse.getFirstName());
        Assertions.assertEquals("Travolta", userDetailsResponse.getLastName());
        Assertions.assertEquals("john@john.com", userDetailsResponse.getEmail());
    }

    @Test
    public void etUserControllerShouldRejectFetchingUserDetailsForUserWithInsufficientPermissions() throws Exception {
        // given
        final String jwtToken = userService.validateUser("johnTravolta", "user");
        final String userId = "admin";
        final String USER_DETAILS_ENDPOINT = Endpoints.USERS_DETAILS_ENDPOINT.replace("{id}", userId);

        // then
        mockMvc.perform(MockMvcRequestBuilders.get(USER_DETAILS_ENDPOINT).contentType(MediaType.APPLICATION_JSON).header(authHeader, BEARER + jwtToken))
                .andExpect(MockMvcResultMatchers.status().isForbidden());
    }

    @Test
    public void etUserControllerShouldCreateNewEtUser() throws Exception {
        // given
        EtRegistrationRequest registrationRequest = new EtRegistrationRequest("newUser", "password", "user", "user", "user@user.com");

        // when
        mockMvc.perform(MockMvcRequestBuilders.post(Endpoints.AUTH_REGISTER_ENDPOINT).contentType(MediaType.APPLICATION_JSON).content(gson.toJson(registrationRequest)))
                .andExpect(MockMvcResultMatchers.status().isCreated());
        EtUser etUser = userService.findEtUserByUsername("newUser");

        // then
        Assertions.assertNotNull(etUser);

    }

    private <T> T jsonToResponseObject(MvcResult result, Class<T> response) {
        try {
            JsonObject responseJsonObject = gson.fromJson(result.getResponse().getContentAsString(), JsonObject.class);
            JsonElement payloadJson = responseJsonObject.getAsJsonObject("payload");
            return gson.fromJson(payloadJson, (Type) response);
        } catch (UnsupportedEncodingException uee) {
            uee.printStackTrace();
            throw new RuntimeException(uee);
        }
    }
}
