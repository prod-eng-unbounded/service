package ro.unibuc.hello.services;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import ro.unibuc.hello.dtos.ActionDTO;
import ro.unibuc.hello.entities.Action;
import ro.unibuc.hello.repositories.ActionRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;
@ExtendWith(SpringExtension.class)
public class ActionServiceTest {

    @Mock
    ActionRepository actionRepository;

    @InjectMocks
    ActionService actionService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        Counter counterMock = Mockito.mock(Counter.class);
        when(metricsRegistry.counter(anyString(), anyString(), anyString())).thenReturn(counterMock);
        doNothing().when(counterMock).increment();
    }

    @Mock
    private MeterRegistry metricsRegistry;

    @Test
    public void test_getActions(){
        List<Action> actions = new ArrayList<>();
        actions.add(new Action("code0", "description0"));
        actions.add(new Action("code1", "description1"));
        actions.add(new Action("code2", "description2"));

        when(actionRepository.findAll()).thenReturn(actions);

        List<ActionDTO> result = actionService.getActions();

        assertEquals(3, result.size());
        assertEquals("code0", result.get(0).getCode());
        assertEquals("code1", result.get(1).getCode());
        assertEquals("code2", result.get(2).getCode());

        verify(actionRepository, times(1)).findAll();
    }

    @Test
    public void test_getByCode_returnsActionDTO(){
        Action action = new Action("code0", "description0");

        when(actionRepository.findById("code0")).thenReturn(Optional.of(action));

        ActionDTO result = actionService.getActionById("code0");

        assertEquals("code0",result.getCode());

        verify(actionRepository, times(1)).findById("code0");
    }

    @Test
    public void test_getByCode_throwsEntityNotFoundException(){
        when(actionRepository.findById("code0")).thenReturn(Optional.empty());

        try{
            actionService.getActionById("code0");
        }
        catch(Exception e){
            assertEquals("Entity: Action was not found", e.getMessage());
        }
        verify(actionRepository, times(1)).findById("code0");
    }

    @Test
    public void test_createAction_returnActionDTO(){
        when(actionRepository.findById("code0")).thenReturn(Optional.empty());

        ActionDTO result = actionService.addAction("code0", "description0");

        assertEquals("code0", result.getCode());

        verify(actionRepository, times(1)).save(any());
    }

    @Test
    public void test_createAction_throwsEntityAlreadyExistsException(){
        Action action = new Action("code0", "description0");

        when(actionRepository.findById("code0")).thenReturn(Optional.of(action));

        try{
            actionService.addAction("code0", "description0");
        }
        catch(Exception e){
            assertEquals("Entity: Action already exists", e.getMessage());
        }

        verify(actionRepository, times(1)).findById("code0");
    }

    @Test
    public void test_updateAction_returnsActionDTO(){
        Action action = new Action("code0", "description0");

        when(actionRepository.findById("code0")).thenReturn(Optional.of(action));

        ActionDTO result = actionService.updateAction("code0", new Action("code1", "description1"));

        assertEquals("code1", result.getCode());
        assertEquals("description1", result.getDescription());

        verify(actionRepository, times(1)).findById("code0");
    }

    @Test
    public void test_updateAction_throwsEntityNotFoundException() {
        when(actionRepository.findById("code0")).thenReturn(Optional.empty());
        try{
            actionService.updateAction("code0", new Action("code1", "description1"));
        }
        catch(Exception e){
            assertEquals("Entity: Action was not found", e.getMessage());
        }
    }

    @Test
    public void test_deleteAction(){
        Action action = new Action("code0", "description0");

        when(actionRepository.findById("code0")).thenReturn(Optional.of(action));

        actionService.deleteAction("code0");

        verify(actionRepository, times(1)).delete(any());
    }

    @Test
    public void test_deleteAction_throwsEntityNotFoundException() {
        when(actionRepository.findById("code0")).thenReturn(Optional.empty());

        try{
            actionService.deleteAction("code0");
        }
        catch(Exception e){
            assertEquals("Entity: Action was not found", e.getMessage());
        }
    }

}
