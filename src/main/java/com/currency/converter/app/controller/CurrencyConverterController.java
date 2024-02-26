package com.currency.converter.app.controller;

import com.currency.converter.app.service.CurrencyConverterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.ExecutionException;

import static com.currency.converter.app.exception.ErrorMessage.*;

@RestController
@RequestMapping("/currency-converter")

public class CurrencyConverterController {

    private CurrencyConverterService service;

    @Autowired
    public CurrencyConverterController(CurrencyConverterService service) {
        this.service = service;
    }

    @RequestMapping(value = "/convert", method = RequestMethod.GET)
    public ResponseEntity convertRequestCall(@RequestParam(required = true) String fromCurrency,
                                             @RequestParam(required = true) String toCurrency,
                                             @RequestParam(required = true) Double amount) throws ExecutionException, InterruptedException {
        if (fromCurrency == null || fromCurrency.isEmpty() ||
                toCurrency == null || toCurrency.isEmpty()) {
            return ResponseEntity.badRequest().body(MISSING_CURRENCY_PARAMETERS.getMessage());// Response.print(HttpStatus.BAD_REQUEST.toString(), MISSING_CURRENCY_PARAMETERS.getMessage());
        }
        if (amount == null) {
            return ResponseEntity.badRequest().body(MISSING_AMOUNT.getMessage());
        }
        if (amount.isNaN()) {
            return ResponseEntity.badRequest().body(INVALID_AMOUNT_NOT_NUMBER.getMessage());
        }
        if (amount <= 0) {
            return ResponseEntity.badRequest().body(INVALID_AMOUNT.getMessage());
        }
        return ResponseEntity.ok().body(service.callExternalConverterApi(fromCurrency, toCurrency, amount));
    }
}
