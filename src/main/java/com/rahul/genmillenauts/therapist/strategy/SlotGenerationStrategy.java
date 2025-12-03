package com.rahul.genmillenauts.therapist.strategy;

import com.rahul.genmillenauts.therapist.dto.SlotRequest;
import com.rahul.genmillenauts.therapist.entity.Therapist;

public interface SlotGenerationStrategy {
    void generate(Therapist therapist, SlotRequest req);
}
