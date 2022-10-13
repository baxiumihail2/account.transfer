package com.mihail.baciu.account.transfer.controller;

import com.google.gson.Gson;
import com.mihail.baciu.account.transfer.dto.AccountTransferRequestDto;
import com.mihail.baciu.account.transfer.service.AccountTransferService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.test.web.servlet.MockMvc;

import static com.mihail.baciu.account.transfer.constants.TestConstants.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.standaloneSetup;

@RunWith(MockitoJUnitRunner.class)
public class AccountTransferControllerTest {

    private MockMvc mockMvc;

    @Mock
    private AccountTransferService accountTransferService;

    @InjectMocks
    private AccountTransferController accountTransferController;

    private static final Gson GSON = new Gson();

    @Before
    public void init() {
        mockMvc = standaloneSetup(accountTransferController).build();
    }

    @Test
    public void doTransfer_shouldDelegateService_andReturnOk() throws Exception {
        var request = new AccountTransferRequestDto(OWNER_ID_1, OWNER_ID_2, AMOUNT);
        mockMvc.perform(post("/account-transfer").contentType(APPLICATION_JSON_VALUE).content(GSON.toJson(request)))
                .andExpect(status().isOk()).andReturn();

        verify(accountTransferService, times(1)).doTransfer(any());
    }

}
