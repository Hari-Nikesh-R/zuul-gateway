package com.example.zuulgateway.repository;


import com.example.zuulgateway.entity.AdminDetails;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends CrudRepository<AdminDetails, Integer> {
    Optional<AdminDetails> findByEmail(String email);
}
