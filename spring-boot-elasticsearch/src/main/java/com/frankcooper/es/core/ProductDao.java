package com.frankcooper.es.core;

import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductDao extends ElasticsearchRepository<Product, Long> {

}

