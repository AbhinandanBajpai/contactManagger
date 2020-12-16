package com.amart.Dao;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.amart.entities.Contact;

public interface ContactRepository extends JpaRepository<Contact, Integer> {
	// Pageable contains two informations 1). contacts per page 2). current page=5
	@Query("from Contact as c where c.user.id =:userId")
	public Page<Contact> findContactsByUser(@Param("userId") int userId, Pageable pageable);

	@Query(value = "delete from author a where a.usee_id= :userId", nativeQuery = true)
	public Contact deleteByUserId(@Param("userId") int userId);

}
