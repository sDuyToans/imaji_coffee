package com.duytoan.imajicoffee.imaji_coffee_be.controller.common;

import com.duytoan.imajicoffee.imaji_coffee_be.dto.common.SpaceDto;
import com.duytoan.imajicoffee.imaji_coffee_be.services.common.ISpaceService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author duytoan
 * @since 10/2025
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/spaces")
public class SpaceController {

    private final ISpaceService spaceService;

    /**
     * Get spaces list
     * @return -> res entity
     */
    @GetMapping
    public ResponseEntity<List<SpaceDto>> getSpaces(){
        List<SpaceDto> spaceDtoList = spaceService.getSpaces();
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(spaceDtoList);
    }
}
