// Full profile (for self)
package com.rahul.genmillenauts.userservice.dto;



import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.rahul.genmillenauts.userservice.entity.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserFullResponse {
    private String email;     // login identity
    private String anyName;   // public name
    private String phone;     // other private info

    public static UserFullResponse fromEntity(User user) {
        return new UserFullResponse(
                user.getEmail(),   // email
                user.getAnyName(),
                user.getMobile()
        );
    }
}

// Public profile (for others)


