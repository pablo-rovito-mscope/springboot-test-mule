package com.example.crud.controller;

import com.example.crud.dto.PortfolioDto;
import com.example.crud.service.PortfolioService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/portfolios")
@RequiredArgsConstructor
public class PortfolioController {
    private final PortfolioService portfolioService;
    
    @GetMapping
    public ResponseEntity<List<PortfolioDto>> getAllPortfolios() {
        List<PortfolioDto> portfolios = portfolioService.getAllPortfolios();
        return ResponseEntity.ok(portfolios);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<PortfolioDto> getPortfolioById(@PathVariable Long id) {
        PortfolioDto portfolio = portfolioService.getPortfolioById(id);
        return ResponseEntity.ok(portfolio);
    }
    
    @GetMapping("/contract/{contractId}")
    public ResponseEntity<List<PortfolioDto>> getPortfoliosByContractId(@PathVariable Long contractId) {
        List<PortfolioDto> portfolios = portfolioService.getPortfoliosByContractId(contractId);
        return ResponseEntity.ok(portfolios);
    }
    
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<PortfolioDto>> getPortfoliosByUserId(@PathVariable Long userId) {
        List<PortfolioDto> portfolios = portfolioService.getPortfoliosByUserId(userId);
        return ResponseEntity.ok(portfolios);
    }
    
    @GetMapping("/contract/{contractId}/search")
    public ResponseEntity<List<PortfolioDto>> searchPortfoliosByContractAndTitle(
            @PathVariable Long contractId,
            @RequestParam String title) {
        List<PortfolioDto> portfolios = portfolioService.searchPortfoliosByContractAndTitle(contractId, title);
        return ResponseEntity.ok(portfolios);
    }
    
    @PostMapping
    public ResponseEntity<PortfolioDto> createPortfolio(@RequestBody PortfolioDto portfolioDto) {
        PortfolioDto createdPortfolio = portfolioService.createPortfolio(portfolioDto);
        return new ResponseEntity<>(createdPortfolio, HttpStatus.CREATED);
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<PortfolioDto> updatePortfolio(@PathVariable Long id, @RequestBody PortfolioDto portfolioDto) {
        PortfolioDto updatedPortfolio = portfolioService.updatePortfolio(id, portfolioDto);
        return ResponseEntity.ok(updatedPortfolio);
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePortfolio(@PathVariable Long id) {
        portfolioService.deletePortfolio(id);
        return ResponseEntity.noContent().build();
    }
}