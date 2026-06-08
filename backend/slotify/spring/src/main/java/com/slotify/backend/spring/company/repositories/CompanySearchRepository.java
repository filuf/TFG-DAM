package com.slotify.backend.spring.company.repositories;

import com.slotify.backend.spring.company.models.CompanyDocument;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public interface CompanySearchRepository extends ElasticsearchRepository<CompanyDocument, String> {
}
