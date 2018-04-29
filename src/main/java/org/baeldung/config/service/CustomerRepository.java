package org.baeldung.config.service;

import org.baeldung.config.model.Customers;
import org.springframework.data.jpa.repository.JpaRepository;



public interface CustomerRepository extends JpaRepository<Customers, Integer> {
	
	Customers findOneByEmail(String email);
	Customers findByUsername(String username);

}
