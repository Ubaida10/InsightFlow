package com.example.insightflowserver.repositories;

import com.example.insightflowserver.model.LabReport;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

/**
 * Repository interface for LabReport entity operations.
 *
 * This interface extends MongoRepository to provide CRUD operations
 * for lab reports stored in MongoDB. It includes custom query methods
 * for retrieving reports by user ID with different sorting options.
 *
 * @author Muhammad Abu Ubaida Aljerah
 */
public interface LabReportRepository extends MongoRepository<LabReport,String> {

    /**
     * Finds all lab reports for a specific user.
     *
     * @param userId the unique identifier of the user
     * @return a list of lab reports associated with the specified user
     */
    List<LabReport> findByUserId(String userId);

    /**
     * Finds all lab reports for a specific user, ordered by upload date descending.
     *
     * This method returns the most recently uploaded reports first, which is
     * useful for displaying a chronological view of a user's lab history.
     *
     * @param userId the unique identifier of the user
     * @return a list of lab reports sorted by upload date (newest first)
     */
    List<LabReport> findByUserIdOrderByUploadedAtAsc(String userId);
}
