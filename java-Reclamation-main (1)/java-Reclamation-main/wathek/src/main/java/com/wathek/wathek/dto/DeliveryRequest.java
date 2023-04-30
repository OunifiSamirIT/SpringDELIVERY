package com.wathek.wathek.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DeliveryRequest implements Serializable {


    private String text;
    private String Description;
    private String Etat;
    private String Reponse;

    private long produit;

    private long livreur;

    private long acheteur;

    public long getAcheteur() {
        return acheteur;
    }


}
