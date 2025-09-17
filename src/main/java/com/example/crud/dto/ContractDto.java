package com.example.crud.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ContractDto {
    private Long id;
    private String name;
    private BigDecimal value;
    private LocalDateTime createdAt;
    private Long userId;
    private List<PortfolioDto> portfolios;
}