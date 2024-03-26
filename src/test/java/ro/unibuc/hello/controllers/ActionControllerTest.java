package ro.unibuc.hello.controllers;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import ro.unibuc.hello.dtos.ActionDTO;
import ro.unibuc.hello.entities.Action;
import ro.unibuc.hello.services.ActionService;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
public class ActionControllerTest {

    @Mock
    private ActionService actionService;

    @InjectMocks
    private ActionController actionController;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void test_createAction(){
        ActionDTO actionDTO = new ActionDTO("create-test", "Writing tests for the application");
        Action action = new Action("create-test", "Writing tests for the application");

        when(actionService.addAction("create-test", "Writing tests for the application")).thenReturn(actionDTO);

        ActionDTO result = actionController.createAction(action);

        assertEquals(actionDTO, result);
        verify(actionService, times(1)).addAction("create-test", "Writing tests for the application");
    }

    @Test
    public void test_getActions(){
        when(actionService.getActions()).thenReturn(new ArrayList<>());

        List<ActionDTO> result = actionController.seeActions();

        assertEquals(0, result.size());
        verify(actionService, times(1)).getActions();
    }

    @Test
    public void test_getActionById(){
        ActionDTO actionDTO = new ActionDTO("create-test", "Writing tests for the application");

        when(actionService.getActionById("create-test")).thenReturn(actionDTO);

        ActionDTO result = actionController.seeAction("create-test");

        assertEquals(actionDTO, result);
        verify(actionService, times(1)).getActionById("create-test");
    }

    @Test
    public void test_updateAction(){
        ActionDTO actionDTO = new ActionDTO("create-test", "Writing tests for the application");
        Action action = new Action("create-test", "Writing tests for the application");

        when(actionService.updateAction("create-test", action)).thenReturn(actionDTO);

        ActionDTO result = actionController.updateAction("create-test", action);

        assertEquals(actionDTO, result);
        verify(actionService, times(1)).updateAction("create-test", action);
    }

    @Test
    public void test_deleteAction(){
        actionController.deleteAction("create-test");

        verify(actionService, times(1)).deleteAction("create-test");
    }
}
