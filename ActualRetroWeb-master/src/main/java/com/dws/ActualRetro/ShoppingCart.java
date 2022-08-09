package com.dws.ActualRetro;
import lombok.Data;


import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;


//THERE WOULD BE ONE SHOPPING CART PER USER.

@Data
@Entity
public class ShoppingCart {
    private long totalProducts;
    private float totalPrice;
    @ManyToMany(cascade = CascadeType.ALL)
    private List<VDConsole> consoleList = new ArrayList<>();
    @ManyToMany(cascade = CascadeType.ALL)
    private List<Videogame> videogameList = new ArrayList<>();
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long idShoppingCart;


    public ShoppingCart() {
        this.totalPrice = 0;
        this.totalProducts = 0;
    }

    public void addConsole(VDConsole console) {
        if (!this.consoleList.contains(console)) {
            consoleList.add(console);
            totalProducts++;
            totalPrice = totalPrice + console.getPrice();
        }else{
            System.out.println("Console already added");
        }
    }

    public void addVideogame(Videogame videogame) {
        if (!this.videogameList.contains(videogame)) {
            videogameList.add(videogame);
            totalProducts++;
            totalPrice = totalPrice + videogame.getPrice();
        }else{
            System.out.println("Videogame already added");
        }
    }

    public void deleteConsole(VDConsole console) {
        if (consoleList.contains(console)) {
            consoleList.remove(console);
            totalProducts--;
            totalPrice = totalPrice - console.getPrice();
        }
    }

    public void deleteVideogame(Videogame videogame) {
        if (videogameList.contains(videogame)) {
            videogameList.remove(videogame);
            totalProducts--;
            totalPrice = totalPrice - videogame.getPrice();
        }
    }
}

