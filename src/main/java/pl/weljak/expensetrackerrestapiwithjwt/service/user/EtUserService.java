package pl.weljak.expensetrackerrestapiwithjwt.service.user;

import pl.weljak.expensetrackerrestapiwithjwt.domain.user.EtUser;

public interface EtUserService {
    String validateUser(String username, String password);

    EtUser createUser(String username, String firstName, String lastName, String email, String password);

    EtUser findEtUserById(String id);

    EtUser findEtUserByUsername(String username);
}
