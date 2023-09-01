package com.Auton.gibg.controller.role;

import com.Auton.gibg.response.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.*;
import com.Auton.gibg.repository.role.role_repository;
import com.Auton.gibg.entity.roleEntity.roleEntity;
import com.Auton.gibg.entity.roleEntity.RoleDTO;


import java.util.List;


@RestController
@RequestMapping(value = "/api/admin/role")
public class role_controller {

    private final JdbcTemplate jdbcTemplate;
    @Autowired
    public role_controller(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }


    @GetMapping("/all")
    public ResponseEntity<ResponseWrapper<List<RoleDTO>>> getAllRoles() {
        try {
            String sql = "SELECT `role_id`, `role_name`, `description` FROM `tb_role` WHERE `role_id` <> 1";
            List<RoleDTO> roles = jdbcTemplate.query(sql, (resultSet, rowNum) -> {
                RoleDTO roleDTO = new RoleDTO();
                roleDTO.setRoleId(resultSet.getLong("role_id"));
                roleDTO.setRoleName(resultSet.getString("role_name"));
                roleDTO.setDescription(resultSet.getString("description"));
                return roleDTO;
            });

            ResponseWrapper<List<RoleDTO>> responseWrapper = new ResponseWrapper<>("Role retrieved successfully.", roles);
            return ResponseEntity.ok(responseWrapper);
        } catch (Exception e) {
            String errorMessage = "An error occurred while retrieving roles.";
            ResponseWrapper<List<RoleDTO>> errorResponse = new ResponseWrapper<>(errorMessage, null);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    @PutMapping("/update/{roleId}")
    public ResponseEntity<String> updateRole(@PathVariable Long roleId, @RequestBody RoleDTO updatedRole) {
        try {
            String sql = "UPDATE `tb_role` SET `role_name` = ?, `description` = ? WHERE `role_id` = ?";
            int affectedRows = jdbcTemplate.update(sql, updatedRole.getRoleName(), updatedRole.getDescription(), roleId);

            if (affectedRows > 0) {
                return new ResponseEntity<>("Role updated successfully.", HttpStatus.OK);
            } else {
                return new ResponseEntity<>("Role not found or update failed.", HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>("Error updating role.", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

}
