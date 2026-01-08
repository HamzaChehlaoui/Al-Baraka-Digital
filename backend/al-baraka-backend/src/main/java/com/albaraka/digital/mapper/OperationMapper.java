package com.albaraka.digital.mapper;

import com.albaraka.digital.dto.response.DocumentDto;
import com.albaraka.digital.dto.response.OperationDto;
import com.albaraka.digital.model.Document;
import com.albaraka.digital.model.Operation;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(componentModel = "spring")
public interface OperationMapper {

    OperationMapper INSTANCE = Mappers.getMapper(OperationMapper.class);

    @Mapping(source = "account.accountNumber", target = "accountNumber")
    @Mapping(source = "account.user.name", target = "ownerName")
    OperationDto toDto(Operation operation);

    List<OperationDto> toDtoList(List<Operation> operations);

    @Mapping(source = "name", target = "fileName")
    @Mapping(source = "type", target = "fileType")
    DocumentDto toDocumentDto(Document document);

    List<DocumentDto> toDocumentDtoList(List<Document> documents);
}
