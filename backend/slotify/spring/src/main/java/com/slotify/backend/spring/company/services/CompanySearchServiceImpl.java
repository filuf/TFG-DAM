package com.slotify.backend.spring.company.services;

import com.slotify.backend.spring.company.models.CompanyDocument;
import com.slotify.backend.spring.company.repositories.CompanySearchRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.client.elc.NativeQuery;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHitSupport;
import org.springframework.data.elasticsearch.core.SearchPage;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CompanySearchServiceImpl implements CompanySearchService {

    private final CompanySearchRepository companySearchRepository;
    private final ElasticsearchOperations elasticsearchOperations;

    @Override
    public void indexCompany(CompanyDocument document) {
        this.companySearchRepository.save(document);
    }

    @Override
    public Page<CompanyDocument> searchCompany(String query, Pageable pageable) {

        NativeQuery nativeQuery = NativeQuery.builder()
                .withQuery(q -> q.bool(b -> b
                        .should(s -> s.matchPhrasePrefix(mpp -> mpp
                                .field("companyName")
                                .query(query)
                                .boost(3.0f)
                        ))
                        .should(s -> s.matchPhrasePrefix(mpp -> mpp
                                .field("services.serviceName")
                                .query(query)
                                .boost(2.5f)
                        ))
                        .should(s -> s.matchPhrasePrefix(mpp -> mpp
                                .field("physicalAddress")
                                .query(query)
                                .boost(1.0f)
                        ))
                        .should(s -> s.matchPhrasePrefix(mpp -> mpp
                                .field("description")
                                .query(query)
                                .boost(0.5f)
                        ))
                        .should(s -> s.matchPhrasePrefix(mpp -> mpp
                                .field("services.description")
                                .query(query)
                                .boost(0.5f)
                        ))
                ))
                .withPageable(pageable)
                .build();

        SearchPage<CompanyDocument> searchPage = SearchHitSupport.searchPageFor(
                this.elasticsearchOperations.search(nativeQuery, CompanyDocument.class),
                pageable
        );

        return (Page<CompanyDocument>) SearchHitSupport.unwrapSearchHits(searchPage);
    }

    @Override
    public CompanyDocument findCompanyById(String companyId) {
        return elasticsearchOperations.get(companyId, CompanyDocument.class);
    }
}
