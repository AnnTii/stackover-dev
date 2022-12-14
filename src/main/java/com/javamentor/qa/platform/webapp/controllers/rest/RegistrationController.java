package com.javamentor.qa.platform.webapp.controllers.rest;

import com.javamentor.qa.platform.converters.UserConverter;
import com.javamentor.qa.platform.models.dto.UserRegistrationDto;
import com.javamentor.qa.platform.models.entity.user.User;
import com.javamentor.qa.platform.service.EmailService;
import com.javamentor.qa.platform.service.abstracts.model.UserService;
import io.swagger.annotations.ApiOperation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("api/user/registration")
public class RegistrationController {

    @Value("${spring.mail.EXPIRATION_TIME_IN_MINUTES}")
    private int EXPIRATION_TIME_IN_MINUTES;
    @Value("${spring.mail.username}")
    private String fromAddress;
    @Value("${spring.mail.username}")
    private String senderName;
    @Value("${spring.mail.host}")
    private String host;

    private final UserService userService;
    private final EmailService emailService;
    private final PasswordEncoder passwordEncoder;
    private final UserConverter userConverter;

    public RegistrationController(UserService userService, EmailService emailService, PasswordEncoder passwordEncoder, UserConverter userConverter) {
        this.userService = userService;
        this.emailService = emailService;
        this.passwordEncoder = passwordEncoder;
        this.userConverter = userConverter;
    }

    public boolean addUserRegistrationDto(UserRegistrationDto userRegistrationDto) {
        if (!StringUtils.hasLength(userRegistrationDto.getEmail()) || userService.getByEmail(userRegistrationDto.getEmail()).isPresent()) {
            return false;
        }
        userRegistrationDto.setPassword(passwordEncoder.encode(userRegistrationDto.getPassword()));
        userRegistrationDto.setActivationCode(UUID.randomUUID().toString());
        userService.persist(userConverter.userRegistrationDtoDtoToUser(userRegistrationDto));

        String message = String.format("Hello, %s \n" +
                        "Please follow the link to complete your registration: http://localhost:8080/api/user/registration/verify/%s",
                userRegistrationDto.getFirstName(),
                userRegistrationDto.getActivationCode());

        emailService.send(userRegistrationDto.getEmail(), "Account verify", message);
        return true;
    }

    public boolean verifyUserRegistrationDto(String activationCode) {
        Optional<User> user = userService.getUserByActivationCode(activationCode);
        if (user.isEmpty()) {
            return false;
        }
        user.get().setIsEnabled(true);
        userService.update(user.get());
        return true;
    }

    @PostMapping
    @ApiOperation("?????????????????????? user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "?????????????????????? ???????????? ??????????????"),
            @ApiResponse(responseCode = "414", description = "???????????? ?? ?????????????? ??????????????????????")
    })
    private ResponseEntity<UserRegistrationDto> sendMessage(@RequestBody UserRegistrationDto userRegistrationDto) {
        if (!addUserRegistrationDto(userRegistrationDto)) {
            return new ResponseEntity<>(HttpStatus.PRECONDITION_FAILED);
        }
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping("/verify/{activationCode}")
    @ApiOperation("???????????????????? ?????????????????? user, ???????????????????? ???????????? ?? ???????????????????????????? email")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Email ??????????????????????"),
            @ApiResponse(responseCode = "414", description = "???????????? ?? ?????????????? ??????????????????????????")
    })
    private ResponseEntity<UserRegistrationDto> verify(@PathVariable String activationCode) {
        if (!verifyUserRegistrationDto(activationCode)) {
            return new ResponseEntity<>(HttpStatus.PRECONDITION_FAILED);
        }
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
