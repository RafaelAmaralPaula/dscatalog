package com.rafaelamaral.dscatalog.services;

import com.rafaelamaral.dscatalog.dto.RoleDTO;
import com.rafaelamaral.dscatalog.dto.UserDTO;
import com.rafaelamaral.dscatalog.dto.UserInsertDTO;
import com.rafaelamaral.dscatalog.dto.UserUpdateDTO;
import com.rafaelamaral.dscatalog.entities.Role;
import com.rafaelamaral.dscatalog.entities.User;
import com.rafaelamaral.dscatalog.repositories.RoleRepository;
import com.rafaelamaral.dscatalog.repositories.UserRepository;
import com.rafaelamaral.dscatalog.services.exceptions.DataBaseException;
import com.rafaelamaral.dscatalog.services.exceptions.ResourceNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;
import java.util.Optional;

@Service
public class UserService implements UserDetailsService {

    private static Logger logger = LoggerFactory.getLogger(UserService.class);

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @Transactional(readOnly = true)
    public Page<UserDTO> findAllPaged(Pageable pageable){
        Page<User> page = userRepository.findAll(pageable);
        return page.map(x -> new UserDTO(x));
    }

    @Transactional(readOnly = true)
    public UserDTO findById(Long id){
        Optional<User> obj = userRepository.findById(id);
        User entity = obj.orElseThrow(() -> new ResourceNotFoundException("Entity not found"));
        return new UserDTO(entity);
    }

    @Transactional
    public UserDTO save(UserInsertDTO userDTO){
        User entity = new User();
        copyDtoToEntity(userDTO , entity);
        entity.setPassword(passwordEncoder.encode(userDTO.getPassword()));
        entity = userRepository.save(entity);
        return new UserDTO(entity);
    }

    @Transactional
    public UserDTO update(Long id , UserUpdateDTO userUpdateDTO){
        try{
            User entity = userRepository.getOne(id);
            copyDtoToEntity(userUpdateDTO , entity);
            entity = userRepository.save(entity);
            return new UserDTO(entity);
        }
        catch (EntityNotFoundException ex){
            throw new ResourceNotFoundException("Id not found : " + id);
        }
    }

    public void delete(Long id){
        try {
            userRepository.deleteById(id);
        }
        catch (EmptyResultDataAccessException ex){
            throw new ResourceNotFoundException("Id not found : " + id);
        }
        catch (DataIntegrityViolationException ex){
            throw new DataBaseException("Integrity Violation");

        }

    }
    private void copyDtoToEntity(UserDTO userDTO, User entity) {
        entity.setFirstName(userDTO.getFirstName());
        entity.setLastName(userDTO.getLastName());
        entity.setEmail(userDTO.getEmail());

        entity.getRoles().clear();
        for(RoleDTO roleDTO : userDTO.getRoles()){
            Role roles = roleRepository.getOne(roleDTO.getId());
            entity.getRoles().add(roles);

        }

    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(username);
        if(user == null){
            logger.error("User not found : " + username);
            throw new UsernameNotFoundException("Email not found");
        }
        logger.info("User found : " + username);
        return user;
    }
}
