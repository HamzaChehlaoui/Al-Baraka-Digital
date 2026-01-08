package com.albaraka.digital.mapper;

import com.albaraka.digital.dto.response.AccountDto;
import com.albaraka.digital.model.Account;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(componentModel = "spring")
public interface AccountMapper {

    AccountMapper INSTANCE = Mappers.getMapper(AccountMapper.class);

    @Mapping(source = "user.name", target = "ownerName")
    AccountDto toDto(Account account);

    List<AccountDto> toDtoList(List<Account> accounts);
}
