package com.certificaciones.backend.client;

import com.certificaciones.backend.service.RequestedServiceType;
import com.certificaciones.backend.service.ServiceRequestService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/clients")
public class ClientController {

    private final ClientService clientService;
    private final ServiceRequestService serviceRequestService;

    public ClientController(
            ClientService clientService,
            ServiceRequestService serviceRequestService) {
        this.clientService = clientService;
        this.serviceRequestService = serviceRequestService;
    }

    @PostMapping
    public Client create(@Valid @RequestBody Client client) {
        return clientService.save(client);
    }

    @GetMapping
    public List<Client> getAll() {
        return clientService.findAll();
    }

    @GetMapping("/{id}")
    public Client getById(@PathVariable Long id) {
        return clientService.findById(id);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        clientService.delete(id);
    }

    @GetMapping("/public/by-tax-id/{taxId}")
    public Client findByTaxId(@PathVariable String taxId) {
        return clientService.findByTaxId(taxId);
    }

    @PostMapping("/public/register")
    public Client register(@Valid @RequestBody ClientRegistrationRequest request) {

        Client existing = clientService.findByTaxId(request.taxId());

        Client client;

        if (existing != null) {
            existing.setBusinessName(request.businessName());
            existing.setLegalName(request.legalName());
            existing.setEmail(request.email());
            existing.setPhone(request.phone());
            existing.setAddress(request.address());
            existing.setCity(request.city());

            client = clientService.save(existing);
        } else {
            Client newClient = new Client();
            newClient.setTaxId(request.taxId());
            newClient.setBusinessName(request.businessName());
            newClient.setLegalName(request.legalName());
            newClient.setEmail(request.email());
            newClient.setPhone(request.phone());
            newClient.setAddress(request.address());
            newClient.setCity(request.city());

            client = clientService.save(newClient);
        }

        // 🔥 CREA UNA SOLICITUD POR CADA SERVICIO
        for (RequestedServiceType type : request.requestedServiceTypes()) {
            serviceRequestService.create(client, type);
        }

        return client;
    }
}