package com.orageTechnology.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.orageTechnology.entity.Client;
import com.orageTechnology.service.ClientService;
import com.orageTechnology.service.PdfService;

import io.jsonwebtoken.io.IOException;

@RestController
@RequestMapping("/api")
public class ClientController {

	@Autowired
	private ClientService service;

	@Autowired
	private PdfService pdfService;

	@PostMapping("/create")
	public Client createClient(@RequestBody Client client) {
		return service.createClient(client);
	}

	@GetMapping("/pdf/{clientId}")
	public String savePdf(@PathVariable Long clientId) throws java.io.IOException {
		try {
			pdfService.savePdfToFile(clientId);
			return "PDF generated successfully" + clientId + ".pdf ";
		} catch (IOException e) {
			e.printStackTrace();
			return "Error generating PDF";
		}
	}

}
