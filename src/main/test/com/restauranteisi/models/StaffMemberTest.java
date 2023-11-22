package com.restauranteisi.models;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class StaffMemberTest {
    @Test
    @DisplayName("Prueba de getters y setters")
    void testGettersAndSetters() {
        StaffMember staffMember = new StaffMember();
        String id = "123";
        String username = "john_doe";
        String password = "secret123";

        staffMember.setId(id);
        staffMember.setUsername(username);
        staffMember.setPassword(password);

        assertEquals(id, staffMember.getId());
        assertEquals(username, staffMember.getUsername());
        assertEquals(password, staffMember.getPassword());
    }
}
