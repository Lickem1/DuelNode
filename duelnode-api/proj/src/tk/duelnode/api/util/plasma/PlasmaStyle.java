package tk.duelnode.api.util.plasma;

import lombok.Getter;

@Getter
public enum PlasmaStyle {

    DESCEND(true, 15),
    NEGATIVE(true, -1),
    MODERN(false, 1);

    private final boolean decending;
    private final int startNumber;

    PlasmaStyle(boolean decending, int startNumber) {
        this.decending = decending;
        this.startNumber = startNumber;
    }
}
