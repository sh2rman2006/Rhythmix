package com.rhythmix.coreservice.service;

import com.rhythmix.coreservice.dto.create.AddUserCustomize;
import com.rhythmix.coreservice.entity.RhythmixUser;

import java.security.Principal;
import java.util.Optional;

public interface UserService {

    Optional<RhythmixUser> getUser(Principal principal);

    RhythmixUser addUserCustomization(AddUserCustomize addUserCustomize, Principal principal);
}
