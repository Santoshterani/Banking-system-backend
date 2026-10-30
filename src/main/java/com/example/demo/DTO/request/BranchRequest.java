package com.example.demo.dto.request;

import jakarta.validation.constraints.NotBlank;

public class BranchRequest {

    @NotBlank(message = "Branch name is required")
    private String name;

    @NotBlank(message = "Address is required")
    private String address;

    @NotBlank(message = "IFSC code is required")
    private String ifscCode;

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }
    public String getIfscCode() { return ifscCode; }
    public void setIfscCode(String ifscCode) { this.ifscCode = ifscCode; }
}
