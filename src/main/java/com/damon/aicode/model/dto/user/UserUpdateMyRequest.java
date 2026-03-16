package com.damon.aicode.model.dto.user;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * Current user self-update request.
 */
@Data
public class UserUpdateMyRequest implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * Optional client-side id. Ignored by the server.
     */
    private Long id;

    /**
     * User display name.
     */
    private String userName;

    /**
     * User avatar.
     */
    private String userAvatar;

    /**
     * User profile.
     */
    private String userProfile;

    /**
     * Optional client-side role. Ignored by the server.
     */
    private String userRole;
}
