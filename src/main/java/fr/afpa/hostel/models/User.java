package fr.afpa.hostel.models;

import java.util.Collection;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

import jakarta.persistence.Table;

@Entity
@Table(name = "user")
public class User implements UserDetails {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(nullable = false)
    private String email;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String password;

    @Column(name="is_admin")
    private boolean isAdmin;

    // @ManyToMany
    // @JoinTable(
    //     name = "user_role",
    //     joinColumns = @JoinColumn(name = "id_role"),
    //     inverseJoinColumns = @JoinColumn(name = "id_user")
    // )
    // private Collection<Role> roles;

    public User() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public boolean isAdmin() {
        return this.isAdmin;
    }

    public void setIsAdmin(boolean isAdmin) {
        this.isAdmin = isAdmin;
    }

    /**
     * Renvoie les autorités de l'utilisateur.
     *
     * La syntaxe "? extends GrantedAuthority" permet d'indiquer : toute classe héritant ou implémentant GrantedAuthority
     */
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of();
    }


    /** 
     * Retourne l'email car c'est l'information utilisée pour la connexion.
     */
    @Override
    public String getUsername() {
        return this.email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }


    // public Collection<Role> getRoles() {
    //     return roles;
    // }

    // public void setRoles(Collection<Role> roles) {
    //     this.roles = roles;
    // }

    // public void addRole(Role role){
    //     this.roles.add(role);
    // }

    
}
