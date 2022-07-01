package com.rafaelamaral.dscatalog.resources;

import com.rafaelamaral.dscatalog.dto.UserDTO;
import com.rafaelamaral.dscatalog.dto.UserInsertDTO;
import com.rafaelamaral.dscatalog.dto.UserUpdateDTO;
import com.rafaelamaral.dscatalog.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.validation.Valid;
import java.net.URI;

@RestController
@RequestMapping(value = "/users")
public class UserResource {

    @Autowired
    private UserService userService;

    @GetMapping
    public ResponseEntity<Page<UserDTO>> findAllPaged(Pageable pageable){
        Page<UserDTO> page = userService.findAllPaged(pageable);
        return ResponseEntity.ok().body(page);
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserDTO> findById(@PathVariable Long id){
        UserDTO dto = userService.findById(id);
        return ResponseEntity.ok().body(dto);
    }

    @PostMapping
    public ResponseEntity<UserDTO> save(@Valid @RequestBody UserInsertDTO userDTO){
        UserDTO newUser = userService.save(userDTO);

        URI uri = ServletUriComponentsBuilder.fromCurrentRequestUri()
                                             .path("/{id}")
                                             .buildAndExpand(newUser.getId())
                                             .toUri();

        return ResponseEntity.created(uri).body(newUser);

    }

    @PutMapping("/{id}")
    public ResponseEntity<UserDTO> update(@PathVariable  Long id , @Valid @RequestBody UserUpdateDTO userUpdateDTO){
        UserDTO newUserDTO = userService.update(id , userUpdateDTO);
        return ResponseEntity.ok().body(newUserDTO);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id){
        userService.delete(id);
        return ResponseEntity.noContent().build();
    }


}
