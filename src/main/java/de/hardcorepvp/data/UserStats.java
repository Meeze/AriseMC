package de.hardcorepvp.data;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.UUID;

@AllArgsConstructor
@Getter
@Setter
public class UserStats {

    private UUID uniqueId;
    private int kills;
    private int deaths;

    public double getKD() {
        if (getKills() <= 0) {
            return 0.0D;
        }
        if (getDeaths() <= 0) {
            return getKills();
        }
        BigDecimal bigDecimal = new BigDecimal(getKills() / getDeaths());
        bigDecimal.setScale(2, 4);
        return bigDecimal.doubleValue();
    }
}
