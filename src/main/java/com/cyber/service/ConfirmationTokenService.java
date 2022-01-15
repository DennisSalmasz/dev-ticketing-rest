package com.cyber.service;

import com.cyber.entity.ConfirmationToken;
import com.cyber.exception.TicketNGProjectException;
import org.springframework.mail.SimpleMailMessage;

public interface ConfirmationTokenService {

    ConfirmationToken save(ConfirmationToken confirmationToken);
    void sendEmail(SimpleMailMessage email);
    ConfirmationToken readByToken(String token) throws TicketNGProjectException;
    void delete(ConfirmationToken confirmationToken);
}
