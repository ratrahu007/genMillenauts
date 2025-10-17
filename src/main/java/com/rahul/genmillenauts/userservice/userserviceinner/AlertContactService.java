package com.rahul.genmillenauts.userservice.userserviceinner;

import com.rahul.genmillenauts.userservice.dto.AlertContactDto;
import com.rahul.genmillenauts.userservice.entity.AlertContact;
import com.rahul.genmillenauts.userservice.entity.User;
import com.rahul.genmillenauts.userservice.repository.AlertContactRepository;
import com.rahul.genmillenauts.userservice.repository.UserRepository;
import com.rahul.genmillenauts.global.service.MessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AlertContactService {

    private final AlertContactRepository alertContactRepository;
    private final UserRepository userRepository;
    private final MessageService messageService; // ✅ for SMS

    public AlertContactDto addContact(Long userId, AlertContactDto dto) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        AlertContact contact = AlertContact.builder()
                .name(dto.getName())
                .phone(dto.getPhone())
                .relation(dto.getRelation())
                .user(user)
                .build();

        AlertContact saved = alertContactRepository.save(contact);
        dto.setId(saved.getId());

        // ✅ SMS notification
        String alertMsg = "You have been added as an alert contact for user: " + user.getFullName();
        try {
            messageService.sendAlertSms(contact.getPhone(), alertMsg);
        } catch (Exception e) {
            System.out.println("⚠ Failed to send SMS to " + contact.getPhone());
        }

        return dto;
    }

    public List<AlertContactDto> listContacts(Long userId) {
        return alertContactRepository.findByUserId(userId)
                .stream()
                .map(c -> {
                    AlertContactDto dto = new AlertContactDto();
                    dto.setId(c.getId());
                    dto.setName(c.getName());
                    dto.setPhone(c.getPhone());
                    dto.setRelation(c.getRelation());
                    return dto;
                })
                .collect(Collectors.toList());
    }

    public void deleteContact(Long userId, Long contactId) {
        AlertContact contact = alertContactRepository.findById(contactId)
                .orElseThrow(() -> new RuntimeException("Contact not found"));

        if (!contact.getUser().getId().equals(userId)) {
            throw new RuntimeException("You are not allowed to delete this contact");
        }

        alertContactRepository.delete(contact);
    }
}
