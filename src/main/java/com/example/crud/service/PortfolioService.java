package com.example.crud.service;

import com.example.crud.dto.PortfolioDto;
import com.example.crud.entity.Contract;
import com.example.crud.entity.Portfolio;
import com.example.crud.exception.ResourceNotFoundException;
import com.example.crud.repository.ContractRepository;
import com.example.crud.repository.PortfolioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class PortfolioService {
    private final PortfolioRepository portfolioRepository;
    private final ContractRepository contractRepository;
    
    public List<PortfolioDto> getAllPortfolios() {
        return portfolioRepository.findAll().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }
    
    public PortfolioDto getPortfolioById(Long id) {
        Portfolio portfolio = portfolioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Portfolio not found with id: " + id));
        return convertToDto(portfolio);
    }
    
    public List<PortfolioDto> getPortfoliosByContractId(Long contractId) {
        return portfolioRepository.findByContractId(contractId).stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }
    
    public List<PortfolioDto> getPortfoliosByUserId(Long userId) {
        return portfolioRepository.findByUserId(userId).stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }
    
    public PortfolioDto createPortfolio(PortfolioDto portfolioDto) {
        Contract contract = contractRepository.findById(portfolioDto.getContractId())
                .orElseThrow(() -> new ResourceNotFoundException("Contract not found with id: " + portfolioDto.getContractId()));
        
        Portfolio portfolio = Portfolio.builder()
                .title(portfolioDto.getTitle())
                .description(portfolioDto.getDescription())
                .contract(contract)
                .build();
        
        Portfolio savedPortfolio = portfolioRepository.save(portfolio);
        return convertToDto(savedPortfolio);
    }
    
    public PortfolioDto updatePortfolio(Long id, PortfolioDto portfolioDto) {
        Portfolio portfolio = portfolioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Portfolio not found with id: " + id));
        
        portfolio.setTitle(portfolioDto.getTitle());
        portfolio.setDescription(portfolioDto.getDescription());
        
        if (portfolioDto.getContractId() != null && !portfolioDto.getContractId().equals(portfolio.getContract().getId())) {
            Contract newContract = contractRepository.findById(portfolioDto.getContractId())
                    .orElseThrow(() -> new ResourceNotFoundException("Contract not found with id: " + portfolioDto.getContractId()));
            portfolio.setContract(newContract);
        }
        
        Portfolio updatedPortfolio = portfolioRepository.save(portfolio);
        return convertToDto(updatedPortfolio);
    }
    
    public void deletePortfolio(Long id) {
        if (!portfolioRepository.existsById(id)) {
            throw new ResourceNotFoundException("Portfolio not found with id: " + id);
        }
        portfolioRepository.deleteById(id);
    }
    
    public List<PortfolioDto> searchPortfoliosByContractAndTitle(Long contractId, String title) {
        return portfolioRepository.findByContractIdAndTitleContaining(contractId, title).stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }
    
    private PortfolioDto convertToDto(Portfolio portfolio) {
        return PortfolioDto.builder()
                .id(portfolio.getId())
                .title(portfolio.getTitle())
                .description(portfolio.getDescription())
                .createdAt(portfolio.getCreatedAt())
                .contractId(portfolio.getContract().getId())
                .build();
    }
}