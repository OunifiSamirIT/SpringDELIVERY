package com.wathek.wathek.services;

import com.wathek.wathek.Entities.Delivery;
import com.wathek.wathek.Entities.Etat;
import com.wathek.wathek.dto.DeliveryRequest;
import com.wathek.wathek.dto.PageResponse;
import com.wathek.wathek.dto.DeliveryStatsResponse;

import java.util.Date;
import java.util.List;

public interface IDeliveryServices {

    void deleteDeliveryById(Long deliveryId);



    Delivery ajouterDelivery(DeliveryRequest deliveryRequest);

    //Delivery ajouterReclamationRedcution(DeliveryRequest reclamationRequest);

    Delivery AddRecAndAssignToCustomer(Delivery r, Integer idCus);


    Delivery AddRecAndAssignToCustomer(Delivery r, Long idCus);

    Delivery retriveRec(Long idRec);


    List<Delivery> findByetat(Etat etat);

    /*int getbydate(Date startDate);*/

    List<Delivery> getbydate(Date date);

    void dd(Long idRec);

    long getnbRecByuser(Integer idCus);


    void fixReclamation(long idRec, String reply,String replays , long idMod) throws InterruptedException;

    Delivery getById(long id, boolean read);

    PageResponse<Delivery> findAll(int page, int size, String sortBy, String sort, boolean all);

    DeliveryStatsResponse getStats();


}
