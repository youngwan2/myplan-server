package com.myplan.server.service;

import com.myplan.server.exception.NotFoundException;
import com.myplan.server.repository.RefreshRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.*;

public class RefreshServiceTest {

    @InjectMocks
    RefreshService refreshService;

    @Mock
    RefreshRepository refreshRepository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    /* 리프레쉬 토큰 삭제*/
    @Test
    void removeRefresh_ShouldThrowException_WhenRefreshTokenIsNotFound(){

        // Given
        String refresh = null;
        String username = "test11";

        //When & Then
        assertThrows(NotFoundException.class, ()-> refreshService.removeRefresh(username));
    }
}
