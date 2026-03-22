package com.banking.banking_api.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class AccountRequest {

    @NotBlank(message = "Owner name is required")
    private String ownerName;

    @NotBlank(message = "Account type is required")
    private String accountType; // SAVINGS or CURRENT
}
