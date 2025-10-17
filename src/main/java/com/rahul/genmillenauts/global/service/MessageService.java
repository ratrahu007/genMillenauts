package com.rahul.genmillenauts.global.service;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import com.rahul.genmillenauts.global.config.MessageConfig;
import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;

@Service
public class MessageService {

    private final MessageConfig config;
    private final JavaMailSender mailSender;

    public MessageService(JavaMailSender mailSender, MessageConfig config) {
        this.mailSender = mailSender;
        this.config = config;

        // Initialize Twilio
        Twilio.init(config.getAccountSid(), config.getAuthToken());
    }

    // ==================== SMS ====================
    public void sendOtpSms(String to, String otp) {
        sendSms(to, "Your OTP is: " + otp);
    }

    public void sendAlertSms(String to, String alertMsg) {
        sendSms(to, "⚠ ALERT: " + alertMsg);
    }

    public void sendOfferSms(String to, String offerMsg) {
        sendSms(to, "🎉 Special Offer: " + offerMsg);
    }

    private void sendSms(String to, String body) {
        Message.creator(
                new PhoneNumber(to),
                new PhoneNumber(config.getMessagingSid()),
                body
        ).create();
    }

    // ==================== Email ====================
    public void sendEmail(String to, String subject, String body) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(config.getMailUsername());
            message.setTo(to);
            message.setSubject(subject);
            message.setText(body);

            mailSender.send(message);
            System.out.println("✅ Email sent to " + to);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("❌ Failed to send email", e);
        }
    }
}
