package vn.khanhduc.bookstorebackend.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import vn.khanhduc.bookstorebackend.exception.AppException;
import vn.khanhduc.bookstorebackend.exception.ErrorCode;
import vn.khanhduc.bookstorebackend.model.Role;
import vn.khanhduc.bookstorebackend.repository.RoleRepository;
import vn.khanhduc.bookstorebackend.service.RoleService;

@Service
@RequiredArgsConstructor
@Slf4j(topic = "ROLE-SERVICE")
public class RoleServiceImpl implements RoleService {

    private final RoleRepository roleRepository;

    @Override
    public void createRole(Role role) {
        log.info("Create role: {}", role);
        if(roleRepository.existsByName(role.getName()))
            throw new AppException(ErrorCode.ROLE_EXISTED);
        roleRepository.save(role);
    }
}
