package pl.weljak.expensetrackerrestapiwithjwt.user;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.test.context.ActiveProfiles;
import pl.weljak.expensetrackerrestapiwithjwt.ExpenseTrackerRestApiWithJwtApplication;
import pl.weljak.expensetrackerrestapiwithjwt.domain.user.EtUser;
import pl.weljak.expensetrackerrestapiwithjwt.domain.user.PostgresEtUserRepository;
import pl.weljak.expensetrackerrestapiwithjwt.domain.user.UserRole;
import pl.weljak.expensetrackerrestapiwithjwt.service.user.EtUserService;

import java.util.Collections;

@Slf4j
@ActiveProfiles("test")
@SpringBootTest
public class H2EtUserServiceTest {
    @Autowired
    PostgresEtUserRepository postgresEtUserRepository;

    @Autowired
    private EtUserService etUserService;

    @BeforeEach
    public void doBeforeEachTest() {
        log.info("Starting new test");
    }

    @AfterEach
    public void doAfterEachTest() {
        postgresEtUserRepository.deleteAll();
    }

    @Test
    public void etUserServiceShouldCreateNewUser() {
        // given
        EtUser newUser = etUserService.createUser("johnTravolta", "john", "travolta", "email@email.com", "password");

        // when
        EtUser res = etUserService.findEtUserByUsername("johnTravolta");

        // then
        Assertions.assertEquals(newUser.getUserId(), res.getUserId());
        Assertions.assertEquals("johnTravolta", res.getUsername());
        Assertions.assertEquals("john", res.getFirstName());
        Assertions.assertEquals("travolta", res.getLastName());
        Assertions.assertEquals("email@email.com", res.getEmail());
    }

    @Test
    public void etUserServiceShouldValidateUser() {
        // given
        EtUser newUser = etUserService.createUser("johnTravolta", "john", "travolta", "email@email.com", "password");

        // when
        String jwtToken = etUserService.validateUser("johnTravolta", "password");

        // then
        Assertions.assertNotNull(jwtToken);
        Assertions.assertNotEquals(0, jwtToken.length());
        Assertions.assertNotEquals(1, jwtToken.length());
    }

    @Test
    public void EtUserServiceShouldThrowBadCredentialsExceptionDuringValidationWhenPasswordIsIncorrect() {
        // given
        EtUser newUser = etUserService.createUser("johnTravolta", "john", "travolta", "email@email.com", "password");

        // then
        Assertions.assertThrows(BadCredentialsException.class, () -> etUserService.validateUser("johnTravolta", "incorrectPassword"));
    }

    @Test
    public void EtUserServiceShouldThrowInternalAuthenticationServiceExceptionDuringValidationWhenUsernameIsIncorrect() {
        // given
        EtUser newUser = etUserService.createUser("johnTravolta", "john", "travolta", "email@email.com", "password");

        // then
        Assertions.assertThrows(InternalAuthenticationServiceException.class, () -> etUserService.validateUser("Incorrect Username", "password"));
    }

    @Test
    public void EtUserServiceShouldFindUserByUserId() {
        // given
        EtUser user = new EtUser("newId", "JohnTravolta", "John", "Travolta", "jonh@john.com", "John123", UserRole.ROLE_USER, Collections.emptyList());
        postgresEtUserRepository.save(user);

        // when
        EtUser res = etUserService.findEtUserById("newId");

        // then
        Assertions.assertEquals(user.getUserId(), res.getUserId());
        Assertions.assertEquals("newId", res.getUserId());
        Assertions.assertEquals("JohnTravolta", res.getUsername());
        Assertions.assertEquals("John", res.getFirstName());
        Assertions.assertEquals("Travolta", res.getLastName());
        Assertions.assertEquals("jonh@john.com", res.getEmail());
        Assertions.assertEquals(0, res.getCategories().size());
    }

}
