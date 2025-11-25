package com.cloudproject.TeamC.CampEat.presentation;

import com.cloudproject.TeamC.CampEat.application.PlaceService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/place")
public class PlaceController {

    private final PlaceService placeService;

    @GetMapping("/change")
    public String change(){
        placeService.importPlacesFromJson("kakao_places.json");
        return "db에 저장완료";
    }

    @PostMapping("/{id}/embed")
    public ResponseEntity<Void> embed(@PathVariable Long id) {
        placeService.processPlace(id);
        return ResponseEntity.ok().build();
    }

}
