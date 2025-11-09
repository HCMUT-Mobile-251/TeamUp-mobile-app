package com.teamup.main.model;

import java.io.Serializable;
import java.util.Objects;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Embeddable
@FieldDefaults(level = lombok.AccessLevel.PRIVATE)
public class PairId implements Serializable {
    String firstId;
    String secondId;

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (!(o instanceof PairId))
            return false;
        PairId that = (PairId) o;
        return Objects.equals(firstId, that.firstId) &&
                Objects.equals(secondId, that.secondId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(firstId, secondId);
    }
}
