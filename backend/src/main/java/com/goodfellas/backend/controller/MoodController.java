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

    @PostMapping
    public ResponseEntity<String> submitMood(@RequestBody Map<String, Integer> payload, Authentication authentication)
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

    @GetMapping
    public ResponseEntity<List<MoodDTO>> getMyMoodHistory(Authentication authentication)
    {
        return ResponseEntity.ok(moodService.getMyMoodHistory(authentication.getName()));
    }
}