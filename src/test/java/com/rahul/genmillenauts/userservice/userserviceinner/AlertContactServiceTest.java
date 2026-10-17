
package com.rahul.genmillenauts.userservice.userserviceinner;

import com.rahul.genmillenauts.global.service.MessageService;
import com.rahul.genmillenauts.userservice.dto.AlertContactDto;
import com.rahul.genmillenauts.userservice.entity.AlertContact;
import com.rahul.genmillenauts.userservice.entity.User;
import com.rahul.genmillenauts.userservice.repository.AlertContactRepository;
import com.rahul.genmillenauts.userservice.repository.UserRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AlertContactServiceTest {

    @Mock
    private AlertContactRepository alertContactRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private MessageService messageService;

    @InjectMocks
    private AlertContactService alertContactService;

    private User user;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);
    }

    // 1. Add contact successfully
    @Test
    void addContact_ShouldSaveAndReturnContactDto() {

        AlertContactDto dto = new AlertContactDto();
        dto.setName("Rahul");
        dto.setPhone("9876543210");
        dto.setRelation("Friend");

        when(userRepository.findById(1L))
                .thenReturn(Optional.of(user));

        when(alertContactRepository.save(any(AlertContact.class)))
                .thenAnswer(invocation -> {
                    AlertContact contact = invocation.getArgument(0);
                    contact.setId(10L);
                    return contact;
                });

        AlertContactDto result =
                alertContactService.addContact(1L, dto);

        assertNotNull(result);
        assertEquals(10L, result.getId());
        assertEquals("Rahul", result.getName());
        assertEquals("9876543210", result.getPhone());
        assertEquals("Friend", result.getRelation());

        verify(userRepository).findById(1L);
        verify(alertContactRepository).save(any(AlertContact.class));
    }

    // 2. Add contact when user does not exist
    @Test
    void addContact_ShouldThrowException_WhenUserNotFound() {

        AlertContactDto dto = new AlertContactDto();
        dto.setName("Rahul");
        dto.setPhone("9876543210");
        dto.setRelation("Friend");

        when(userRepository.findById(1L))
                .thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> alertContactService.addContact(1L, dto)
        );

        assertEquals("User not found", exception.getMessage());

        verify(alertContactRepository, never())
                .save(any(AlertContact.class));
    }

    // 3. List contacts successfully
    @Test
    void listContacts_ShouldReturnMappedDtos() {

        AlertContact contact = AlertContact.builder()
                .id(10L)
                .name("Rahul")
                .phone("9876543210")
                .relation("Friend")
                .user(user)
                .build();

        when(alertContactRepository.findByUserId(1L))
                .thenReturn(List.of(contact));

        List<AlertContactDto> result =
                alertContactService.listContacts(1L);

        assertEquals(1, result.size());

        AlertContactDto dto = result.get(0);

        assertEquals(10L, dto.getId());
        assertEquals("Rahul", dto.getName());
        assertEquals("9876543210", dto.getPhone());
        assertEquals("Friend", dto.getRelation());

        verify(alertContactRepository).findByUserId(1L);
    }

    // 4. List contacts when none exist
    @Test
    void listContacts_ShouldReturnEmptyList_WhenNoContactsExist() {

        when(alertContactRepository.findByUserId(1L))
                .thenReturn(List.of());

        List<AlertContactDto> result =
                alertContactService.listContacts(1L);

        assertNotNull(result);
        assertTrue(result.isEmpty());

        verify(alertContactRepository).findByUserId(1L);
    }

    // 5. Delete contact successfully
    @Test
    void deleteContact_ShouldDelete_WhenContactBelongsToUser() {

        AlertContact contact = AlertContact.builder()
                .id(10L)
                .name("Rahul")
                .phone("9876543210")
                .relation("Friend")
                .user(user)
                .build();

        when(alertContactRepository.findById(10L))
                .thenReturn(Optional.of(contact));

        assertDoesNotThrow(
                () -> alertContactService.deleteContact(1L, 10L)
        );

        verify(alertContactRepository).delete(contact);
    }

    // 6. Delete contact when contact does not exist
    @Test
    void deleteContact_ShouldThrowException_WhenContactNotFound() {

        when(alertContactRepository.findById(10L))
                .thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> alertContactService.deleteContact(1L, 10L)
        );

        assertEquals("Contact not found", exception.getMessage());

        verify(alertContactRepository, never())
                .delete(any(AlertContact.class));
    }

    // 7. Prevent deleting another user's contact
    @Test
    void deleteContact_ShouldThrowException_WhenContactBelongsToAnotherUser() {

        User anotherUser = new User();
        anotherUser.setId(2L);

        AlertContact contact = AlertContact.builder()
                .id(10L)
                .name("Another User")
                .phone("9876543210")
                .relation("Friend")
                .user(anotherUser)
                .build();

        when(alertContactRepository.findById(10L))
                .thenReturn(Optional.of(contact));

        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> alertContactService.deleteContact(1L, 10L)
        );

        assertEquals(
                "You are not allowed to delete this contact",
                exception.getMessage()
        );

        verify(alertContactRepository, never())
                .delete(any(AlertContact.class));
    }
}
