package com.slotify.backend.spring.company.models;

import com.slotify.backend.spring.service.models.ServiceDocument;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.util.List;

@Document(indexName = "companies")
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@ToString
public class CompanyDocument {

    @Id
    private String companyId;

    @Field(type = FieldType.Text, analyzer = "spanish")
    private String companyName;

    @Field(type = FieldType.Text)
    private String physicalAddress;


    @Field(type = FieldType.Text, analyzer = "spanish")
    private String description;

    @Field(type = FieldType.Integer)
    private Short rattingAvg;

    @Field(type = FieldType.Nested)
    private List<ServiceDocument> services;
}
