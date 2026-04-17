package ru.kata.spring.boot_security.demo.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import ru.kata.spring.boot_security.demo.model.User;
import ru.kata.spring.boot_security.demo.service.UserService;
import ru.kata.spring.boot_security.demo.service.RoleService;
import java.security.Principal;

@Controller
@RequestMapping("/admin")
public class UserController {

    private final UserService userService;
    private final RoleService roleService;

    public UserController(UserService userService, RoleService roleService) {
        this.userService = userService;
        this.roleService = roleService;
    }

    @GetMapping("/users")
    public String showAllUsers(Model model, Principal principal) {
        model.addAttribute("users", userService.findAll());
        model.addAttribute("currentUser", userService.findByUsername(principal.getName()));
        model.addAttribute("allRoles", roleService.findAll());
        model.addAttribute("newUser", new User());
        return "admin/users";
    }

    @PostMapping("/users")
    public String createUser(@ModelAttribute User user, @RequestParam(required = false) Long[] roleIds) {
        if (roleIds != null) {
            user.setRoles(roleService.findByIds(roleIds));
        }
        userService.save(user);
        return "redirect:/admin/users";
    }

    @PostMapping("/users/{id}")
    public String updateUser(@PathVariable Long id, @ModelAttribute User user,
                             @RequestParam(required = false) Long[] roleIds) {
        user.setId(id);
        if (roleIds != null) {
            user.setRoles(roleService.findByIds(roleIds));
        }
        userService.update(user);
        return "redirect:/admin/users";
    }

    @PostMapping("/users/{id}/delete")
    public String deleteUser(@PathVariable Long id) {
        userService.delete(id);
        return "redirect:/admin/users";
    }
}