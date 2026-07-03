package com.slearn.membermanagement.service;

import com.slearn.membermanagement.dto.PositionForm;
import com.slearn.membermanagement.entity.Position;
import com.slearn.membermanagement.repository.PositionRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PositionService {

    private final PositionRepository positionRepository;
    private final ActivityLogService activityLogService;

    public PositionService(PositionRepository positionRepository,
                           ActivityLogService activityLogService) {
        this.positionRepository = positionRepository;
        this.activityLogService = activityLogService;
    }

    @Transactional(readOnly = true)
    public Page<Position> findAll(Pageable pageable) {
        return positionRepository.findAll(pageable);
    }

    @Transactional(readOnly = true)
    public Position getById(Long id) {
        return positionRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy vị trí id=" + id));
    }

    @Transactional(readOnly = true)
    public PositionForm getFormById(Long id) {
        Position p = getById(id);
        return PositionForm.builder()
                .id(p.getId())
                .name(p.getName())
                .abbreviation(p.getAbbreviation())
                .build();
    }

    @Transactional
    public Position create(PositionForm form) {
        Position position = Position.builder()
                .name(form.getName())
                .abbreviation(form.getAbbreviation())
                .build();
        positionRepository.save(position);
        activityLogService.record("CREATE_POSITION",
                "Tạo vị trí '" + position.getName() + "' (id=" + position.getId() + ")");
        return position;
    }

    @Transactional
    public Position update(Long id, PositionForm form) {
        Position position = getById(id);
        position.setName(form.getName());
        position.setAbbreviation(form.getAbbreviation());
        positionRepository.save(position);
        activityLogService.record("UPDATE_POSITION",
                "Cập nhật vị trí '" + position.getName() + "' (id=" + position.getId() + ")");
        return position;
    }

    @Transactional
    public void delete(Long id) {
        Position position = getById(id);
        positionRepository.delete(position);
        activityLogService.record("DELETE_POSITION",
                "Xóa vị trí '" + position.getName() + "' (id=" + id + ")");
    }
}
