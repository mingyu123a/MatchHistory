package com.example.test.entity;

import jakarta.annotation.Nullable;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
public class UserEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private int tier;

    private String riot_id;
    private String pwd;



    @OneToMany
    @JoinColumn(name = "community")
    private List<CommunityEntity> community = new ArrayList<>();
//
    @OneToMany
    @JoinColumn(name = "champion_analysis")
    private List<ChampionAnalysisEntity> championAnalysis = new ArrayList<>();

    @Builder
    public UserEntity(){

    }

}

