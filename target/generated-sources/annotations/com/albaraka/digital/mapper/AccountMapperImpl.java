package com.albaraka.digital.mapper;

import com.albaraka.digital.dto.response.AccountDto;
import com.albaraka.digital.model.Account;
import com.albaraka.digital.model.User;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-12-24T10:15:20+0100",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 17.0.16 (Microsoft)"
)
@Component
public class AccountMapperImpl implements AccountMapper {

    @Override
    public AccountDto toDto(Account account) {
        if ( account == null ) {
            return null;
        }

        AccountDto.AccountDtoBuilder accountDto = AccountDto.builder();

        accountDto.ownerName( accountUserName( account ) );
        accountDto.id( account.getId() );
        accountDto.accountNumber( account.getAccountNumber() );
        accountDto.balance( account.getBalance() );
        accountDto.createdAt( account.getCreatedAt() );

        return accountDto.build();
    }

    @Override
    public List<AccountDto> toDtoList(List<Account> accounts) {
        if ( accounts == null ) {
            return null;
        }

        List<AccountDto> list = new ArrayList<AccountDto>( accounts.size() );
        for ( Account account : accounts ) {
            list.add( toDto( account ) );
        }

        return list;
    }

    private String accountUserName(Account account) {
        if ( account == null ) {
            return null;
        }
        User user = account.getUser();
        if ( user == null ) {
            return null;
        }
        String name = user.getName();
        if ( name == null ) {
            return null;
        }
        return name;
    }
}
