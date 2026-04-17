package ru.kata.spring.boot_security.demo.service;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.kata.spring.boot_security.demo.model.Role;
import ru.kata.spring.boot_security.demo.model.User;
import ru.kata.spring.boot_security.demo.repository.RoleRepository;
import ru.kata.spring.boot_security.demo.repository.UserRepository;
import javax.annotation.PostConstruct;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    public UserServiceImpl(UserRepository userRepository, RoleRepository roleRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional(readOnly = true)
    public User getById(Long id) {
        return userRepository.findById(id).orElse(null);
    }

    @Override
    @Transactional(readOnly = true)
    public User findByUsername(String username) {
        return userRepository.findByUsername(username).orElse(null);
    }

    @Override
    @Transactional(readOnly = true)
    public List<User> findAll() {
        return userRepository.findAll();
    }

    @Override
    @Transactional
    public User save(User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userRepository.save(user);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        userRepository.deleteById(id);
    }

    @Override
    @Transactional
    public void update(User user) {
        User exitingUser = getById(user.getId());
        if (exitingUser != null) {
            exitingUser.setFirstName(user.getFirstName());
            exitingUser.setFirstName(user.getLastName());
            exitingUser.setEmail(user.getEmail());
            exitingUser.setUsername(user.getUsername());

            if (user.getPassword() != null || !user.getPassword().isEmpty()) {
                exitingUser.setPassword(passwordEncoder.encode(user.getPassword()));
            }

            if (user.getRoles() != null) {
                exitingUser.setRoles(user.getRoles());
            }

            userRepository.save(exitingUser);
        }
    }


    @Transactional(readOnly = true)
    public UserDetails loadUsersByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findByUsername(username).orElseThrow(() -> new UsernameNotFoundException("Пользователь не найден: " + username));
    }

    @PostConstruct
    @Transactional
    public void init() {
        if (roleRepository.count() == 0) {
            roleRepository.save(new Role("ROLE_USER"));
            roleRepository.save(new Role("ROLE_ADMIN"));
        }

        if (userRepository.count() == 0) {
            Role userRole = roleRepository.findByName("ROLE_USER").get();
            Role adminRole = roleRepository.findByName("ROLE_ADMIN").get();

            Set<Role> adminRoles = new HashSet<>();
            adminRoles.add(adminRole);
            adminRoles.add(userRole);

            User admin = new User(
                    "Admin",
                    "Adminov",
                    "admin@mail.ru",
                    "admin",
                    "admin",
                    adminRoles
            );

            save(admin);

            Set<Role> userRoles = new HashSet<>();
            userRoles.add(userRole);

            User user = new User(
                    "User",
                    "Userov",
                    "user@mail.ru",
                    "user",
                    "user",
                    userRoles
            );

            save(user);
            }
        }

    }

}

