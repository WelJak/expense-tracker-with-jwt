package pl.weljak.expensetrackerrestapiwithjwt.security.userdetails;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import pl.weljak.expensetrackerrestapiwithjwt.domain.user.PostgresEtUserRepository;
import pl.weljak.expensetrackerrestapiwithjwt.domain.user.EtUser;

@Service
@RequiredArgsConstructor
public class PostgresUserDetailsService implements UserDetailsService {
    private final PostgresEtUserRepository postgresEtUserRepository;

    @Override
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        EtUser etUser = postgresEtUserRepository.findByUsername(username);
        return new UserPrinciple(etUser);
    }
}
