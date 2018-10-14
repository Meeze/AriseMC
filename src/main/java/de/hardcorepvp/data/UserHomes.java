package de.hardcorepvp.data;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.Location;

import java.util.Map;
import java.util.UUID;

@AllArgsConstructor
@Getter
public class UserHomes {

    private UUID uniqueId;
    private Map<String, Location> homes;

    public boolean hasHome(String name) {
        return homes.containsKey(name);
    }
}
