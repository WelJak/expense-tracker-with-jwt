package pl.weljak.expensetrackerrestapiwithjwt.domain.categories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EtPostgresCategoryRepository extends JpaRepository<Category, String> {
}
