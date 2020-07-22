package pl.weljak.expensetrackerrestapiwithjwt.domain.user;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PostgresEtUserRepository extends JpaRepository<EtUser, String> {
    EtUser findByUserId(String userId);

    EtUser findByUsername(String username);

    Boolean existsByUsername(String username);

    Boolean existsByEmail(String email);
}
