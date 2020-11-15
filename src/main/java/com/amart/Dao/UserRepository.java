package com.amart.Dao;

import org.springframework.data.jpa.repository.JpaRepository;

import com.amart.entities.User;

public interface UserRepository extends JpaRepository<User, Integer	>{

}
