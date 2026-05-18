package com.slotify.backend.spring.company.models;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

@Document(indexName = "companies")
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@ToString
public class CompanyDocument {

    @Id
    private String companyId;

    @Field(type = FieldType.Text)
    private String companyName;

    @Field(type = FieldType.Text)
    private String physicalAddress;

    @Field(type = FieldType.Keyword)
    private String emailAddress;

    @Field(type = FieldType.Keyword)
    private String phoneNumber;

    @Field(type = FieldType.Keyword)
    private String s3ImageKey;

    @Field(type = FieldType.Text)
    private String description;

    @Field(type = FieldType.Integer)
    private Short rattingAvg;
}
