package com.rafaelamaral.dscatalog.repositories;

import com.rafaelamaral.dscatalog.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User , Long> {

    User findByEmail(String email);

}
