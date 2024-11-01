package edu.ucsb.cs156.example.controllers;

import edu.ucsb.cs156.example.entities.RecommendationRequest;
import edu.ucsb.cs156.example.entities.UCSBDate;
import edu.ucsb.cs156.example.errors.EntityNotFoundException;
import edu.ucsb.cs156.example.repositories.RecommendationRequestRepository;
import edu.ucsb.cs156.example.repositories.UCSBDateRepository;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;

import com.fasterxml.jackson.core.JsonProcessingException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.access.method.P;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;

import java.time.LocalDateTime;

/**
 * This is a REST controller for req requests
 */

 @Tag(name = "RecommendationRequests")
 @RequestMapping("/api/recommendationrequests")
 @RestController
 @Slf4j

public class RecommendationRequestsController extends ApiController {
    @Autowired
    RecommendationRequestRepository recommendationRequestRepository;

    /**
     * List all recommendation requests
     * 
     * @return an iterable of rec req
     */
    @Operation(summary= "List all recommendation reqs")
    @PreAuthorize("hasRole('ROLE_USER')")
    @GetMapping("/all")
    public Iterable<RecommendationRequest> allRecommendationRequests() {
        Iterable<RecommendationRequest> recommendationRequests = recommendationRequestRepository.findAll();
        return recommendationRequests;
    }

    /**
     * Get a single request by id
     * 
     * @param id the id of the rec req
     * @return a recommendation request
     */
    @Operation(summary= "Get a single recommendation request")
    @PreAuthorize("hasRole('ROLE_USER')")
    @GetMapping("")
    public RecommendationRequest getById(
            @Parameter(name="id") @RequestParam Long id) {
        RecommendationRequest recommendationRequest = recommendationRequestRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(RecommendationRequest.class, id));

        return recommendationRequest;
    }

    /**
     * Create a new rec req
     * 
     * @param requesterEmail the email of the requester
     * @param professorEmail the professor to request
     * @param explanation the explanation
     * @param dateRequested the date requested
     * @param dateNeeded the date it is needed
     * @param done is it done
     * @return the saved recommendation req
     */
    @Operation(summary= "Create a new rec req")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PostMapping("/post")
    public RecommendationRequest postRecommendationRequest(
            @Parameter(name="requesterEmail") @RequestParam String requesterEmail,
            @Parameter(name="professorEmail") @RequestParam String professorEmail,
            @Parameter(name="explanation") @RequestParam String explanation,
            @Parameter(name="dateRequested", description="date (in iso format, e.g. YYYY-mm-ddTHH:MM:SS; see https://en.wikipedia.org/wiki/ISO_8601)") @RequestParam("dateRequested") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime dateRequested,
            @Parameter(name="dateNeeded", description="date (in iso format, e.g. YYYY-mm-ddTHH:MM:SS; see https://en.wikipedia.org/wiki/ISO_8601)") @RequestParam("dateNeeded") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime dateNeeded,
            @Parameter(name="done") @RequestParam boolean done)
            throws JsonProcessingException {

        // For an explanation of @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
        // See: https://www.baeldung.com/spring-date-parameters

        log.info("dateRequested={}", dateRequested);
        log.info("dateNeeded={}", dateNeeded);

        RecommendationRequest recommendationRequest = new RecommendationRequest();
        recommendationRequest.setRequesterEmail(requesterEmail);
        recommendationRequest.setProfessorEmail(professorEmail);
        recommendationRequest.setExplanation(explanation);
        recommendationRequest.setDateNeeded(dateNeeded);
        recommendationRequest.setDateRequested(dateRequested);
        recommendationRequest.setDone(done);

        RecommendationRequest savedRecommendationRequest = recommendationRequestRepository.save(recommendationRequest);

        return savedRecommendationRequest;
    }
    
    /**
     * Update a single rec req
     * 
     * @param id the id of the rec request to update
     * @param incoming an object to copy from
     * @return the saved recommendation req

     */
    @Operation(summary= "Update a single Recommendation Request")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PutMapping("")
    public RecommendationRequest updateRecommendationRequest(
            @Parameter(name="id") @RequestParam Long id,
            @RequestBody @Valid RecommendationRequest incoming) {

        RecommendationRequest recommendationRequest = recommendationRequestRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(RecommendationRequest.class, id));

                recommendationRequest.setRequesterEmail(incoming.getRequesterEmail());
                recommendationRequest.setProfessorEmail(incoming.getProfessorEmail());
                recommendationRequest.setExplanation(incoming.getExplanation());
                recommendationRequest.setDateNeeded(incoming.getDateNeeded());
                recommendationRequest.setDateRequested(incoming.getDateRequested());
                recommendationRequest.setDone(incoming.getDone());
        
                recommendationRequestRepository.save(recommendationRequest);
        
                return recommendationRequest;
            }

    /**
     * Delete a Recommendation Request
     * 
     * @param id the id of the rec request to delete
     * @return a message indicating the req request was deleted
     */
    @Operation(summary= "Delete a Recommendation Request")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @DeleteMapping("")
    public Object deleteRecommendationRequest(
            @Parameter(name="id") @RequestParam Long id) {
        RecommendationRequest recRequest = recommendationRequestRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(RecommendationRequest.class, id));

        recommendationRequestRepository.delete(recRequest);
        return genericMessage("RecommendationRequest with id %s deleted".formatted(id));
    }

}