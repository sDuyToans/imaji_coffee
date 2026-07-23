package com.duytoan.imajicoffee.imaji_coffee_be.controller.common;

import com.duytoan.imajicoffee.imaji_coffee_be.dto.common.NewDto;
import com.duytoan.imajicoffee.imaji_coffee_be.services.common.INewService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author duytoan
 * @since 10/2025
 */
@RestController
@RequestMapping("/api/v1/news")
@RequiredArgsConstructor
public class NewsController {

    private final INewService newService;

    /**
     * Get top news by created date
     * @param page -> page number
     * @param size -> page size
     * @return -> res entity
     */
    @GetMapping
    public ResponseEntity<List<NewDto>> getTopNewsByCreatedDate(@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "5") int size) {
        List<NewDto> newDtos = newService.getPageNews(page, size);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(newDtos);
    }

    /**
     * Get new by id
     * @param newId -> long new id
     * @return -> res entity
     */
    @GetMapping("/{newId}")
    public ResponseEntity<NewDto> getNewById(@PathVariable Long newId) {
        NewDto newDto = newService.getNewById(newId);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(newDto);
    }
}
