package com.duytoan.imajicoffee.imaji_coffee_be.services.email;

import com.duytoan.imajicoffee.imaji_coffee_be.entities.order.Order;
import jakarta.mail.MessagingException;

/**
 * Mail interface contains method's name and parameters
 * @author duytoan
 * @since 10/2025
 */
public interface IMailService {
    /**
     * Send order info to user's email
     * @param order -> order object
     * @throws MessagingException -> message exception
     */
    void sendOrderInfoToEmail(Order order) throws MessagingException;
}
