package com.ead.authuser.models;

import com.ead.authuser.dtos.UserEventDto;
import com.ead.authuser.enums.UserStatus;
import com.ead.authuser.enums.UserType;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import org.springframework.beans.BeanUtils;
import org.springframework.hateoas.RepresentationModel;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@Entity
@Table(name = "TB_USER")
public class UserModel extends RepresentationModel implements Serializable {

    private static final long serialVersionUID = 1l;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "ID")
    private UUID userId;

    @Column(name = "USER_NAME", nullable = false, unique = true, length = 50)
    private String username;

    @Column(name = "EMAIL", nullable = false, unique = true, length = 100)
    private String email;

    @JsonIgnore
    @Column(name = "PASSWORD", nullable = false, length = 255)
    private String password;

    @Column(name = "FULL_NAME", nullable = false, length = 255)
    private String fullName;

    @Column(name = "USER_STATUS", nullable = false)
    @Enumerated(EnumType.STRING)
    private UserStatus userStatus;

    @Column(name = "USER_TYPE", nullable = false)
    @Enumerated(EnumType.STRING)
    private UserType userType;

    @Column(name = "PHONE_NUMBER", length = 20)
    private String phoneNumber;

    @Column(name = "CPF", length = 20)
    private String cpf;

    @Column(name = "IMAGE_URL")
    private String imageUrl;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'")
    @Column(name = "CREATION_DATE", nullable = false)
    private LocalDateTime creationDate;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'")
    @Column(name = "LAST_UPDATE_DATE")
    private LocalDateTime lastUpdateDate;

    public UserEventDto convertUserEventDto(){
        var userEventoDto = new UserEventDto();
        BeanUtils.copyProperties(this, userEventoDto);
        userEventoDto.setUserType(this.getUserType().toString());
        userEventoDto.setUserStatus(this.getUserStatus().toString());
        return userEventoDto;
    }

}
