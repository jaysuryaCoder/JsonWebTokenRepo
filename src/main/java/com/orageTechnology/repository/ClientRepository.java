package com.orageTechnology.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.orageTechnology.entity.Client;


@Repository
public interface ClientRepository extends JpaRepository<Client, Long> {

}
