package com.teamup.main.model;

import java.io.Serializable;
import java.util.Objects;

public class PairId implements Serializable {
    private String firstId;
    private String secondId;

    public PairId() {}
    public PairId(String firstId, String secondId) {
        this.firstId = firstId;
        this.secondId = secondId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof PairId)) return false;
        PairId that = (PairId) o;
        return Objects.equals(firstId, that.firstId) &&
               Objects.equals(secondId, that.secondId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(firstId, secondId);
    }
}
