package com.sameepyam.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ScamCheckRequest(@NotBlank
                               @Size(max = 4000)
                               String text) {
}
