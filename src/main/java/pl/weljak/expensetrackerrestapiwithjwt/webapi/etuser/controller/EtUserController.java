package pl.weljak.expensetrackerrestapiwithjwt.webapi.etuser.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import pl.weljak.expensetrackerrestapiwithjwt.domain.user.EtUser;
import pl.weljak.expensetrackerrestapiwithjwt.security.userdetails.UserPrinciple;
import pl.weljak.expensetrackerrestapiwithjwt.service.user.EtUserService;
import pl.weljak.expensetrackerrestapiwithjwt.utils.Endpoints;
import pl.weljak.expensetrackerrestapiwithjwt.utils.EtResponse;
import pl.weljak.expensetrackerrestapiwithjwt.utils.EtResponseUtils;
import pl.weljak.expensetrackerrestapiwithjwt.webapi.etuser.request.EtLoginRequest;
import pl.weljak.expensetrackerrestapiwithjwt.webapi.etuser.request.EtRegistrationRequest;
import pl.weljak.expensetrackerrestapiwithjwt.webapi.etuser.response.EtLoginResponse;
import pl.weljak.expensetrackerrestapiwithjwt.webapi.etuser.response.EtUserDetailsResponse;

@Slf4j
@RestController
@RequiredArgsConstructor
public class EtUserController {
    private final EtUserService etUserService;

    @GetMapping(Endpoints.AUTH_LOGIN_ENDPOINT)
    public ResponseEntity<EtResponse> authenticateUser(@RequestBody EtLoginRequest loginRequest) {
        String jwtToken = etUserService.validateUser(loginRequest.getUsername(), loginRequest.getPassword());
        return EtResponseUtils.success(Endpoints.AUTH_LOGIN_ENDPOINT, new EtLoginResponse("Bearer", jwtToken), "Generated authentication token", HttpStatus.OK);
    }

    @GetMapping(Endpoints.USER_DETAILS_ENDPOINT)
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<EtResponse> fetchCurrentEtUserDetails(@AuthenticationPrincipal UserPrinciple userPrinciple) {
        EtUser etUser = etUserService.findEtUserByUsername(userPrinciple.getEtUserUsername());
        return EtResponseUtils.success(Endpoints.USER_DETAILS_ENDPOINT, toEtUserDetailsResponse(etUser), "Fetched user details", HttpStatus.OK);
    }

    @GetMapping(Endpoints.USERS_DETAILS_ENDPOINT)
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<EtResponse> fetchEtUserDetails(@AuthenticationPrincipal UserPrinciple userPrinciple, @PathVariable String id) {
        log.info("Fetching info for user with id: {}. Commissioned by: {} ", id, userPrinciple.getEtUserUsername());
        EtUser etUser = etUserService.findEtUserById(id);
        return EtResponseUtils.success(Endpoints.USER_DETAILS_ENDPOINT, toEtUserDetailsResponse(etUser), "Fetched user details", HttpStatus.OK);
    }

    @PostMapping(Endpoints.AUTH_REGISTER_ENDPOINT)
    public ResponseEntity<EtResponse> registerEtUser(@RequestBody EtRegistrationRequest registrationRequest) {
        log.info("Processing registration request for: {} with email: {}", registrationRequest.getUsername(), registrationRequest.getEmail());
        EtUser etUser = etUserService.createUser(registrationRequest.getUsername(), registrationRequest.getFirstName(), registrationRequest.getLastName(), registrationRequest.getEmail(), registrationRequest.getPassword());
        return EtResponseUtils.success(Endpoints.AUTH_REGISTER_ENDPOINT, toEtUserDetailsResponse(etUser), "New user created!", HttpStatus.CREATED);
    }

    @DeleteMapping(Endpoints.USER_DELETE_ENDPOINT)
    public ResponseEntity<EtResponse> deleteEtUserById(@AuthenticationPrincipal UserPrinciple userPrinciple) {
        log.info("Deleting user with id: {}", userPrinciple.getEtUserId());
        etUserService.deleteEtUserById(userPrinciple.getEtUserId());
        return EtResponseUtils.noContent();
    }

    private EtUserDetailsResponse toEtUserDetailsResponse(EtUser etUser) {
        return EtUserDetailsResponse.builder()
                .username(etUser.getUsername())
                .firstName(etUser.getFirstName())
                .lastName(etUser.getLastName())
                .email(etUser.getEmail())
                .build();
    }
}
