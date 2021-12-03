package com.ead.authuser.controllers;

import com.ead.authuser.configs.security.JwtProvider;
import com.ead.authuser.dtos.JwtDto;
import com.ead.authuser.dtos.LoginDto;
import com.ead.authuser.dtos.UserDTO;
import com.ead.authuser.enums.RoleType;
import com.ead.authuser.enums.UserStatus;
import com.ead.authuser.enums.UserType;
import com.ead.authuser.models.RoleModel;
import com.ead.authuser.models.UserModel;
import com.ead.authuser.service.RoleService;
import com.ead.authuser.service.UserService;
import com.fasterxml.jackson.annotation.JsonView;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.time.LocalDateTime;
import java.time.ZoneId;

@Log4j2
@RestController
@CrossOrigin(origins = "*", maxAge = 3600)
@RequestMapping(value = "/auth")
public class AuthenticationController {

    @Autowired
    private UserService userService;

    @Autowired
    private RoleService roleService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    JwtProvider jwtProvider;

    @Autowired
    AuthenticationManager authenticationManager;


    @PostMapping("/signup")
    public ResponseEntity<Object> registerUser(@RequestBody @Validated(UserDTO.UserView.RegistrationPost.class)  @JsonView(UserDTO.UserView.RegistrationPost.class) UserDTO userDTO){
        log.debug("POST registerUser userDTO received {} ", userDTO.toString());
        if(userService.existsByUserName(userDTO.getUsername())){
            log.warn("Username {} is already taken ", userDTO.getUsername());
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Erro: Username is already taken.");
        }
        if(userService.existsByEmail(userDTO.getEmail())){
            log.warn("Email {} is already taken ", userDTO.getEmail());
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Erro: Email is already taken.");
        }

        RoleModel roleModel = this.roleService.findByRoleName(RoleType.ROLE_STUDENT)
                .orElseThrow(() -> new RuntimeException("Error: Role is Not Found"));

        userDTO.setPassword(passwordEncoder.encode(userDTO.getPassword()));

        var userModel = new UserModel();
        BeanUtils.copyProperties(userDTO, userModel);
        userModel.setUserStatus(UserStatus.ACTIVE);
        userModel.setUserType(UserType.STUDENT);
        userModel.setCreationDate(LocalDateTime.now(ZoneId.of("UTC")));
        userModel.setLastUpdateDate(LocalDateTime.now(ZoneId.of("UTC")));

        userModel.getRoles().add(roleModel);

        userService.saveUser(userModel);
        log.debug("POST registerUser userDTO saved {} ", userModel.getUserId());
        log.info("User saved sucessfully userId {} ", userModel.getUserId());
        return ResponseEntity.status(HttpStatus.CREATED).body(userModel);
    }

    @PostMapping("/login")
    public ResponseEntity<JwtDto> authenticateUser(@Valid @RequestBody LoginDto loginDto){
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginDto.getUsername(), loginDto.getPassword())
        );
        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = jwtProvider.generateJwt(authentication);
        return ResponseEntity.ok(new JwtDto(jwt));
    }


    //Método apenas para exemplificar níveis de log
    @GetMapping("/")
    public String index(){
        log.trace("TRACE"); //todos os logs
        log.debug("DEBUG");  //para depuração
        log.info("INFO"); //para ambiente de produção
        log.warn("WARN"); //alerta
        log.error("ERROR"); //para erro no sistema

        return "Logging Spring Boot";
    }


}
