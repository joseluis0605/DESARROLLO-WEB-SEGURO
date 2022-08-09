package com.dws.ActualRetro;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.method.HandlerTypePredicate;

import javax.annotation.PostConstruct;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Controller
public class UserController {
    @Autowired
    UserService userService;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private CSRFHandlerConfiguration csrfConf;

    /*@PostConstruct
    private void initDatabase(){
        List<String> auxlist = new ArrayList<>();
        auxlist.add("USER");
        Users aux = new Users("user", "trower12@outlook.com", passwordEncoder.encode("pass"), "655844033");
        aux.setRoles(auxlist);
        userService.userRepository.save(aux);
        aux = new Users("admin", "noemail@gmail.com", passwordEncoder.encode("adminpass"), "111111111");
        auxlist = new ArrayList<>();
        auxlist.add("USER");
        auxlist.add("ADMIN");
        aux.setRoles(auxlist);
        userService.userRepository.save(aux);
    }*/

    @GetMapping("/")
    public String index(Model model, HttpServletRequest request){
        model.addAttribute("isadmin", request.isUserInRole("ADMIN"));
        model.addAttribute("isuser", request.isUserInRole("USER"));
        if (request.getUserPrincipal() != null) {
            Optional<Users> user = userService.userRepository.findByName(request.getUserPrincipal().getName());
            if (user.isPresent()) {
                model.addAttribute("id", user.get().getId());
            }
        }
        return "index";
    }

    @GetMapping("/login")
    public String login(Model model){
        return "login";
    }
    @PostMapping("/login")
    public String login(@RequestParam String username, HttpServletRequest request) throws ServletException {
        Optional<Users> user = userService.userRepository.findByName(username);
        if (user.isPresent()) {
            request.login(username, user.get().getPassword());
            return "loginok";
        }else{
            return "loginerror";
        }
    }
    @GetMapping("/loginerror")
    public String loginerror(){
        return "loginerror";
    }

    @GetMapping("/logout")
    public String logout(){
        return "logout";
    }

    @PostMapping("/register")
    public String register(@RequestParam String mail,  @RequestParam String username, @RequestParam String password, @RequestParam String phone){
        if (!userService.userRepository.existsByName(username)) {
            Users auxUser = new Users(username, mail, passwordEncoder.encode(password), phone);
            List<String> auxlist = new ArrayList<>();
            auxlist.add("USER");
            auxUser.setRoles(auxlist);
            userService.userRepository.save(auxUser);
            return "register_success";
        }else{
            return "registererror";
        }
    }

    @GetMapping("/register")
    public String showRegisterForm(){
        return "register_form";
    }

    @GetMapping("/adminpage")
    @PreAuthorize("hasRole('ADMIN')")
    public String showUsers(Model model, HttpServletRequest request){
        List<Users> users = userService.userRepository.findAll();
        model.addAttribute("users", users);
        model.addAttribute("account", request.getUserPrincipal().getName());
        return "users";
    }

    @GetMapping("/users/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public String showUser(Model model, HttpServletRequest request, @PathVariable long id){
        Users user = userService.userRepository.findById(id);
        model.addAttribute("user", user);
        model.addAttribute("isadmin", request.isUserInRole("ADMIN"));
        model.addAttribute("consolecart", user.getShoppingCart().getConsoleList());
        model.addAttribute("videogamecart", user.getShoppingCart().getVideogameList());
        return "user";
    }

    @PostMapping("/users/delete/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public String deleteUser(Model model, HttpServletRequest request, @PathVariable long id){
        Users user = userService.userRepository.findById(id);
        model.addAttribute("user", user);
        userService.userRepository.disableForeignKeyChecks();
        userService.userRepository.removeUser(id);
        userService.userRepository.reenableForeignKeyChecks();
        return "deleted_success_user";
    }
}
