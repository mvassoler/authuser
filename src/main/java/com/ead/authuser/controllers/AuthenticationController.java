package com.ead.authuser.controllers;

import com.ead.authuser.dtos.UserDTO;
import com.ead.authuser.enums.UserStatus;
import com.ead.authuser.enums.UserType;
import com.ead.authuser.models.UserModel;
import com.ead.authuser.service.UserService;
import com.fasterxml.jackson.annotation.JsonView;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.ZoneId;

@Log4j2
@RestController
@CrossOrigin(origins = "*", maxAge = 3600)
@RequestMapping(value = "/auth")
public class AuthenticationController {

    @Autowired
    public UserService userService;

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
        var userModel = new UserModel();
        BeanUtils.copyProperties(userDTO, userModel);
        userModel.setUserStatus(UserStatus.ACTIVE);
        userModel.setUserType(UserType.STUDENT);
        userModel.setCreationDate(LocalDateTime.now(ZoneId.of("UTC")));
        userModel.setLastUpdateDate(LocalDateTime.now(ZoneId.of("UTC")));
        userService.save(userModel);
        log.debug("POST registerUser userDTO saved {} ", userModel.getUserId());
        log.info("User saved sucessfully userId {} ", userModel.getUserId());
        return ResponseEntity.status(HttpStatus.CREATED).body(userModel);
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
