package com.ncinga.chatservice.controllers;

import com.ncinga.chatservice.service.RoleService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping(path = "/role")
@RequiredArgsConstructor

public class RoleController {
    private final RoleService roleService;

    @PostMapping(path = "/create")
    public void create(@RequestBody Map<String, String> roleMap) {
        this.roleService.createRole(roleMap);
    }

}
