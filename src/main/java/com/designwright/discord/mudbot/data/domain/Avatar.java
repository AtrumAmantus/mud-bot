package com.designwright.discord.mudbot.data.domain;

import com.designwright.discord.mudbot.data.enums.Gender;
import lombok.Data;

@Data
public class Avatar {

    private Long id;
    private User user;
    private String name;
    private boolean admin;
    private Gender gender;
    private Long createDate;

    public boolean isValid() {
        return name != null && gender != null;
    }

}
