package com.dws.ActualRetro;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.checkerframework.common.aliasing.qual.Unique;
import org.springframework.beans.factory.annotation.Autowired;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Data
@Entity
@NoArgsConstructor
public class Users {
    //Como HttpServletRequest acoje el atributo/columna "name" y no "username", "name" debería ser Unique.
    //Pero.. qué sentido tiene que los nombres no puedan repertirse? Voto por quitar name y surname, y poner
    //username como "name":
    /*private String name;
    private String surname;
    Así que ya sabéis, dropead las tablas de mysql que tengáis y a generar to' de nuevo u.u*/
    @Unique
    private String name; //Username, lo devuelve request.getUserPrincipal.getName()s
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private long id;
    private String mail;
    private String password;
    private String phone;
    @OneToOne(cascade = CascadeType.ALL)
    private ShoppingCart shoppingCart;
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Videogame> videogamesHistory;
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    private List<VDConsole> consolesHistory;
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Videogame> videogamesUploaded;
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    private List<VDConsole> consolesUploaded;

    //Lista de roles que posee el usuario
    @ElementCollection(fetch = FetchType.EAGER)
    private List<String> roles;
    public Users(String username, String mail, String password, String phone){
        this.name= username;
        this.mail=mail;
        this.password= password;
        this.phone= phone;
        this.shoppingCart=new ShoppingCart();
        this.videogamesHistory = new ArrayList<>();
        this.consolesHistory = new ArrayList<>();
        this.videogamesUploaded = new ArrayList<>();
        this.consolesUploaded = new ArrayList<>();
    }

    public void setId(long id){
        this.id = id;
    }
    public void setShoppingCart(ShoppingCart shopCa){
        this.shoppingCart = shopCa;
    }
    public void buyVideogame(Videogame videogame){
        this.videogamesHistory.add(videogame);
    }
    public void buyConsole(VDConsole vdConsole){
        this.consolesHistory.add(vdConsole);
    }
    public boolean isProppertyOf(long id_db){
        return this.id==id_db;
    }

}
