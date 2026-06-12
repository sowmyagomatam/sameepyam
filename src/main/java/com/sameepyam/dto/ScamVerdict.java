package com.sameepyam.dto;



import java.util.List;

public record ScamVerdict(RiskLevel riskLevel,
                          String reason,
                          List<String> redFlags,
                          String nextSteps) {
}
