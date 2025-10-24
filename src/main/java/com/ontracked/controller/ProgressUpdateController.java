package com.ontracked.controller;

import com.ontracked.model.ProgressUpdate;
import com.ontracked.dto.progress.ProgressUpdateRequest;
import com.ontracked.dto.progress.ProgressUpdateResponse;
import com.ontracked.service.ProgressUpdateService;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * REST controller for ProgressUpdate endpoints
 */
@RestController
@RequestMapping("/progress")
public class ProgressUpdateController {

    private static final Logger logger = LoggerFactory.getLogger(ProgressUpdateController.class);
    private final ProgressUpdateService progressUpdateService;

    public ProgressUpdateController(ProgressUpdateService progressUpdateService) {
        this.progressUpdateService = progressUpdateService;
    }

    private void logRequest(HttpServletRequest request, String endpoint) {
        logger.info(
                "Timestamp: {}, Method: {}, Endpoint: {}",
                java.time.Instant.now(),
                request.getMethod(),
                endpoint
        );
    }


    @GetMapping
    public ResponseEntity<?> getAllProgressUpdates(HttpServletRequest request) {
        logRequest(request, "/progress");

        try {
            List<ProgressUpdateResponse> responses = new ArrayList<>();
            for (ProgressUpdate pu : progressUpdateService.getAllProgressUpdates()) {
                responses.add(ProgressUpdateResponse.from(pu));
            }
            return new ResponseEntity<>(responses, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("Error retrieving progress updates", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    @GetMapping("/{id}")
    public ResponseEntity<?> getProgressUpdateById(@PathVariable Long id, HttpServletRequest request) {
        logRequest(request, "/progress/" + id);


        ProgressUpdate pu = progressUpdateService.getProgressUpdateById(id);

        if (pu == null) {
            return new ResponseEntity<>("Progress update not found", HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(ProgressUpdateResponse.from(pu), HttpStatus.OK);
    }


    @GetMapping("/goal/{goalId}")
    public ResponseEntity<?> getProgressUpdatesByGoalId(@PathVariable Long goalId, HttpServletRequest request) {
        logRequest(request, "/progress/goal/" + goalId);


        try {
            List<ProgressUpdate> updates = progressUpdateService.getProgressUpdatesByGoalId(goalId);
            List<ProgressUpdateResponse> responses = new ArrayList<>();
            for (ProgressUpdate pu : updates) {
                responses.add(ProgressUpdateResponse.from(pu));
            }
            return new ResponseEntity<>(responses, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("Error retrieving progress updates for goal", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    @PostMapping
    public ResponseEntity<?> createProgressUpdate(@RequestBody ProgressUpdateRequest request, HttpServletRequest httpRequest) {
        logRequest(httpRequest, "/progress");


        try {
            ProgressUpdate pu = request.toEntity();
            progressUpdateService.addProgressUpdate(pu);
            return new ResponseEntity<>(ProgressUpdateResponse.from(pu), HttpStatus.CREATED);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity<>("Error creating progress update", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    @PutMapping("/{id}")
    public ResponseEntity<?> updateProgressUpdate(@PathVariable Long id, @RequestBody ProgressUpdateRequest request, HttpServletRequest httpRequest) {
        logRequest(httpRequest, "/progress/" + id);


        try {
            ProgressUpdate existing = progressUpdateService.getProgressUpdateById(id);
            if (existing == null) {
                return new ResponseEntity<>("Progress update not found", HttpStatus.NOT_FOUND);
            }

            existing.setPercentComplete(request.getPercentComplete());
            existing.setNote(request.getNote());
            progressUpdateService.updateProgressUpdate(existing);

            return new ResponseEntity<>(ProgressUpdateResponse.from(existing), HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity<>("Error updating progress update", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteProgressUpdate(@PathVariable Long id, HttpServletRequest request) {
        logRequest(request, "/progress/" + id);

        boolean deleted = progressUpdateService.deleteProgressUpdate(id);
        if (deleted) {
            return new ResponseEntity<>("Progress update deleted successfully", HttpStatus.OK);
        } else {
            return new ResponseEntity<>("Progress update not found", HttpStatus.NOT_FOUND);
        }
    }
}
