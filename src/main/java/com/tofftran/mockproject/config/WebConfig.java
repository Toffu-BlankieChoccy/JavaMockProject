package com.tofftran.mockproject.config;

import com.tofftran.mockproject.data.dto.BorrowingDTO;
import com.tofftran.mockproject.data.entity.Borrowing;
import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class WebConfig {
//    @Bean
//    public ModelMapper modelMapper(){
//        return new ModelMapper();
//    }

    @Bean
    public ModelMapper modelMapper() {
        ModelMapper modelMapper = new ModelMapper();
        modelMapper.typeMap(Borrowing.class, BorrowingDTO.class).addMappings(mapper -> {
            mapper.map(src -> src.getBook().getId(), BorrowingDTO::setBookId);
            mapper.map(src -> src.getUser().getId(), BorrowingDTO::setUserId);
            mapper.map(Borrowing::getBorrowDate, BorrowingDTO::setBorrowDate);
            mapper.map(Borrowing::getReturnDate, BorrowingDTO::setReturnDate);
        });
        return modelMapper;
    }
}
