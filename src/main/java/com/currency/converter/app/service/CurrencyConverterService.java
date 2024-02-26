package com.currency.converter.app.service;

import com.currency.converter.app.properties.CurrencyConverterProperties;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.apache.coyote.BadRequestException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;

import static com.currency.converter.app.exception.ErrorMessage.*;

@Service
public class CurrencyConverterService {
    private static Logger log = LoggerFactory.getLogger(CurrencyConverterService.class.getName());
    private static final String ACCESS_KEY = "access_key";
    private static final String FROM = "from";
    private static final String TO = "to";
    private static final String AMOUNT = "amount";
    private CurrencyConverterProperties properties;
    private RestTemplate restTemplate = new RestTemplate();
    private Cache<String, JSONObject> cache;


    @Autowired
    public CurrencyConverterService(CurrencyConverterProperties properties) {
        this.properties = properties;
        this.cache = CacheBuilder.newBuilder()
                .maximumSize(1000)
                .expireAfterWrite(1, TimeUnit.DAYS)
                .build();
    }


    @CircuitBreaker(name = "externalConverterApi", fallbackMethod = "callExternalConverterApiFallback")
    public JSONObject callExternalConverterApi(String from, String to, Double amount) {
        JSONObject exchangeRateCache = null;
        try {
            if (!properties.getCurrency().contains(from)) {
                throw new BadRequestException(INVALID_CURRENCY_FROM.getMessage() + from);
            }
            if (!properties.getCurrency().contains(to)) {
                throw new BadRequestException(INVALID_CURRENCY_TO.getMessage() + to);
            }
            exchangeRateCache = cache.getIfPresent(from + to + LocalDate.now() + amount);
            if (exchangeRateCache == null) {
                HttpHeaders headers = new HttpHeaders();
                headers.setContentType(MediaType.APPLICATION_JSON);
                UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(properties.getUrl())
                        .queryParam(ACCESS_KEY, properties.getKey())
                        .queryParam(FROM, from)
                        .queryParam(TO, to)
                        .queryParam(AMOUNT, amount);

                ResponseEntity<String> reportResponse = this.callAPI(builder.toUriString(),
                        HttpMethod.GET, new HttpEntity<>(null, headers), String.class);
                exchangeRateCache = new JSONObject(new ObjectMapper().readTree(reportResponse.getBody()).toString());

                cache.put(from + to + LocalDate.now() + amount, exchangeRateCache);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return exchangeRateCache;

    }

    public JSONObject callExternalConverterApiFallback(String from, String to, Double amount, Throwable throwable) {
        log.error(EXTERNAL_API_ERROR.getMessage(), throwable.getMessage());
        JSONObject jsonNode = new JSONObject();
        jsonNode.put("status", "500");
        jsonNode.put("error", EXTERNAL_API_ERROR + throwable.getMessage());
        return jsonNode;
    }

    public <T> ResponseEntity<T> callAPI(String url, org.springframework.http.HttpMethod method, org.springframework.http.HttpEntity<?> requestEntity, Class<T> responseType, Object... uriVariables) {
        log.info(String.format("Calling %s %s params: %s request", method.toString(), url, Arrays.toString(uriVariables)));
        return restTemplate.exchange(url, method, requestEntity, responseType, uriVariables);
    }


}
