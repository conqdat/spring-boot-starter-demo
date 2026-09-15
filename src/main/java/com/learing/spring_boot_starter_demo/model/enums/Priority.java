package com.learing.spring_boot_starter_demo.model.enums;

public enum Priority {
    LOW(1),
    MEDIUM(2),
    HIGH(3),
    URGENT(4);

    private final int level;

    Priority(int level) {
        this.level = level;
    }

    public int getLevel() {
        return level;
    }

    public boolean isHigherThan(Priority other) {
        return this.level > other.level;
    }

    public Priority getEscalationTarget() {
        return switch (this) {
            case LOW -> MEDIUM;
            case MEDIUM -> HIGH;
            case HIGH, URGENT -> URGENT;
        };
    }
}
