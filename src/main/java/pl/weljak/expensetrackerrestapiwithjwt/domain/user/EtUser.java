package pl.weljak.expensetrackerrestapiwithjwt.domain.user;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import pl.weljak.expensetrackerrestapiwithjwt.domain.categories.Category;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "et_users")
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class EtUser {
    @Id
    @Column(name = "user_id", nullable = false)
    private String userId;

    @Column(name = "username", nullable = false)
    private String username;

    @Column(name = "first_name", nullable = false)
    private String firstName;

    @Column(name = "last_name", nullable = false)
    private String lastName;

    @Column(name = "email", nullable = false)
    private String email;

    @Column(name = "password", nullable = false)
    private String password;

    @Column(name = "role", nullable = false)
    @Enumerated(EnumType.STRING)
    private UserRole userRole;

    @OneToMany(mappedBy = "etUserId", cascade = CascadeType.REMOVE)
    List<Category> categories;

}
