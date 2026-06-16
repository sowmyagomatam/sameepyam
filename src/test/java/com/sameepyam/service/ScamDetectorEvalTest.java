package com.sameepyam.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sameepyam.dto.RiskLevel;
import com.sameepyam.dto.ScamVerdict;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.ResourceLoader;

import java.io.IOException;
import java.util.List;

@SpringBootTest
@EnabledIfEnvironmentVariable(named = "OPENAI_API_KEY", matches = ".+")
public class ScamDetectorEvalTest {

    record EvalCase(String id, RiskLevel expected,String note, String text){}

    @Autowired
    ScamDetectorService service;

    @Autowired
    ObjectMapper objectMapper;

    @Test
    void reportScamDetectionMetrics() throws IOException {

        List<EvalCase> evalCases = objectMapper.readValue(new ClassPathResource("/eval/scam-eval.json").getInputStream(),
                new TypeReference<List<EvalCase>>() {});
       int correct = 0, safeTotal = 0, safeFlagged = 0, scamTotal = 0, scamCaught = 0;

        for(EvalCase evalCase : evalCases){
            ScamVerdict scamVerdict = service.checkForScam(evalCase.text());
            if(scamVerdict.riskLevel() == evalCase.expected()) correct++;

            if(evalCase.expected() == RiskLevel.SAFE) {
                safeTotal++;
                if(scamVerdict.riskLevel() != RiskLevel.SAFE) safeFlagged++;
            }
            if(evalCase.expected() == RiskLevel.LIKELY_SCAM) {
                scamTotal++;
                if(scamVerdict.riskLevel() != RiskLevel.SAFE) scamCaught++;
            }
            System.out.printf("%-22s exp=%-12s got=%-12s %s%n",
                    evalCase.id(), evalCase.expected(), scamVerdict, scamVerdict.riskLevel() == evalCase.expected() ? "" : "  <-- MISS");

        }

        System.out.printf("%nAccuracy: %d/%d  |  False-positive rate: %.0f%%  |  Scam recall: %.0f%%%n",
                correct, evalCases.size(),
                100.0 * safeFlagged / safeTotal,
                100.0 * scamCaught / scamTotal);


    }
}
