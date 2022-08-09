package com.dws.ActualRetro;

import com.dws.ActualRetro.jwt.JwtUtils;
import org.owasp.html.Sanitizers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api")
public class ProductRESTController{
    @Autowired
    VideogameService videogameService;
    @Autowired
    ConsoleService consoleService;
    @Autowired
    UserService userService;
    @Autowired
    JwtUtils jwtUtils;
    @Autowired
    Environment environment;

    /*@PostConstruct
    public void init(){
        Videogame videogame= new Videogame("Super Mario Brothers", 150, 0, new Date(13,9,1985), VDGenre.PLATFORMS, "Original Super Mario Bros for NES");
        Videogame videogame1= new Videogame("GTA V", 200, 18, new Date(13,9,2005), VDGenre.ACTION, "THIEVES AND MONEY DREAMS!");
        VDConsole console= new VDConsole("XBOX 360", 85, 4, new Date(22,11,2005), "Consola de séptima generación creada por Microsoft");
        VDConsole console1= new VDConsole("XBOX 45", 100, 1, new Date(22,11,2005), "Consola de séptima generación creada por Microsoft");
        VDConsole console2= new VDConsole("ps2", 200, 2, new Date(22,11,2005), "Consola de séptima generación creada por Microsoft");
       // Videogame vulnerability = new Videogame("Underpants", 10, 7, new Date(0, 0, 0), VDGenre.CASUAL, Sanitizers.FORMATTING.sanitize("<p><strong>dedededefrfr&nbsp;</strong></p><script>alert('Yooo hacked')</script>"));
        videogameService.videogameRepository.save(videogame);
        videogameService.videogameRepository.save(videogame1);
        //videogameService.videogameRepository.save(vulnerability);
        consoleService.consoleRepository.save(console);
        consoleService.consoleRepository.save(console1);
        consoleService.consoleRepository.save(console2);
    }*/


    @GetMapping("/products/consoles")
    public ResponseEntity<List<VDConsole>> showConsoles(){
        if (!consoleService.consoleRepository.findAll().isEmpty()) {
            List<VDConsole> auxConsoles = new ArrayList<>(consoleService.consoleRepository.findAll());
            return new ResponseEntity<>(auxConsoles, HttpStatus.OK);
        }else{
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
    @GetMapping("/products/games")
    public ResponseEntity<List<Videogame>> showGames(){
        if (!videogameService.videogameRepository.findAll().isEmpty()) {
            List<Videogame> auxGames = new ArrayList<>(videogameService.videogameRepository.findAll());
            return new ResponseEntity<>(auxGames, HttpStatus.OK);
        }
        else{
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
    @GetMapping("/products/consoles/{id}")
    public ResponseEntity<VDConsole> showConsole(@PathVariable long id){
        if (consoleService.consoleRepository.existsById(id)){
            return new ResponseEntity<>(consoleService.consoleRepository.findById(id), HttpStatus.OK);
        }else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

    }
    @GetMapping("/products/games/{id}")
    public ResponseEntity<Videogame> showGame(@PathVariable long id){
        if (videogameService.videogameRepository.existsById(id)) {
            return new ResponseEntity<>(videogameService.videogameRepository.findById(id), HttpStatus.OK);
        }else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @PostMapping("/products/consoles")
    public ResponseEntity<VDConsole> addVConsole(HttpServletRequest request, @RequestBody VDConsole vdConsole){
        List<VDConsole> consoles = consoleService.consoleRepository.findAll();
        vdConsole.setDescription(Sanitizers.FORMATTING.sanitize(vdConsole.getDescription()));
        Optional<Users> user = userService.userRepository.findByName(jwtUtils.getUserNameFromJwtToken(request.getHeader(environment.getProperty("Authorization")).substring(7)));
        System.out.println(jwtUtils.getUserNameFromJwtToken(request.getHeader(environment.getProperty("Authorization")).substring(7)));
        if (user.isPresent()){
            vdConsole.setUser(user.get());
            if (consoles.contains(vdConsole)) {
                VDConsole newcon = consoles.get(consoles.indexOf(vdConsole));
                newcon.addStock();
                consoleService.consoleRepository.save(newcon);
                return new ResponseEntity<>(newcon, HttpStatus.CREATED);
            } else {
                consoleService.consoleRepository.save(vdConsole);
                return new ResponseEntity<>(vdConsole, HttpStatus.CREATED);
            }
        }else{
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @PostMapping("/products/games")
    public ResponseEntity<Videogame> addVideogame(HttpServletRequest request, @RequestBody Videogame videogame){
        List<Videogame> games = videogameService.videogameRepository.findAll();
        videogame.setDescription(Sanitizers.FORMATTING.sanitize(videogame.getDescription()));
        Optional<Users> user = userService.userRepository.findByName(jwtUtils.getUserNameFromJwtToken(request.getHeader(environment.getProperty("Authorization")).substring(7)));
        System.out.println(jwtUtils.getUserNameFromJwtToken(request.getHeader(environment.getProperty("Authorization")).substring(7)));
        if (user.isPresent()) {
            videogame.setUser(user.get());
            if (games.contains(videogame)) {
                Videogame newgame = games.get(games.indexOf(videogame));
                newgame.addStock();
                videogameService.videogameRepository.save(newgame);
                return new ResponseEntity<>(newgame, HttpStatus.CREATED);
            } else {
                videogameService.videogameRepository.save(videogame);
                return new ResponseEntity<>(videogame, HttpStatus.CREATED);
            }
        }else{
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @DeleteMapping("/products/consoles/{id}")
    public ResponseEntity<VDConsole> deleteVDConsole(@PathVariable long id, HttpServletRequest request){
        if (consoleService.consoleRepository.existsById(id)) {
            Optional<Users> user = userService.userRepository.findByName(jwtUtils.getUserNameFromJwtToken(request.getHeader(environment.getProperty("Authorization")).substring(7)));
            if (user.isPresent()) {
                VDConsole vdConsole = consoleService.consoleRepository.findById(id);
                if (user.get().isProppertyOf(vdConsole.getUser().getId())) {
                    vdConsole.removeStock();
                    consoleService.consoleRepository.save(vdConsole);
                    if (!vdConsole.isStock()) {
                        consoleService.consoleRepository.delete(vdConsole);
                    }
                    return new ResponseEntity<>(vdConsole, HttpStatus.OK);
                }else{
                    return new ResponseEntity<>(HttpStatus.FORBIDDEN);
                }
            } else {
                System.out.println("Authentication requiered");
                return new ResponseEntity<>(HttpStatus.FORBIDDEN);
            }
        }else{
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
    @DeleteMapping("/products/games/{id}")
    public ResponseEntity<Videogame> deleteVideogame(@PathVariable long id, HttpServletRequest request){
        if (videogameService.videogameRepository.existsById(id)) {
            Optional<Users> user = userService.userRepository.findByName(jwtUtils.getUserNameFromJwtToken(request.getHeader(environment.getProperty("Authorization")).substring(7)));
            if (user.isPresent()) {
                Videogame videogame = videogameService.videogameRepository.findById(id);
                if (user.get().isProppertyOf(videogame.getUser().getId())) {
                    videogame.removeStock();
                    videogameService.videogameRepository.save(videogame);
                    if (!videogame.isStock()) {
                        videogameService.videogameRepository.delete(videogame);
                    }
                    return new ResponseEntity<>(videogame, HttpStatus.OK);
                }else{
                    return new ResponseEntity<>(HttpStatus.FORBIDDEN);
                }
            }else{
                System.out.println("Authentication requiered");
                return new ResponseEntity<>(HttpStatus.FORBIDDEN);
            }
        }else{
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @PutMapping("/products/consoles/{id}")
    public ResponseEntity<VDConsole> putVDConsole(@PathVariable long id, HttpServletRequest request, @RequestBody VDConsole vdConsole){
        if (consoleService.consoleRepository.existsById(id)){
            Optional<Users> user = userService.userRepository.findByName(jwtUtils.getUserNameFromJwtToken(request.getHeader(environment.getProperty("Authorization")).substring(7)));
            if (user.isPresent()) {
                VDConsole ori = consoleService.consoleRepository.findById(id);
                if (user.get().isProppertyOf(ori.getUser().getId())) {
                    vdConsole.setId(id);
                    vdConsole.setStock(consoleService.consoleRepository.findById(id).getStock());
                    vdConsole.setDescription(Sanitizers.FORMATTING.sanitize(vdConsole.getDescription()));
                    consoleService.consoleRepository.deleteById(id);
                    consoleService.consoleRepository.save(vdConsole);
                    return new ResponseEntity<>(vdConsole, HttpStatus.OK);
                }else{
                    return new ResponseEntity<>(HttpStatus.FORBIDDEN);
                }
            }else{
                System.out.println("Authentication requiered");
                return new ResponseEntity<>(HttpStatus.FORBIDDEN);
            }
        }else{
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
    @PutMapping("/products/games/{id}")
    public ResponseEntity<Videogame> putVideogame(@PathVariable long id, HttpServletRequest request, @RequestBody Videogame videogame){
        if (videogameService.videogameRepository.existsById(id)){
            Optional<Users> user = userService.userRepository.findByName(jwtUtils.getUserNameFromJwtToken(request.getHeader(environment.getProperty("Authorization")).substring(7)));
            if (user.isPresent()) {
                Videogame ori = videogameService.videogameRepository.findById(id);
                if (user.get().isProppertyOf(ori.getUser().getId())) {
                    videogame.setId(id);
                    videogame.setStock(videogameService.videogameRepository.findById(id).getStock());
                    videogame.setDescription(Sanitizers.FORMATTING.sanitize(videogame.getDescription()));
                    videogameService.videogameRepository.save(videogame);
                    return new ResponseEntity<>(videogame, HttpStatus.OK);
                } else {
                    return new ResponseEntity<>(HttpStatus.FORBIDDEN);
                }
            }else{
                System.out.println("Authentication requiered");
                return new ResponseEntity<>(HttpStatus.FORBIDDEN);
            }
        }else{
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }



}
