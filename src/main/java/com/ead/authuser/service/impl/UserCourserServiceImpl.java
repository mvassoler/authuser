package com.ead.authuser.service.impl;

import com.ead.authuser.repository.UserCourseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserCourserServiceImpl {

    @Autowired
    private UserCourseRepository userCourseRepository;
}
