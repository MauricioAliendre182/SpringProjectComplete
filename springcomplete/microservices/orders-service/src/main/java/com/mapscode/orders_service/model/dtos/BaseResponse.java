package com.mapscode.orders_service.model.dtos;

// record is like an inmutable class
public record BaseResponse(String[] errorMessages) {

    // For this case we will have a method with the errors that can be produced in the service
    // This will indicate if we have errors
    public boolean hasError() {
        return errorMessages != null && errorMessages.length > 0;
    }
}
