package com.dws.ActualRetro;

import lombok.*;
import org.springframework.security.core.userdetails.User;

import javax.persistence.*;
import java.util.Objects;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class VDConsole {
    private String name;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    private float price;
    private int stock = 1;
    private int maxcontrollers;
    @Transient
    private Date date;
    private String description;
    @ManyToOne(cascade = CascadeType.ALL)
    private Users user;

    public VDConsole(String na, float pri, int maxcon, Date date, String description){
        this.name = na;
        this.price = pri;
        this.maxcontrollers = maxcon;
        this.date = date;
        this.description = description;
    }


    public void setId(long num){
        this.id = num;
    }
    public long getId(){
        return this.id;
    }

    public boolean isStock(){
        return this.stock > 0;
    }

    public void addStock(){
        this.stock++;
    }
    public void removeStock(){
        this.stock--;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        VDConsole vdConsole = (VDConsole) o;
        if (vdConsole.getUser() == null && this.getUser() == null) return true;
        if (vdConsole.getUser() == null) return false;
        return name.equals(vdConsole.name) && user.getId()== vdConsole.getUser().getId();
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }
}
