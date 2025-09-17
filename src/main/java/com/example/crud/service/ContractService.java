package com.example.crud.service;

import com.example.crud.dto.ContractDto;
import com.example.crud.entity.Contract;
import com.example.crud.entity.User;
import com.example.crud.exception.ResourceNotFoundException;
import com.example.crud.repository.ContractRepository;
import com.example.crud.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class ContractService {
    private final ContractRepository contractRepository;
    private final UserRepository userRepository;
    
    public List<ContractDto> getAllContracts() {
        return contractRepository.findAll().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }
    
    public ContractDto getContractById(Long id) {
        Contract contract = contractRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Contract not found with id: " + id));
        return convertToDto(contract);
    }
    
    public List<ContractDto> getContractsByUserId(Long userId) {
        return contractRepository.findByUserId(userId).stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }
    
    public ContractDto createContract(ContractDto contractDto) {
        User user = userRepository.findById(contractDto.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + contractDto.getUserId()));
        
        Contract contract = Contract.builder()
                .name(contractDto.getName())
                .value(contractDto.getValue())
                .user(user)
                .build();
        
        Contract savedContract = contractRepository.save(contract);
        return convertToDto(savedContract);
    }
    
    public ContractDto updateContract(Long id, ContractDto contractDto) {
        Contract contract = contractRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Contract not found with id: " + id));
        
        contract.setName(contractDto.getName());
        contract.setValue(contractDto.getValue());
        
        if (contractDto.getUserId() != null && !contractDto.getUserId().equals(contract.getUser().getId())) {
            User newUser = userRepository.findById(contractDto.getUserId())
                    .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + contractDto.getUserId()));
            contract.setUser(newUser);
        }
        
        Contract updatedContract = contractRepository.save(contract);
        return convertToDto(updatedContract);
    }
    
    public void deleteContract(Long id) {
        if (!contractRepository.existsById(id)) {
            throw new ResourceNotFoundException("Contract not found with id: " + id);
        }
        contractRepository.deleteById(id);
    }
    
    public List<ContractDto> searchContractsByUserAndName(Long userId, String name) {
        return contractRepository.findByUserIdAndNameContaining(userId, name).stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }
    
    public long countContractsByUserId(Long userId) {
        return contractRepository.countByUserId(userId);
    }
    
    private ContractDto convertToDto(Contract contract) {
        return ContractDto.builder()
                .id(contract.getId())
                .name(contract.getName())
                .value(contract.getValue())
                .createdAt(contract.getCreatedAt())
                .userId(contract.getUser().getId())
                .build();
    }
}