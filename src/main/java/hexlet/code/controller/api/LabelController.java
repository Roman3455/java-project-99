package hexlet.code.controller.api;

import hexlet.code.dto.label.LabelCreateDTO;
import hexlet.code.dto.label.LabelDTO;
import hexlet.code.dto.label.LabelUpdateDTO;
import hexlet.code.service.LabelService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/labels")
public class LabelController {

    private final LabelService labelService;

    @GetMapping("")
    public ResponseEntity<List<LabelDTO>> index() {
        var result = labelService.getAllLabels();

        return ResponseEntity.status(HttpStatus.OK)
                .header("X-Total-Count", String.valueOf(result.size()))
                .body(result);
    }

    @PostMapping("")
    @ResponseStatus(HttpStatus.CREATED)
    public LabelDTO create(@Valid @RequestBody LabelCreateDTO labelCreateDTO) {

        return labelService.createLabel(labelCreateDTO);
    }

    @GetMapping("/{id}")
    public LabelDTO show(@PathVariable Long id) {

        return labelService.getLabelById(id);
    }

    @PutMapping("/{id}")
    public LabelDTO update(@PathVariable Long id, @Valid @RequestBody LabelUpdateDTO labelUpdateDTO) {

        return labelService.updateLabel(id, labelUpdateDTO);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void destroy(@PathVariable Long id) {

        labelService.deleteLabel(id);
    }
}
