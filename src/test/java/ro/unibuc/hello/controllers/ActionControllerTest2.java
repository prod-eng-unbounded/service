package ro.unibuc.hello.controllers;

import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import ro.unibuc.hello.services.ActionService;

@ExtendWith(SpringExtension.class)
public class ActionControllerTest2 {
    @Mock
    private ActionService actionService;
    @InjectMocks
    private ActionController actionController;
}
