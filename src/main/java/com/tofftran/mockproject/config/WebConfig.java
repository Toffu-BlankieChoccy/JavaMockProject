package com.tofftran.mockproject.config;

import com.tofftran.mockproject.data.dto.BorrowingDTO;
import com.tofftran.mockproject.data.entity.Borrowing;
import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class WebConfig {
    @Bean
    public ModelMapper modelMapper(){
        return new ModelMapper();
    }

}
