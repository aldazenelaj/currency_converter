package com.currency.converter.app.controller;

import com.currency.converter.app.service.CurrencyConverterService;
import org.json.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static com.currency.converter.app.exception.ErrorMessage.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyDouble;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

public class CurrencyConverterControllerTest {
    @Mock
    private CurrencyConverterService service;

    @InjectMocks
    private CurrencyConverterController controller;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testConvertRequestCallMissingCurrencyParameter() throws Exception {
        when(service.callExternalConverterApi(anyString(), anyString(), anyDouble())).thenReturn(new JSONObject("{\"key\": \"value\"}"));

        ResponseEntity result = controller.convertRequestCall(null, "EUR", 1.0);

        assertEquals(HttpStatus.BAD_REQUEST, result.getStatusCode());
        assertEquals(MISSING_CURRENCY_PARAMETERS.getMessage(), result.getBody());
    }

    @Test
    public void testConvertRequestCallMissingToCurrencyParameter() throws Exception {
        when(service.callExternalConverterApi(anyString(), anyString(), anyDouble())).thenReturn(new JSONObject("{\"key\": \"value\"}"));

        ResponseEntity result = controller.convertRequestCall("EUR", null, 1.0);

        assertEquals(HttpStatus.BAD_REQUEST, result.getStatusCode());
        assertEquals(MISSING_CURRENCY_PARAMETERS.getMessage(), result.getBody());
    }


    @Test
    public void testConvertRequestCallMissingAmountParameter() throws Exception {
        when(service.callExternalConverterApi(anyString(), anyString(), anyDouble())).thenReturn(new JSONObject("{\"key\": \"value\"}"));

        ResponseEntity result = controller.convertRequestCall("EUR", "USD", null);

        assertEquals(HttpStatus.BAD_REQUEST, result.getStatusCode());
        assertEquals(MISSING_AMOUNT.getMessage(), result.getBody());
    }

    @Test
    public void testConvertRequestCallInvalidAmountParameter() throws Exception {
        when(service.callExternalConverterApi(anyString(), anyString(), anyDouble())).thenReturn(new JSONObject("{\"key\": \"value\"}"));

        ResponseEntity result = controller.convertRequestCall("EUR", "USD", Double.NaN);

        assertEquals(HttpStatus.BAD_REQUEST, result.getStatusCode());
        assertEquals(INVALID_AMOUNT_NOT_NUMBER.getMessage(), result.getBody());
    }

    @Test
    public void testConvertRequestCallNegativeAmountParameter() throws Exception {
        when(service.callExternalConverterApi(anyString(), anyString(), anyDouble())).thenReturn(new JSONObject("{\"key\": \"value\"}"));

        ResponseEntity result = controller.convertRequestCall("EUR", "USD", -13.0);

        assertEquals(HttpStatus.BAD_REQUEST, result.getStatusCode());
        assertEquals(INVALID_AMOUNT.getMessage(), result.getBody());
    }

    @Test
    public void testConvertRequestSuccess() throws Exception {
        when(service.callExternalConverterApi(anyString(), anyString(), anyDouble())).thenReturn(new JSONObject("{\"key\": \"value\"}"));

        ResponseEntity result = controller.convertRequestCall("EUR", "USD", 13.0);

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals("{\"key\":\"value\"}", result.getBody());
    }
}
