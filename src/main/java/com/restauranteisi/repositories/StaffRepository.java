package com.restauranteisi.repositories;

import com.restauranteisi.models.StaffMember;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StaffRepository extends MongoRepository<StaffMember, String> {
    StaffMember findByUsername(String username);
}
