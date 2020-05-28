package com.designwright.discord.mudbot.data.domain;

import com.designwright.discord.mudbot.data.enums.Gender;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;

@Data
@NoArgsConstructor
public class Avatar {

    private Long id;
    private User user;
    private String name;
    private boolean admin;
    private Gender gender;
    private Long createDate;

    public Avatar(Avatar original) {
        this.id = original.id;
        this.user = original.user;
        this.name = original.name;
        this.admin = original.admin;
        this.gender = original.gender;
        this.createDate = original.createDate;
    }

    public boolean isValid() {
        return name != null && gender != null;
    }

    public String getName() {
        return StringUtils.capitalize(this.name);
    }

}
