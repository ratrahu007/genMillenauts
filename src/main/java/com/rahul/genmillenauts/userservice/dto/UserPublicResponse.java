package com.rahul.genmillenauts.userservice.dto;

import com.rahul.genmillenauts.userservice.entity.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserPublicResponse {

    private String anyName;

    // Factory method to map from User entity
    public static UserPublicResponse fromEntity(User user) {
        if (user == null) {
            return null; // avoid NPE if user not found
        }
        return new UserPublicResponse(user.getAnyName());
    }
}
