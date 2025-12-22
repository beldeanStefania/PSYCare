package com.goodfellas.backend.service;

import com.goodfellas.backend.repository.PatientRepository;
import com.goodfellas.backend.repository.PsychologistRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final PsychologistRepository psychologistRepository;
    private final PatientRepository patientRepository;

    @Autowired
    public CustomUserDetailsService(PsychologistRepository psychologistRepository, PatientRepository patientRepository)
    {
        this.psychologistRepository = psychologistRepository;
        this.patientRepository = patientRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException
    {
        var psychologist = psychologistRepository.findByUsername(username);
        if (psychologist.isPresent())
        {
            List<GrantedAuthority> authorities = Collections.singletonList(new SimpleGrantedAuthority("ROLE_PSYCHOLOGIST"));
            return new User(psychologist.get().getUsername(), psychologist.get().getPassword(), authorities);
        }

        var patient = patientRepository.findByUsername(username);
        if (patient.isPresent())
        {
            List<GrantedAuthority> authorities = Collections.singletonList(new SimpleGrantedAuthority("ROLE_PATIENT"));
            return new User(patient.get().getUsername(), patient.get().getPassword(), authorities);
        }

        throw new UsernameNotFoundException("User not found with username: " + username);
    }
}
