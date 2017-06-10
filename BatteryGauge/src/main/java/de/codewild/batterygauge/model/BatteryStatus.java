package de.codewild.batterygauge.model;

public class BatteryStatus {

    private final boolean isCharging;

    public BatteryStatus(final boolean isCharging) {
        this.isCharging = isCharging;
    }

    public boolean isCharging() {
        return isCharging;
    }
}
