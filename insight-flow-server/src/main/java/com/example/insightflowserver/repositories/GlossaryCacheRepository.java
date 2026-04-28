package com.example.insightflowserver.repositories;

import com.example.insightflowserver.model.MedicalTermCache;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GlossaryCacheRepository extends MongoRepository<MedicalTermCache, String> {}
