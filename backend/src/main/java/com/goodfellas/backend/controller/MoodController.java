package com.goodfellas.backend.controller;

import com.goodfellas.backend.dto.MoodDTO;
import com.goodfellas.backend.service.MoodService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/mood")
public class MoodController
{
    private final MoodService moodService;

    @Autowired
    public MoodController(MoodService moodService)
    {
        this.moodService = moodService;
    }

    /**
     ENDPOINT: POST /mood
     FUNCTION:
     Records the user's mood for the current day. The system restricts this to one entry per day via the service logic.
     @param payload A map containing the mood integer value (key: "value").
     @param authentication The security context identifying the patient.
     INPUT (JSON): (Uses the JWT from Authentication Header of User)
     {
     "value": 5
     }
     (Note: 'value' usually represents a scale from 1 to 10).
     @return A confirmation message on success or error message on failure.
     OUTPUT:
     - 201 CREATED: "Mood submitted successfully!"
     - 400 BAD REQUEST: Custom error message (e.g. "Mood already submitted for today").
     */
    @PostMapping
    public ResponseEntity<String> submitMoodOfPatientForTheDay(@RequestBody Map<String, Integer> payload, Authentication authentication)
    {
        try
        {
            Integer value = payload.get("value");
            if (value == null)
            {
                return new ResponseEntity<>("Error: 'value' field is required.", HttpStatus.BAD_REQUEST);
            }
            moodService.submitMood(authentication.getName(), value);
            return new ResponseEntity<>("Mood submitted successfully!", HttpStatus.CREATED);
        }
        catch (RuntimeException e)
        {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    /**
     ENDPOINT: GET /mood
     FUNCTION:
     Retrieves the historical list of all mood entries recorded by the authenticated patient.
     @param authentication The security context identifying the patient.
     INPUT: None (Uses the JWT from Authentication Header of User)
     @return A list of all historical mood entries for the user.
     OUTPUT:
     - 200 OK: A List of MoodDTO objects.
     - 401 UNAUTHORIZED: If the user is not logged in.
     */
    @GetMapping
    public ResponseEntity<List<MoodDTO>> getMyMoodHistoryOfPatient(Authentication authentication)
    {
        return ResponseEntity.ok(moodService.getMyMoodHistory(authentication.getName()));
    }
}