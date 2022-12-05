package com.tlc.group.seven.orderprocessingservice.constant;

public interface ServiceConstants {
     String exchangeURL = "https://exchange.matraining.com/1567ae46-8d44-4210-bf91-9d5c61290d0f";
     String exchange2URL = "https://exchange2.matraining.com/1567ae46-8d44-4210-bf91-9d5c61290d0f";

     String successStatus = "00";
     String failureStatus = "01";
     String successUserCreation = "user created successfully";
     String orderCreationSuccess = "order created successfully";
     String orderCreationFailure = "order creation failed";
     String UnsuccessfullOrderCreation = "order creation not successful";

     String roleNotFoundFailure = "Error: Role is not found.";

     String userNotFoundFailure = "Can not set user authentication: User not found";

     String orderGettingError= "Order can not be found";

     String nameAlreadyTaken = "name already taken";
     String emailAlreadyTaken = "email already taken";
     String userLoginSuccess = "login successful";
     String ordersGettingSuccess = "order fetch successful";
     String portfolioCreationSuccess = "portfolio creation success";
     String portfolioCreationFailure = "portfolio creation failure";
     String creationFailure = "creation failure";
     String portfolioNameTaken = "name already taken";
     String portfolioGettingSuccess = "portfolios getting success";
}
