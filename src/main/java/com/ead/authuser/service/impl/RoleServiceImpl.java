package com.ead.authuser.service.impl;

import com.ead.authuser.repository.RoleRepository;
import com.ead.authuser.service.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RoleServiceImpl implements RoleService {

    @Autowired
    private RoleRepository roleRepository;
}
