package com.ead.authuser.dtos;

import com.ead.authuser.enums.CourseLevel;
import com.ead.authuser.enums.CourseStatus;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.util.UUID;

@Data
//JsonIgnoreProperties = Se houver alterações no DTO do microservice course não quebra o DTO do client
@JsonIgnoreProperties(ignoreUnknown = true)
public class CourseDto {

    private UUID courseId;
    private String name;
    private String description;
    private String imageUrl;
    private CourseStatus courseStatus;
    private CourseLevel courseLevel;
    private UUID userInstructor;

}
