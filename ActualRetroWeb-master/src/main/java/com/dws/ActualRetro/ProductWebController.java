package com.dws.ActualRetro;

import org.owasp.html.Sanitizers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.Banner;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.*;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Controller
public class ProductWebController {
    @Autowired
    VideogameService videogameService;
    @Autowired
    ConsoleService consoleService;
    @Autowired
    UserService userService;



    @GetMapping("/products/consoles")
    public String showVDConsoles(Model model, HttpServletRequest request) {
        List<VDConsole> vdConsoles = consoleService.consoleRepository.findAll();
        List<VDConsole> inProperty = new ArrayList<>();
        if (request.getUserPrincipal()!=null) {
            String name = request.getUserPrincipal().getName();
            Optional<Users> useraux = userService.userRepository.findByName(name);
            if (useraux.isPresent()) {
                if (request.isUserInRole("ADMIN")){
                    model.addAttribute("isadmin", true);
                }else {
                    inProperty = useraux.get().getConsolesUploaded();
                    model.addAttribute("isadmin", false);
                }
            }
        }
        model.addAttribute("vdConsolesUploaded", inProperty);
        vdConsoles.removeAll(inProperty);
        model.addAttribute("vdConsoles", vdConsoles);
        return "consoles";
    }

    @GetMapping("/products/videogames")
    public String showVideogames(Model model, HttpServletRequest request) {
        List<Videogame> videogames = videogameService.videogameRepository.findAll();
        List<Videogame> inProperty = new ArrayList<>();
        if (request.getUserPrincipal()!=null) {
            String name = request.getUserPrincipal().getName();
            Optional<Users> useraux = userService.userRepository.findByName(name);
            if (useraux.isPresent()) {
                if (request.isUserInRole("ADMIN")){
                        model.addAttribute("isadmin", true);
                }else {
                    for (int i = 0; i < videogames.size(); i++) {
                        inProperty = useraux.get().getVideogamesUploaded();
                        model.addAttribute("isadmin", false);
                    }
                }
            }
        }
        model.addAttribute("videogamesUpdated", inProperty);
        videogames.removeAll(inProperty);
        model.addAttribute("videogames", videogames);
        return "videogames";
    }

    @GetMapping("/products/consoles/{id}")
    public String showVDConsole(Model model, @PathVariable long id) {
        model.addAttribute("vdConsole", consoleService.consoleRepository.findById(id));
        return "console";
    }

    @GetMapping("/products/videogames/{id}")
    public String showVideogame(Model model, @PathVariable long id) {
        model.addAttribute("videogame", videogameService.videogameRepository.findById(id));
        return "videogame";
    }

    //He quitado la parte de Date
    /*
    @PostMapping("/products/consoles/sell")
    public String addVDConsole(Model model, @RequestParam String name, @RequestParam float price, @RequestParam int maxcon, @RequestParam String date, @RequestParam String description) {
        Date newdate = new Date();
        newdate.parseDate(date, "-");
        String sanitizedDesc = Sanitizers.FORMATTING.sanitize(description);
        VDConsole console = new VDConsole(name, price, maxcon, newdate, sanitizedDesc);
        model.addAttribute("console", console);
        consoleService.consoleRepository.save(console);
        return "saved_console";
    }

    @PostMapping("/products/videogames/sell")
    public String addVideogame(Model model, @RequestParam String name, @RequestParam float price, @RequestParam int pegi, @RequestParam String date, @RequestParam String genre, @RequestParam String description) {
        Date newdate = new Date();
        newdate.parseDate(date, "-");
        VDGenre gen = VDGenre.valueOf(genre);
        String sanitizedDesc = Sanitizers.FORMATTING.sanitize(description);
        Videogame videogame = new Videogame(name, price, pegi, newdate, gen, sanitizedDesc);
        model.addAttribute("videogame", videogame);
        videogameService.videogameRepository.save(videogame);

        return "saved_videogame";
    }*/
    @GetMapping("/products/consoles/sell")
    public String console(Model model){
        return "consoleForm";
    }

    @GetMapping("/products/videogames/sell")
    public String videogame(Model model){
        return "videogameForm";
    }

    @PostMapping("/products/consoles/sell")
    public String addVDConsole(Model model, HttpServletRequest request, @RequestParam String name, @RequestParam float price, @RequestParam int maxcon, @RequestParam String description) {
        Date newdate = new Date();
        String sanitizedDesc = Sanitizers.FORMATTING.sanitize(description);
        VDConsole console = new VDConsole(name, price, maxcon, newdate, sanitizedDesc);
        String userna = request.getUserPrincipal().getName();
        Optional<Users> user = userService.userRepository.findByName(userna);
        if (user.isPresent()){
            console.setUser(user.get());
            model.addAttribute("console", console);
            consoleService.consoleRepository.save(console);
            user.get().getConsolesUploaded().add(console);
            userService.userRepository.save(user.get());
            return "saved_console";
        }else{
            return "loginerror";
        }
    }

    @PostMapping("/products/videogames/sell")
    public String addVideogame(Model model, HttpServletRequest request, @RequestParam String name, @RequestParam float price, @RequestParam int pegi, @RequestParam String genre, @RequestParam String description) {
        Date newdate = new Date();
        VDGenre gen = VDGenre.valueOf(genre);
        String sanitizedDesc = Sanitizers.FORMATTING.sanitize(description);
        Videogame videogame = new Videogame(name, price, pegi, newdate, gen, sanitizedDesc);
        String userna = request.getUserPrincipal().getName();
        Optional<Users> user = userService.userRepository.findByName(userna);
        if (user.isPresent()){
            videogame.setUser(user.get());
            model.addAttribute("videogame", videogame);
            videogameService.videogameRepository.save(videogame);
            user.get().getVideogamesUploaded().add(videogame);
            userService.userRepository.save(user.get());
            return "saved_videogame";
        }else{
            return "loginerror";
        }
    }

    @GetMapping("/products/consoles/delete/{id}")
    public String deleteConsole(@PathVariable long id, HttpServletRequest request, Model model){
        Optional<Users> user=userService.userRepository.findByName(request.getUserPrincipal().getName());
        if (user.isPresent()){
            if (user.get().isProppertyOf(consoleService.consoleRepository.findById(id).getUser().getId())){
                VDConsole console= consoleService.consoleRepository.getById(id);
                consoleService.consoleRepository.disableForeignKeyChecks();
                consoleService.consoleRepository.removeConsole(id);
                consoleService.consoleRepository.reenableForeignKeyChecks();
                model.addAttribute("console", console);
                return "delete_success_console";
            }else{
                return "not_authorized";
            }
        } else {
            return "loginerror";
        }
    }

    @GetMapping("/products/videogames/delete/{id}")
    public String deleteVideogame(@PathVariable long id, HttpServletRequest request, Model model){
        Optional<Users> user=userService.userRepository.findByName(request.getUserPrincipal().getName());
        if (user.isPresent()){
            if (user.get().isProppertyOf(videogameService.videogameRepository.findById(id).getUser().getId())){
                Videogame videogame= videogameService.videogameRepository.getById(id);
                consoleService.consoleRepository.disableForeignKeyChecks();
                videogameService.videogameRepository.removeGame(id);
                consoleService.consoleRepository.reenableForeignKeyChecks();
                model.addAttribute("videogame", videogame);
                return "delete_success_videogame";
            }else{
                return "not_authorized";
            }
        } else {
            return "loginerror";
        }
    }

    @GetMapping("/products/consoles/edit")
    public String showModifyConsoleForm(){
        return "modify_console";
    }
    @GetMapping("/products/videogames/edit")
    public String showModifyVideogameForm(){
        return "modify_console";
    }

    @GetMapping("/products/consoles/{id}/edit/")
    public String editConsole(@PathVariable long id, Model model, HttpServletRequest request, @RequestParam String name, @RequestParam float price, @RequestParam int maxcon, @RequestParam String description) {
        Optional<Users> user = userService.userRepository.findByName(request.getUserPrincipal().getName());
        if (user.isPresent()) {
            if (user.get().isProppertyOf(consoleService.consoleRepository.findById(id).getUser().getId())) {
            VDConsole newConsole=new VDConsole(name, price,maxcon,new Date(), Sanitizers.FORMATTING.sanitize(description));
            newConsole.setId(id);
            consoleService.consoleRepository.save(newConsole);
            model.addAttribute("console", newConsole);
            return "saved_console";
            }else {
                return "not_authorized";
            }
        }else{
            return "loginerror";
        }
    }
    @GetMapping("/products/videogames/{id}/edit/")
    public String editVideogame(@PathVariable long id, Model model, HttpServletRequest request, @RequestParam String name, @RequestParam float price, @RequestParam int pegi, @RequestParam String genre, @RequestParam String description) {
        Optional<Users> user = userService.userRepository.findByName(request.getUserPrincipal().getName());
        if (user.isPresent()) {
            if (user.get().isProppertyOf(videogameService.videogameRepository.findById(id).getUser().getId())) {
                Videogame newVideogame=new Videogame(name,price,pegi,new Date(),VDGenre.valueOf(genre), Sanitizers.FORMATTING.sanitize(description));
                newVideogame.setId(id);
                videogameService.videogameRepository.save(newVideogame);
                model.addAttribute("videogame", newVideogame);
                return "saved_videogame";
            }else {
                return "not_authorized";
            }
        }else{
            return "loginerror";
        }
    }
    //-- Queries-- //

    @GetMapping("/filter/consoles/pricefilter/")
    public String getConsolesBetweenPrices(Model model, @RequestParam int pricemin, @RequestParam int pricemax) {
        model.addAttribute("vdConsoles", consoleService.consoleRepository.findConsoleBetweenPrices(pricemin, pricemax));
        return "consoles";
    }

    @GetMapping("/filter/videogames/pricefilter/")
    public String getVideogamesBetweenPrices(Model model, @RequestParam int pricemin, @RequestParam int pricemax) {
        model.addAttribute("videogames", videogameService.videogameRepository.findVideogameBetweenPrices(pricemin, pricemax));
        return "videogames";
    }

    @GetMapping("/filter/videogames/pegifilter/")
    public String getVideogamesWhichPegiIs(Model model, @RequestParam int pegi) {
        model.addAttribute("videogames", videogameService.videogameRepository.findGamesPegi(pegi));
        return "videogames";
    }

    @GetMapping("/filter/consoles/controllersfilter/")
    public String getConsolesWithControllers(Model model, @RequestParam int maxcon){
        model.addAttribute("vdConsoles", consoleService.consoleRepository.findConsoleWithControllers(maxcon));
        return "consoles";
    }


}