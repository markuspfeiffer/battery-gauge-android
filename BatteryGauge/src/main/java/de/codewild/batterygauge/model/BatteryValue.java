package de.codewild.batterygauge.model;

public class BatteryValue {

    private final int percent;

    public BatteryValue(final int percent) {
        this.percent = percent;
    }

    public int getPercent() {
        return percent;
    }
}
