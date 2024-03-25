package ro.unibuc.hello.controllers;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import ro.unibuc.hello.controllers.contracts.RoleCreateRequest;
import ro.unibuc.hello.dtos.RoleDTO;
import ro.unibuc.hello.services.RoleService;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class RoleControllerTest {

    @Mock
    private RoleService roleService;

    @InjectMocks
    private RoleController roleController;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testCreateRole() {
        RoleDTO roleDTO = new RoleDTO("admin", new ArrayList<>());
        RoleCreateRequest roleCreateRequest = new RoleCreateRequest("admin", new ArrayList<>());

        when(roleService.addRole(roleCreateRequest)).thenReturn(roleDTO);

        RoleDTO result = roleController.addRole(roleCreateRequest);

        assertEquals(roleDTO, result);
        verify(roleService, times(1)).addRole(roleCreateRequest);
    }

    @Test
    void testGetRoles() {
        when(roleService.getRoles()).thenReturn(new ArrayList<>());

        List<RoleDTO> result = roleController.getRoles();

        assertEquals(0, result.size());
        verify(roleService, times(1)).getRoles();
    }

    @Test
    void testGetRoleByName() {
        RoleDTO roleDTO = new RoleDTO("admin", new ArrayList<>());

        when(roleService.getRoleById("admin")).thenReturn(roleDTO);

        RoleDTO result = roleController.getRoleByName("admin");

        assertEquals(roleDTO, result);
        verify(roleService, times(1)).getRoleById("admin");
    }

    @Test
    void testUpdateRole() {
        RoleDTO roleDTO = new RoleDTO("admin", new ArrayList<>());
        RoleCreateRequest roleCreateRequest = new RoleCreateRequest("admin", new ArrayList<>());

        when(roleService.updateRole("admin", roleCreateRequest)).thenReturn(roleDTO);

        RoleDTO result = roleController.updateRole("admin", roleCreateRequest);

        assertEquals(roleDTO, result);
        verify(roleService, times(1)).updateRole("admin", roleCreateRequest);
    }

    @Test
    void testDeleteRole() {
        roleController.deleteRole("admin");

        verify(roleService, times(1)).deleteRole("admin");
    }
}