package com.mihail.baciu.account.transfer.service;

import com.google.gson.Gson;
import com.mihail.baciu.account.transfer.config.WebClientConfig;
import com.mihail.baciu.account.transfer.domain.Account;
import com.mihail.baciu.account.transfer.dto.AccountTransferRequestDto;
import com.mihail.baciu.account.transfer.dto.ExchangeRatesApiDto;
import com.mihail.baciu.account.transfer.repository.AccountRepository;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.util.HashMap;

import static com.mihail.baciu.account.transfer.constants.TestConstants.*;
import static java.lang.String.format;
import static java.util.UUID.randomUUID;
import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.when;
import static org.springframework.http.HttpHeaders.CONTENT_TYPE;
import static org.springframework.test.util.ReflectionTestUtils.setField;
import static org.springframework.util.MimeTypeUtils.APPLICATION_JSON_VALUE;

@RunWith(SpringRunner.class)
public class AccountTransferServiceTest {

    private static final Gson GSON = new Gson();
    public static MockWebServer mockBackEnd;
    private static final String apiKey = randomUUID().toString();
    @InjectMocks
    AccountTransferService accountTransferService;
    @Mock
    AccountRepository accountRepository;

    @Before
    public void init() throws IOException {
        mockBackEnd = new MockWebServer();
        mockBackEnd.start();

        String baseUrl = format("http://localhost:%s", mockBackEnd.getPort());

        setField(accountTransferService, "apiKey", apiKey);
        setField(accountTransferService, "apiUrl", baseUrl + API_FX_URL);
    }

    @After
    public void tearDown() throws IOException {
        mockBackEnd.shutdown();
    }

    @Test
    public void doTransfer_whenCalled_expectSuccess() throws InterruptedException {
        var sourceAccount = new Account(OWNER_ID_1, CURRENCY_1, BALANCE_1);
        var destinationAccount = new Account(OWNER_ID_2, CURRENCY_2, BALANCE_2);
        when(accountRepository.getAccountByOwnerId(eq(OWNER_ID_1))).thenReturn(sourceAccount);
        when(accountRepository.getAccountByOwnerId(eq(OWNER_ID_2))).thenReturn(destinationAccount);

        var responseFx = new HashMap<String, Double>();
        responseFx.put(CURRENCY, CURRENCY_FX);
        var response = new ExchangeRatesApiDto(responseFx);

        mockBackEnd.enqueue(new MockResponse()
                .setBody(GSON.toJson(response))
                .addHeader(CONTENT_TYPE, APPLICATION_JSON_VALUE));

        var serviceMethodResponse = accountTransferService.doTransfer(new AccountTransferRequestDto(OWNER_ID_1, OWNER_ID_2, AMOUNT));

        mockBackEnd.takeRequest();

        doReturn(sourceAccount).when(accountRepository).save(any());

        assertEquals(RESPONSE_MESSAGE, serviceMethodResponse);
    }

    @Test(expected = ResponseStatusException.class)
    public void doTransfer_whenCalled_expectResponseStatusException_1(){
        when(accountRepository.getAccountByOwnerId(eq(OWNER_ID_1))).thenReturn(null);
        accountTransferService.doTransfer(new AccountTransferRequestDto(OWNER_ID_1, OWNER_ID_2, AMOUNT));

    }

    @Test(expected = ResponseStatusException.class)
    public void doTransfer_whenCalled_expectResponseStatusException_2(){
        var sourceAccount = new Account(OWNER_ID_1, CURRENCY_1, BALANCE_1);
        when(accountRepository.getAccountByOwnerId(eq(OWNER_ID_1))).thenReturn(sourceAccount);
        when(accountRepository.getAccountByOwnerId(eq(OWNER_ID_2))).thenReturn(null);
        accountTransferService.doTransfer(new AccountTransferRequestDto(OWNER_ID_1, OWNER_ID_2, AMOUNT));

    }

    @Test(expected = ResponseStatusException.class)
    public void doTransfer_whenCalled_expectResponseStatusException_3(){
        var sourceAccount = new Account(OWNER_ID_1, CURRENCY_1, BALANCE_1);
        var destinationAccount = new Account(OWNER_ID_2, CURRENCY_2, BALANCE_2);
        when(accountRepository.getAccountByOwnerId(eq(OWNER_ID_1))).thenReturn(sourceAccount);
        when(accountRepository.getAccountByOwnerId(eq(OWNER_ID_2))).thenReturn(destinationAccount);
        accountTransferService.doTransfer(new AccountTransferRequestDto(OWNER_ID_1, OWNER_ID_2, AMOUNT_TO_BIG));


    }
}
