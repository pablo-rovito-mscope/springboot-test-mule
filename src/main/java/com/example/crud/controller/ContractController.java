package com.example.crud.controller;

import com.example.crud.dto.ContractDto;
import com.example.crud.service.ContractService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/contracts")
@RequiredArgsConstructor
public class ContractController {
    private final ContractService contractService;
    
    @GetMapping
    public ResponseEntity<List<ContractDto>> getAllContracts() {
        List<ContractDto> contracts = contractService.getAllContracts();
        return ResponseEntity.ok(contracts);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<ContractDto> getContractById(@PathVariable Long id) {
        ContractDto contract = contractService.getContractById(id);
        return ResponseEntity.ok(contract);
    }
    
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<ContractDto>> getContractsByUserId(@PathVariable Long userId) {
        List<ContractDto> contracts = contractService.getContractsByUserId(userId);
        return ResponseEntity.ok(contracts);
    }
    
    @GetMapping("/user/{userId}/search")
    public ResponseEntity<List<ContractDto>> searchContractsByUserAndName(
            @PathVariable Long userId,
            @RequestParam String name) {
        List<ContractDto> contracts = contractService.searchContractsByUserAndName(userId, name);
        return ResponseEntity.ok(contracts);
    }
    
    @GetMapping("/user/{userId}/count")
    public ResponseEntity<Long> countContractsByUserId(@PathVariable Long userId) {
        long count = contractService.countContractsByUserId(userId);
        return ResponseEntity.ok(count);
    }
    
    @PostMapping
    public ResponseEntity<ContractDto> createContract(@RequestBody ContractDto contractDto) {
        ContractDto createdContract = contractService.createContract(contractDto);
        return new ResponseEntity<>(createdContract, HttpStatus.CREATED);
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<ContractDto> updateContract(@PathVariable Long id, @RequestBody ContractDto contractDto) {
        ContractDto updatedContract = contractService.updateContract(id, contractDto);
        return ResponseEntity.ok(updatedContract);
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteContract(@PathVariable Long id) {
        contractService.deleteContract(id);
        return ResponseEntity.noContent().build();
    }
}