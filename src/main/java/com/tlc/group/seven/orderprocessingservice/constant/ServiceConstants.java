package com.tlc.group.seven.orderprocessingservice.constant;

public interface ServiceConstants {
     String exchangeURL = "https://exchange.matraining.com/9a0dfb7b-18c9-40e3-86b0-adc27cc8916c";
     String exchange2URL = "https://exchange2.matraining.com/9a0dfb7b-18c9-40e3-86b0-adc27cc8916c";

     String successStatus = "00";
     String failureStatus = "01";
     String successUserCreation = "user created successfully";
     String orderCreationSuccess = "order created successfully";
     String orderCreationFailure = "order creation failed";
     String UnsuccessfullOrderCreation = "order creation not successful";
     String insufficientBalance = "insufficient balance to make the order";
     String portfolioCannotMakeOrder = "this portfolio can not make the sell order";

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
     String portfolioDeleteSuccess = "portfolios deleting was successful";
     String portfolioDeleteFailure = "portfolios deleting was unsuccessful";
     String defaultPortfolioDeleteFailure = "default portfolio can not be deleted";
     String orderStatusOpen = "open";
     String orderStatusClose = "close";
     String defaultPortfolio = "DEFAULT";
     String topUpSuccess = "account top up successful";
     String orderCancelSuccess = "order cancellation success";
     String orderCancelFailure = "order cancellation failed or has been fulfilled";
     String userBadCredential = "password and/or email address invalid";
     String microserviceServiceName = "Order Processing";
     String userTriggeredEvent = "user triggered";
     String systemTriggeredEvent = "system triggered";
     String openOrderPortfolioDeleteFailure = "cannot delete portfolio with open orders";
}
