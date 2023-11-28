package com.restauranteisi.services;

import com.restauranteisi.models.StaffMember;
import com.restauranteisi.repositories.StaffRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class StaffService {
    private final StaffRepository staffRepository;

    @Autowired
    public StaffService(StaffRepository staffRepository) {
        this.staffRepository = staffRepository;
    }

    public StaffMember findByUsername(String username) {
        return staffRepository.findByUsername(username);
    }

    public boolean login(String username, String password) {
        StaffMember staffMember = findByUsername(username);
        return staffMember != null && staffMember.getPassword().equals(password);
    }

    public StaffMember register(String username, String password) {
        StaffMember staffMember = new StaffMember();
        staffMember.setUsername(username);
        staffMember.setPassword(password);
        return staffRepository.save(staffMember);
    }
}
