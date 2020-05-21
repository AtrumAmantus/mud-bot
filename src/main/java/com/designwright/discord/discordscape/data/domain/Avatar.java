package com.designwright.discord.discordscape.data.domain;

import com.designwright.discord.discordscape.data.enums.Gender;
import lombok.Data;

@Data
public class Avatar {

    private Long id;
    private User user;
    private String name;
    private boolean admin;
    private Gender gender;
    private Long createDate;

}
