package pl.weljak.expensetrackerrestapiwithjwt.security.userdetails;

import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.User;
import pl.weljak.expensetrackerrestapiwithjwt.domain.user.EtUser;
import pl.weljak.expensetrackerrestapiwithjwt.domain.user.UserRole;

public class UserPrinciple extends User {
    private final EtUser etUser;

    public UserPrinciple(EtUser etUser) {
        super(etUser.getUsername(), etUser.getPassword(), AuthorityUtils.commaSeparatedStringToAuthorityList(etUser.getUserRole().name()));
        this.etUser = etUser;
    }

    public EtUser getCurrentEtUser(){
        return this.etUser;
    }

    public UserRole getEtUserRole() {
        return this.etUser.getUserRole();
    }

    public String getEtUserId() {
        return this.etUser.getUserId();
    }

    public String getEtUserUsername() {
        return this.etUser.getUsername();
    }
}
