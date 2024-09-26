package com.orageTechnology.service;

import java.io.File;
import java.io.IOException;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.orageTechnology.entity.Client;
import com.orageTechnology.repository.ClientRepository;

@Service
public class PdfService {

    @Autowired
    private ClientRepository clientRepository;

    public void savePdfToFile(Long clientId) throws IOException {
        Client client = clientRepository.findById(clientId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid client ID"));

        String filePath = "C:\\Users\\hp\\Videos\\PdfStorage\\client_" + clientId + ".pdf";

        try (PDDocument document = new PDDocument()) {
            PDPage page = new PDPage();
            document.addPage(page);

            // Create a content stream to write to the PDF
            try (PDPageContentStream contentStream = new PDPageContentStream(document, page)) {
                contentStream.beginText();
                contentStream.setFont(PDType1Font.HELVETICA_BOLD, 12);
                contentStream.newLineAtOffset(100, 700);

                // Add client details to the PDF
                contentStream.showText("Client ID: " + client.getClientId());
                contentStream.newLineAtOffset(0, -15);
                contentStream.showText("Client Name: " + client.getCilentName());
                contentStream.newLineAtOffset(0, -15);
                contentStream.showText("Client Details: " + client.getClientDetails());

                contentStream.endText();
            }

            // Save the PDF to the specified location
            document.save(new File(filePath));
        }
    }
}

