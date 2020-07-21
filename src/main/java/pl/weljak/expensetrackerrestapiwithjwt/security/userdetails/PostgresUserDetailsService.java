package pl.weljak.expensetrackerrestapiwithjwt.security.userdetails;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import pl.weljak.expensetrackerrestapiwithjwt.domain.user.PostgresEtUserRepo;
import pl.weljak.expensetrackerrestapiwithjwt.domain.user.EtUser;

@Service
@RequiredArgsConstructor
public class PostgresUserDetailsService implements UserDetailsService {
    private final PostgresEtUserRepo postgresEtUserRepo;

    @Override
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        EtUser etUser = postgresEtUserRepo.findByUsername(username);
        return new UserPrinciple(etUser);
    }
}
