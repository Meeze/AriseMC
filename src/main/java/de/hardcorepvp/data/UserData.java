package de.hardcorepvp.data;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@AllArgsConstructor
@Getter
@Setter
public class UserData {

    private UUID uniqueId;

    private String lastName;
    private String lastIP;
    private String realName;
    private String discordName;
    private String teamspeakName;
    private String skypeName;

    private long onlineTime;
    private long firstLogin;
    private long lastLogin;
}
