package com.albaraka.digital.mapper;

import com.albaraka.digital.dto.response.DocumentDto;
import com.albaraka.digital.dto.response.OperationDto;
import com.albaraka.digital.model.Account;
import com.albaraka.digital.model.Document;
import com.albaraka.digital.model.Operation;
import com.albaraka.digital.model.User;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-12-24T10:15:19+0100",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 17.0.16 (Microsoft)"
)
@Component
public class OperationMapperImpl implements OperationMapper {

    @Override
    public OperationDto toDto(Operation operation) {
        if ( operation == null ) {
            return null;
        }

        OperationDto.OperationDtoBuilder operationDto = OperationDto.builder();

        operationDto.accountNumber( operationAccountAccountNumber( operation ) );
        operationDto.ownerName( operationAccountUserName( operation ) );
        operationDto.id( operation.getId() );
        operationDto.type( operation.getType() );
        operationDto.amount( operation.getAmount() );
        operationDto.status( operation.getStatus() );
        operationDto.date( operation.getDate() );
        operationDto.description( operation.getDescription() );
        operationDto.documents( toDocumentDtoList( operation.getDocuments() ) );

        return operationDto.build();
    }

    @Override
    public List<OperationDto> toDtoList(List<Operation> operations) {
        if ( operations == null ) {
            return null;
        }

        List<OperationDto> list = new ArrayList<OperationDto>( operations.size() );
        for ( Operation operation : operations ) {
            list.add( toDto( operation ) );
        }

        return list;
    }

    @Override
    public DocumentDto toDocumentDto(Document document) {
        if ( document == null ) {
            return null;
        }

        DocumentDto.DocumentDtoBuilder documentDto = DocumentDto.builder();

        documentDto.fileName( document.getName() );
        documentDto.fileType( document.getType() );
        documentDto.id( document.getId() );
        documentDto.uploadedAt( document.getUploadedAt() );

        return documentDto.build();
    }

    @Override
    public List<DocumentDto> toDocumentDtoList(List<Document> documents) {
        if ( documents == null ) {
            return null;
        }

        List<DocumentDto> list = new ArrayList<DocumentDto>( documents.size() );
        for ( Document document : documents ) {
            list.add( toDocumentDto( document ) );
        }

        return list;
    }

    private String operationAccountAccountNumber(Operation operation) {
        if ( operation == null ) {
            return null;
        }
        Account account = operation.getAccount();
        if ( account == null ) {
            return null;
        }
        String accountNumber = account.getAccountNumber();
        if ( accountNumber == null ) {
            return null;
        }
        return accountNumber;
    }

    private String operationAccountUserName(Operation operation) {
        if ( operation == null ) {
            return null;
        }
        Account account = operation.getAccount();
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
