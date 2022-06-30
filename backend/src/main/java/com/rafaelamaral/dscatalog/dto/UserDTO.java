package com.rafaelamaral.dscatalog.dto;

import com.rafaelamaral.dscatalog.entities.User;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

public class UserDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long id;

    @NotBlank(message = "Campo requerido")
    @Size(min = 5 , max = 60 , message = "Campo deve ter entre 5 a 60 caracteres")
    private String firstName;

    @NotBlank(message = "Campo requerido")
    @Size(min = 5 , max = 60 , message = "Campo deve ter entre 5 a 60 caracteres")
    private String lastName;

    @Email(message = "Por favor informe um email válido")
    private String email;

    Set<RoleDTO> roles = new HashSet<>();

    public UserDTO(){}

    public UserDTO(Long id , String firstName , String lastName , String email){
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
    }

    public UserDTO(User entity){
        id = entity.getId();
        firstName = entity.getFirstName();
        lastName = entity.getLastName();
        email = entity.getEmail();
        entity.getRoles().forEach(role -> roles.add(new RoleDTO(role)));
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Set<RoleDTO> getRoles() {
        return roles;
    }
}
