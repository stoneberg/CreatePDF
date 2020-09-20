package com.blog.example.CreatePDF.dto;

import lombok.Data;

@Data
public class UserInfo {
    private String title;
    private String firstname;
    private String lastname;
    private String street;
    private String zipCode;
    private String city;
}
