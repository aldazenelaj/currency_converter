package com.currency.converter.app.service;

import com.currency.converter.app.properties.CurrencyConverterProperties;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.cache.Cache;
import org.json.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SpringBootTest
public class CurrencyConverterServiceTest {

    @Mock
    private CurrencyConverterProperties properties;

    @Mock
    private RestTemplate restTemplate;
    @Mock
    private Cache<String, JSONObject> cache;
    @InjectMocks
    private CurrencyConverterService currencyConverterService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        // Mock CurrencyConverterProperties
        when(properties.getUrl()).thenReturn("http://test.com/api");
        when(properties.getKey()).thenReturn("access_key");
        when(properties.getCurrency()).thenReturn(Arrays.asList("USD", "EUR"));

    }

    @Test
    public void testCallExternalConverterApiWithInvalidFromCurrency() {

        assertThrows(RuntimeException.class, () -> currencyConverterService.callExternalConverterApi("USD", "invalid", 1.0));
    }

    @Test
    public void testCallExternalConverterApiWithInvalidToCurrency() {
        assertThrows(RuntimeException.class, () -> currencyConverterService.callExternalConverterApi("invalid", "USD", 1.0));
    }

    @Test
    public void testCallExternalConverterApiFallback() {
        // Call the fallback method
        JSONObject fallbackResult = currencyConverterService.callExternalConverterApiFallback("USD", "EUR", 100.0, new RuntimeException("API failed"));

        // Verify that the fallback method returns the expected result
        assertEquals("{\"error\":\"EXTERNAL_API_ERRORAPI failed\",\"status\":\"500\"}", fallbackResult.toString());
    }

    @Test
    public void testCallExternalConverterApiWithValidParametersNoCache() throws Exception {

        when(cache.getIfPresent(anyString())).thenReturn(null);
        ResponseEntity<String> responseEntity = new ResponseEntity<>("{\"key\":\"value\"}", HttpStatus.OK);
        when(restTemplate.exchange(anyString(), eq(HttpMethod.GET), any(HttpEntity.class), eq(String.class)))
                .thenReturn(responseEntity);

        JSONObject result = currencyConverterService.callExternalConverterApi("USD", "EUR", 1.0);

        assertEquals(responseEntity.getBody().toString(), result.toString());
    }

    @Test
    public void testCallExternalConverterApiWithValidParametersCacheHit() throws Exception {
        when(cache.getIfPresent(anyString())).thenReturn(new JSONObject(new ObjectMapper().readTree("{\"key\": \"value\"}").toString()));

        JSONObject result = currencyConverterService.callExternalConverterApi("USD", "EUR", 1.0);

        assertEquals("{\"key\":\"value\"}", result.toString());
    }

    @Test
    public void testCallExternalConverterApi_Success() throws Exception {

        ResponseEntity<String> responseEntity = new ResponseEntity<>("{\"key\":\"value\"}", HttpStatus.OK);

        when(restTemplate.exchange(anyString(), eq(HttpMethod.GET), any(HttpEntity.class), eq(String.class)))
                .thenReturn(responseEntity);
        // Call the method
        JSONObject result = currencyConverterService.callExternalConverterApi("USD", "EUR", 100.0);

        // Verify that exchange method was called with the correct parameters
        verify(restTemplate).exchange(anyString(), eq(HttpMethod.GET), any(HttpEntity.class), eq(String.class));

        // Verify that the method returns the expected JsonNode
        assertEquals(responseEntity.getBody().toString(), result.toString());
    }

}
