package com.example.SIDIS_Reader.readermanagement.api;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class ListResponse<T> {
    private List<T> items;
}

