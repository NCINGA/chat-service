package com.ncinga.chatservice.controllers;

import com.ncinga.chatservice.service.RoleService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

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

    @GetMapping(path = "/welcome")
    public String create() {
        return "Welcome!";
    }

}
