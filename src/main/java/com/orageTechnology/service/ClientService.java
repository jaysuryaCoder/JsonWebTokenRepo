package com.orageTechnology.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.orageTechnology.entity.Client;
import com.orageTechnology.repository.ClientRepository;


@Service
public class ClientService {
	
	@Autowired
	private ClientRepository repository;
	
	public Client createClient(Client client) {
		return repository.save(client);
	}
	

}
