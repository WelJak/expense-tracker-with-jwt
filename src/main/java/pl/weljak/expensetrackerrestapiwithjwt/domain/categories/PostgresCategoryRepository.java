package pl.weljak.expensetrackerrestapiwithjwt.domain.categories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pl.weljak.expensetrackerrestapiwithjwt.domain.user.EtUser;

import java.util.List;

@Repository
public interface PostgresCategoryRepository extends JpaRepository<Category, String> {
    Category findByCategoryId(String categoryId);

    List<Category> findAllByEtUserId(EtUser etUser);

    Category findByTitle(String title);
}
