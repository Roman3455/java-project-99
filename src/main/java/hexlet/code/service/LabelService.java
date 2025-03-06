package hexlet.code.service;

import hexlet.code.dto.label.LabelCreateDTO;
import hexlet.code.dto.label.LabelDTO;
import hexlet.code.dto.label.LabelUpdateDTO;
import hexlet.code.exception.ResourceNotFoundException;
import hexlet.code.mapper.LabelMapper;
import hexlet.code.model.Label;
import hexlet.code.repository.LabelRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class LabelService {

    private final LabelRepository labelRepository;

    private final LabelMapper labelMapper;

    public List<LabelDTO> getAllLabels() {
        var labels = labelRepository.findAll();

        return labels.stream()
                .map(labelMapper::map)
                .toList();
    }

    public LabelDTO getLabelById(Long id) {

        return labelMapper.map(getById(id));
    }

    public LabelDTO createLabel(LabelCreateDTO dto) {
        var label = labelMapper.map(dto);
        label = labelRepository.save(label);

        return labelMapper.map(label);
    }

    public LabelDTO updateLabel(Long id, LabelUpdateDTO dto) {
        var label = getById(id);
        labelMapper.update(dto, label);
        labelRepository.save(label);

        return labelMapper.map(label);
    }

    public void deleteLabel(Long id) {
        if (!labelRepository.existsById(id)) {
            throw new ResourceNotFoundException(String.format("Label with id '%s' not found", id));
        }

        labelRepository.deleteById(id);
    }

    private Label getById(Long id) {

        return labelRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(String.format("Label with id '%s' not found", id)));
    }
}
