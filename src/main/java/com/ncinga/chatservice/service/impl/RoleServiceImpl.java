package com.ncinga.chatservice.service.impl;

import com.ncinga.chatservice.document.Role;
import com.ncinga.chatservice.repository.RoleRepository;
import com.ncinga.chatservice.service.RoleService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class RoleServiceImpl implements RoleService {

    private final RoleRepository roleRepository;

    @Override
    public void createRole(Map<String, String> roleMap) {
        Role newRole = Role.builder()
                .role(roleMap.get("role").toUpperCase())
                .build();
        this.roleRepository.save(newRole);
    }
}
