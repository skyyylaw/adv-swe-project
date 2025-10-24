package com.ontracked.controller;

import com.ontracked.model.ProgressUpdate;
import com.ontracked.service.ProgressUpdateService;
import com.ontracked.dto.progress.ProgressUpdateRequest;
import com.ontracked.dto.progress.ProgressUpdateResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import jakarta.servlet.http.HttpServletRequest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class ProgressUpdateAPITests {

    private ProgressUpdateService service;
    private ProgressUpdateController controller;

    @Mock
    private HttpServletRequest mockRequest;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
        service = new ProgressUpdateService();
        controller = new ProgressUpdateController(service);
    }

    @Test
    public void testCreateProgressUpdate() {
        ProgressUpdateRequest request = new ProgressUpdateRequest(1L, 50, "halfway done");
        ResponseEntity<?> response = controller.createProgressUpdate(request, mockRequest);

        assertEquals(201, response.getStatusCode().value());
        assertTrue(response.getBody() instanceof ProgressUpdateResponse);
    }

    @Test
    public void testCreateProgressUpdateZero() {
        ProgressUpdateRequest request = new ProgressUpdateRequest(2L, 0);
        ResponseEntity<?> response = controller.createProgressUpdate(request, mockRequest);

        assertEquals(201, response.getStatusCode().value());
    }

    @Test
    public void testCreateProgressUpdateInvalid() {
        ResponseEntity<?> response = controller.createProgressUpdate(null, mockRequest);
        assertEquals(500, response.getStatusCode().value());
    }

    @Test
    public void testGetProgressUpdateById() {
        ProgressUpdateRequest request = new ProgressUpdateRequest(5L, 60, "making progress");
        controller.createProgressUpdate(request, mockRequest);

        List<ProgressUpdate> all = service.getAllProgressUpdates();
        Long id = all.get(0).getId();

        ResponseEntity<?> response = controller.getProgressUpdateById(id, mockRequest);
        assertEquals(200, response.getStatusCode().value());
    }

    @Test
    public void testGetProgressUpdateByIdNotFound() {
        ResponseEntity<?> response = controller.getProgressUpdateById(999L, mockRequest);
        assertEquals(404, response.getStatusCode().value());
    }

    @Test
    public void testGetProgressUpdatesByGoalId() {
        ProgressUpdateRequest request1 = new ProgressUpdateRequest(777L, 30, "started");
        ProgressUpdateRequest request2 = new ProgressUpdateRequest(777L, 60, "halfway");
        controller.createProgressUpdate(request1, mockRequest);
        controller.createProgressUpdate(request2, mockRequest);

        ResponseEntity<?> response = controller.getProgressUpdatesByGoalId(777L, mockRequest);
        assertEquals(200, response.getStatusCode().value());

        @SuppressWarnings("unchecked")
        List<ProgressUpdateResponse> updates = (List<ProgressUpdateResponse>) response.getBody();
        assertTrue(updates.size() >= 2);
    }

    @Test
    public void testUpdateProgressUpdate() {
        ProgressUpdateRequest createReq = new ProgressUpdateRequest(20L, 40, "original");
        controller.createProgressUpdate(createReq, mockRequest);

        Long id = service.getAllProgressUpdates().get(0).getId();

        ProgressUpdateRequest updateReq = new ProgressUpdateRequest(20L, 80, "updated");
        ResponseEntity<?> response = controller.updateProgressUpdate(id, updateReq, mockRequest);

        assertEquals(200, response.getStatusCode().value());
    }

    @Test
    public void testDeleteProgressUpdate() {
        ProgressUpdateRequest request = new ProgressUpdateRequest(40L, 90);
        controller.createProgressUpdate(request, mockRequest);

        Long id = service.getAllProgressUpdates().get(0).getId();

        ResponseEntity<?> response = controller.deleteProgressUpdate(id, mockRequest);
        assertEquals(200, response.getStatusCode().value());
    }

    // test write then read
    @Test
    public void testPersistence() {
        ProgressUpdateRequest request = new ProgressUpdateRequest(100L, 45, "test persistence");
        ResponseEntity<?> createResponse = controller.createProgressUpdate(request, mockRequest);

        ProgressUpdateResponse created = (ProgressUpdateResponse) createResponse.getBody();
        Long id = created.getId();

        ResponseEntity<?> getResponse = controller.getProgressUpdateById(id, mockRequest);
        ProgressUpdateResponse retrieved = (ProgressUpdateResponse) getResponse.getBody();
        assertEquals(45, retrieved.getPercentComplete());
    }
}
