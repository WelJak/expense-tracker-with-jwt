package pl.weljak.expensetrackerrestapiwithjwt.security.jwt;

import io.jsonwebtoken.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import pl.weljak.expensetrackerrestapiwithjwt.security.userdetails.UserPrinciple;

import java.util.Date;

@Component
@Slf4j
public class JwtTokenProvider {
    @Value("${weljak.app.jwtSecret}")
    private String jwtSecret;

    @Value("${weljak.app.jwtExpiration}")
    private long jwtExpiration;

    public String generateJwtToken(Authentication authentication){
        UserPrinciple userPrinciple = (UserPrinciple) authentication.getPrincipal();

        return Jwts.builder()
                .setSubject(userPrinciple.getEtUserUsername())
                .setIssuedAt(new Date())
                .setExpiration(new Date((new Date()).getTime() + jwtExpiration))
                .signWith(SignatureAlgorithm.HS512, jwtSecret)
                .compact();
    }

    public String getUserNameFromJwtToken(String jwtToken) {
        return Jwts.parser()
                .setSigningKey(jwtSecret)
                .parseClaimsJws(jwtToken)
                .getBody()
                .getSubject();
    }

    public Boolean validateJwtToken(String jwtToken) {
        try {
            Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(jwtToken);
            return true;
        } catch (SignatureException se) {
            log.error("Invalid JWT signature: {} ", se.getMessage());
        } catch (MalformedJwtException mje) {
            log.error("Invalid JWT token: {}", mje.getMessage());
        } catch (ExpiredJwtException eje) {
            log.error("Expired JWT token: {}", eje.getMessage());
        } catch (UnsupportedJwtException uje) {
            log.error("Unsupported JWT token: {}", uje.getMessage());
        } catch (IllegalArgumentException iae) {
            log.error("JWT claims string is empty: {}", iae.getMessage());
        }

        return false;
    }
}
