package com.makedreamteam.capstoneback.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;

import java.util.UUID;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class UserLang {
    @Id
    private UUID userid;

    @Column
    @ColumnDefault("0")
    private int python;

    @Column
    @ColumnDefault("0")
    private int c;

    @Column
    @ColumnDefault("0")
    private int java;

    @Column
    @ColumnDefault("0")
    private int cpp;

    @Column
    @ColumnDefault("0")
    private int cs;

    @Column
    @ColumnDefault("0")
    private int vb;

    @Column
    @ColumnDefault("0")
    private int javascript;

    @Column
    @ColumnDefault("0")
    private int assembly;

    @Column
    @ColumnDefault("0")
    private int php;

    @Column
    @ColumnDefault("0")
    private int sqllang;


    public UserLang(UUID userid, int python, int c, int java, int cpp, int cs, int vb, int javascript, int assembly, int php, int sqllang) {
        this.userid = userid;
        this.python = python;
        this.c = c;
        this.java = java;
        this.cpp = cpp;
        this.cs = cs;
        this.vb = vb;
        this.javascript = javascript;
        this.assembly = assembly;
        this.php = php;
        this.sqllang = sqllang;
    }
}
