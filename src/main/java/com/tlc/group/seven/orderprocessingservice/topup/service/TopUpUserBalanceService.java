package com.tlc.group.seven.orderprocessingservice.topup.service;

import com.tlc.group.seven.orderprocessingservice.authentication.model.User;
import com.tlc.group.seven.orderprocessingservice.authentication.repository.UserRepository;
import com.tlc.group.seven.orderprocessingservice.constant.ServiceConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public class TopUpUserBalanceService {

    @Autowired
    private UserRepository userRepository;
    
 
    private ServiceConstants serviceConstants;


    public ResponseEntity<?> topUpUserBalance(Long id){
        User user = userRepository.getReferenceById(id);
        user.setBalance(serviceConstants.defaultTopUpAmount);

        return ResponseEntity.status(HttpStatus.OK).body(user);
    }
}
