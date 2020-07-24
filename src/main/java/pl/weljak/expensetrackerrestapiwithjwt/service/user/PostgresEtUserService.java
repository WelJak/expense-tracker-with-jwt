package pl.weljak.expensetrackerrestapiwithjwt.service.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.weljak.expensetrackerrestapiwithjwt.domain.user.EtUser;
import pl.weljak.expensetrackerrestapiwithjwt.domain.user.PostgresEtUserRepository;
import pl.weljak.expensetrackerrestapiwithjwt.domain.user.UserAlreadyExistsException;
import pl.weljak.expensetrackerrestapiwithjwt.domain.user.UserRole;
import pl.weljak.expensetrackerrestapiwithjwt.security.jwt.JwtTokenProvider;

import java.util.Collections;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class PostgresEtUserService implements EtUserService {
    private final PostgresEtUserRepository etUserRepo;
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    @Override
    public String  validateUser(String username, String password) {
        log.info("Validating user: {}", username);
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(username, password)
        );
        SecurityContextHolder.getContext().setAuthentication(authentication);
        return jwtTokenProvider.generateJwtToken(authentication);
    }

    @Override
    @Transactional
    public EtUser createUser(String username, String firstName, String lastName, String email, String password) {
        log.info("Creating new user with username: {}, email:{}", username, email);
        if (etUserRepo.existsByUsername(username) || etUserRepo.existsByEmail(email)){
            log.error("User {} already exists", username);
            throw new UserAlreadyExistsException();
        }
        EtUser etUser = new EtUser(UUID.randomUUID().toString(),username, firstName, lastName, email, bCryptPasswordEncoder.encode(password), UserRole.ROLE_USER, Collections.emptyList());
        etUserRepo.save(etUser);
        log.info("User {} created! User id: {}", etUser.getUsername(), etUser.getUserId());
        return etUser;
    }

    @Override
    public EtUser findEtUserById(String id) {
        log.info("Fetching details of user with id: {}", id);
        return etUserRepo.findByUserId(id);
    }

    @Override
    public EtUser findEtUserByUsername(String username) {
        log.info("Fetching details of user with username: {}", username);
        return etUserRepo.findByUsername(username);
    }
}
