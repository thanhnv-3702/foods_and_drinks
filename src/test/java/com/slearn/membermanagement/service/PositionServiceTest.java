package com.slearn.membermanagement.service;

import com.slearn.membermanagement.dto.PositionForm;
import com.slearn.membermanagement.entity.Position;
import com.slearn.membermanagement.repository.PositionRepository;
import com.slearn.membermanagement.support.TestEntityFactory;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.contains;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PositionServiceTest {

    @Mock
    private PositionRepository positionRepository;

    @Mock
    private ActivityLogService activityLogService;

    @InjectMocks
    private PositionService positionService;

    @Test
    void getById_notFound_throws() {
        when(positionRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> positionService.getById(99L))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("99");
    }

    @Test
    void create_savesAndLogs() {
        var form = PositionForm.builder().name("Developer").abbreviation("DEV").build();
        when(positionRepository.save(any(Position.class))).thenAnswer(inv -> {
            Position p = inv.getArgument(0);
            p.setId(1L);
            return p;
        });

        Position created = positionService.create(form);

        assertThat(created.getName()).isEqualTo("Developer");
        verify(activityLogService).record(eq("CREATE_POSITION"), contains("Developer"));
    }

    @Test
    void update_changesFieldsAndLogs() {
        Position existing = TestEntityFactory.position(1L);
        when(positionRepository.findById(1L)).thenReturn(Optional.of(existing));
        var form = PositionForm.builder().name("Lead").abbreviation("LD").build();

        Position updated = positionService.update(1L, form);

        assertThat(updated.getName()).isEqualTo("Lead");
        assertThat(updated.getAbbreviation()).isEqualTo("LD");
        verify(activityLogService).record(eq("UPDATE_POSITION"), contains("Lead"));
    }

    @Test
    void delete_removesAndLogs() {
        Position existing = TestEntityFactory.position(2L);
        when(positionRepository.findById(2L)).thenReturn(Optional.of(existing));

        positionService.delete(2L);

        ArgumentCaptor<Position> captor = ArgumentCaptor.forClass(Position.class);
        verify(positionRepository).delete(captor.capture());
        assertThat(captor.getValue().getId()).isEqualTo(2L);
        verify(activityLogService).record(eq("DELETE_POSITION"), contains("2"));
    }
}
