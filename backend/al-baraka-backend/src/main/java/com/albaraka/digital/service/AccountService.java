package com.albaraka.digital.service;

import com.albaraka.digital.dto.response.AccountDto;
import com.albaraka.digital.model.Account;
import com.albaraka.digital.model.User;

import java.math.BigDecimal;
import java.util.List;

public interface AccountService {

    Account createAccountForUser(User user);

    List<AccountDto> getAccountsByUserId(Long userId);

    AccountDto getAccountByNumber(String accountNumber);

    void updateBalance(Long accountId, BigDecimal amount);

    Account getAccountEntityByNumber(String accountNumber);

    Account getAccountEntityById(Long accountId);
}
