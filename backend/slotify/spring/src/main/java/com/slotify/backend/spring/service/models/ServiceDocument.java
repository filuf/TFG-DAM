package com.slotify.backend.spring.service.models;

import lombok.*;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@Getter
@ToString
public class ServiceDocument {

    @Field(type = FieldType.Keyword)
    private String serviceId;

    @Field(type = FieldType.Text, analyzer = "spanish")
    private String serviceName;

    @Field(type = FieldType.Text, analyzer = "spanish")
    private String description;
}