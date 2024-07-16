package com.safety.law.global.external;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.service.annotation.GetExchange;
import org.springframework.web.service.annotation.HttpExchange;

import com.safety.law.domain.scheduler.model.LawRS;

@Component
@HttpExchange
public interface PublicDataPotalComponent {

    @GetExchange("/B552468/srch/smartSearch")
    public ResponseEntity<LawRS> findLawDataByQuery(
        @RequestParam String serviceKey,
        @RequestParam Integer pageNo,
        @RequestParam Integer numOfRows,
        @RequestParam String searchValue,
        @RequestParam Integer category
    );

    @GetExchange("/B552468/srch/smartSearch")
    public ResponseEntity<String> findLawDataErrorByQuery(
        @RequestParam String serviceKey,
        @RequestParam Integer pageNo,
        @RequestParam Integer numOfRows,
        @RequestParam String searchValue,
        @RequestParam Integer category
    );

}
