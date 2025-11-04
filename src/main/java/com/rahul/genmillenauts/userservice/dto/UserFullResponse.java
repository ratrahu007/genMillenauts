package com.rahul.genmillenauts.userservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.rahul.genmillenauts.userservice.entity.User;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserFullResponse {

    private String email;       // login identity
    private String anyName;     // username / handle
    private String mobile;      // contact number
    private String fullName;    // full name
    private String city;        // city of user
    private String role;        // role (USER / ADMIN etc.)

    // ✅ Convert from entity
    public static UserFullResponse fromEntity(User user) {
        return new UserFullResponse(
            user.getEmail(),
            user.getAnyName(),
            user.getMobile(),
            user.getFullName(),
            user.getCity(),
            user.getRole()
        );
    }
}
