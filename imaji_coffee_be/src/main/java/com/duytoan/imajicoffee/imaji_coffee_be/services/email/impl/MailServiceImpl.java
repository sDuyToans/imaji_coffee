package com.duytoan.imajicoffee.imaji_coffee_be.services.email.impl;

import com.duytoan.imajicoffee.imaji_coffee_be.entities.order.Order;
import com.duytoan.imajicoffee.imaji_coffee_be.services.email.IMailService;
import com.duytoan.imajicoffee.imaji_coffee_be.services.email.template.OrderConfirmationEmailModel;
import com.duytoan.imajicoffee.imaji_coffee_be.services.email.template.OrderConfirmationEmailModelFactory;
import com.duytoan.imajicoffee.imaji_coffee_be.services.email.template.OrderConfirmationEmailTemplateRenderer;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

/**
 * Implemented MailService Interface -> Override and implement interface's methods
 * @author duytoan
 * @since 10/2025
 */
@Service
@RequiredArgsConstructor
public class MailServiceImpl implements IMailService {

    private final JavaMailSender javaMailSender;
    private final OrderConfirmationEmailModelFactory modelFactory;
    private final OrderConfirmationEmailTemplateRenderer templateRenderer;

    @Value("${spring.mail.username:imajicoffee@email.com}")
    private String fromEmail;

    /**
     * Send order info to user's email
     * @param order -> order object
     * @throws MessagingException -> message exception
     */
    @Override
    public void sendOrderInfoToEmail(Order order) throws MessagingException {
        OrderConfirmationEmailModel emailModel = modelFactory.build(order);
        String subject = "Imaji Coffee - Order " + emailModel.orderNumber() + " Confirmed";
        String htmlBody = templateRenderer.renderHtml(emailModel);
        String plainTextBody = templateRenderer.renderText(emailModel);

        MimeMessage message = javaMailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

        helper.setTo(order.getEmail());
        helper.setSubject(subject);
        helper.setFrom(fromEmail);
        helper.setText(plainTextBody, htmlBody);

        javaMailSender.send(message);
    }
}
