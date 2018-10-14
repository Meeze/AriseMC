package de.hardcorepvp.data;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@AllArgsConstructor
@Getter
@Setter
public class UserCurrency {

    private UUID uniqueId;
    private long money;
}
