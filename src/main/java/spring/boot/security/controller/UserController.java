package spring.boot.security.controller;

import spring.boot.security.entity.User;
import spring.boot.security.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;


@Controller
public class UserController {

    private final UserService userService;
    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }


    @GetMapping("/user")
    public String userInfo(Model model, Principal principal) {
        User user = userService.findByUsername(principal.getName());
        model.addAttribute(user);
        return "info";
    }


    @GetMapping("/admin")
    public String showAllUsers(Model model, Principal principal) {
        model.addAttribute("admin", userService.findByUsername(principal.getName()));
        model.addAttribute("userList", userService.findAllUsers());
        return "users";
    }

    @GetMapping("/admin/add")
    public String addNewUser(Model model, User user, Principal principal) {
        model.addAttribute("admin", userService.findByUsername(principal.getName()));
        model.addAttribute(user);
        model.addAttribute("roleList", userService.findAllRoles());
        return "edit";
    }

    @GetMapping("/admin/edit{id}")
    public String editUser(@PathVariable Long id, Model model, Principal principal) {
        return addNewUser(model, userService.findUserById(id), principal);
    }

    @PostMapping("/admin/save")
    public String saveUser(@RequestParam List<Long> roleIds, Model model, User user, Principal principal) {
        if (userService.findAllUsers().contains(user)) {
            model.addAttribute("isExists", true);
            return showAllUsers(model, principal);
        }
        user.setRoles(userService.findRoles(roleIds));
        userService.saveUser(user);
        return showAllUsers(model, principal);
    }

    @GetMapping("/admin/delete{id}")
    public String deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return "redirect:/admin";
    }
}