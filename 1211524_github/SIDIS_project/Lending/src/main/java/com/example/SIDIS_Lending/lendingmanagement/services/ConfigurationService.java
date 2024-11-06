package com.example.SIDIS_Lending.lendingmanagement.services;

import com.example.SIDIS_Lending.lendingmanagement.model.Fine;
import com.example.SIDIS_Lending.lendingmanagement.model.Lending;
import jakarta.annotation.PostConstruct;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Service;

@Getter
@Service
@PropertySource("classpath:server/file.properties")
public class ConfigurationService {

    @Value("${lending.lendingPeriodDays}")
    private long maxLoanDays;

    @Value("${lending.pricePerDay}")
    private float pricePerDay;

    @PostConstruct
    public void init() {
        Lending.setMaxLoanDays(maxLoanDays);
        Fine.setPricePerDay(pricePerDay);
    }
}
