package com.wathek.wathek.services;

import com.wathek.wathek.Entities.*;
import com.wathek.wathek.Repositories.*;
import com.wathek.wathek.dto.PageResponse;
import com.wathek.wathek.dto.DeliveryRequest;
import com.wathek.wathek.dto.DeliveryStatsResponse;
import com.wathek.wathek.exception.InputValidationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import java.util.*;

@Service
public class DeliveryServicesImpl implements IDeliveryServices {



    @Autowired
    IDeliveryRepository reclamationRepository;

    @Autowired
    IProduitRepository produitRepository;


    @Autowired
    NotificationService notificationService;

    @Autowired
    UserRepository userRepository;

    @Autowired
    private BadWordService badWordService;



    @Override
    public void deleteDeliveryById(Long deliveryId) {
        Delivery delivery = reclamationRepository.findById(deliveryId)
                .orElseThrow(() -> new RuntimeException("Delivery not found"));
        reclamationRepository.delete(delivery);
    }
    @Override
    public Delivery ajouterDelivery(DeliveryRequest deliveryRequest) {

        List<String> errorList = validateReclamation(deliveryRequest);
        if (!errorList.isEmpty()) {
            throw new InputValidationException(errorList);
        }

        Produit produit = produitRepository.findById(deliveryRequest.getProduit()).orElseThrow(() -> new RuntimeException("Product not found"));

        User acheteur = userRepository.findById(deliveryRequest.getAcheteur()).orElseThrow(() -> new RuntimeException("User not found"));

        // Check if the user has already made 3 complaints for this product
        if (reclamationRepository.countByAcheteurAndProduit(acheteur, produit) >= 3) {
            // Apply 20% discount on the product's price
            produit.setPrix((float) (produit.getPrix() * 0.8));
            produitRepository.save(produit);
        }

        Delivery delivery = new Delivery();
        delivery.setLivreur(userRepository.findById(deliveryRequest.getLivreur()).get());
        delivery.setAcheteur(acheteur);
        delivery.setProduit(produit);
        delivery.setDescription(deliveryRequest.getText());
        delivery.setDate(new Date());

        return reclamationRepository.save(delivery);
    }
    private List<String> validateReclamation(DeliveryRequest deliveryRequest) {
        List<String> errors = new ArrayList<>();

        if (badWordService.isWordForbidden(deliveryRequest.getText())) {
            errors.add("La description contient un mot interdit.");
        }

        Optional<User> optionalUser = userRepository.findById(deliveryRequest.getLivreur());
        if (!optionalUser.isPresent()) {
            errors.add("id Livreur invalide");
        } else {
            User user = optionalUser.get();
            if (!Role.livreur.equals(user.getRole())) {
                errors.add("Role invalid (livreur)");
            }
        }

        Optional<Produit> optionalProduit = produitRepository.findById(deliveryRequest.getProduit());
        if (!optionalProduit.isPresent()) {
            errors.add("id Produit invalide");
        }

        Optional<User> opionalCustomer = userRepository.findById(deliveryRequest.getAcheteur());
        if (!opionalCustomer.isPresent()) {
            errors.add("id Customer invalide");
        } else {
            User user = opionalCustomer.get();
            if (!Role.acheteur.equals(user.getRole())) {
                errors.add("Role invalid (acheteur)");
            }
        }
        return errors;
    }

    @Override
    public Delivery AddRecAndAssignToCustomer(Delivery r, Integer idCus) {
        return null;
    }
   /* @Override
    public Delivery affecterwathe(Delivery a, Integer idMod) {
        Moderator moderator = moderatorRepository.findById(idMod).get();
        a.setModerators(moderator);
        return reclamationRepository.save(a);

    }*/

    @Override
    public Delivery AddRecAndAssignToCustomer(Delivery r, Long idCus) {
        User customer = userRepository.findById(idCus).get();
        r.setAcheteur(customer);

        return reclamationRepository.save(r);
    }
   /* @RestController
    @RequestMapping("/api")
    public class ComplaintController {

        @Autowired
        private BadWordService badWordService;

        @PostMapping("/complaints")
        public ResponseEntity<String> submitComplaint(@RequestBody String complaintText) {
            String filteredText = badWordService.filterBadWords(complaintText);
            // ici, vous pouvez effectuer d'autres opérations sur la réclamation filtrée,
            // comme l'enregistrer dans une base de données
            return new ResponseEntity<>(filtered*/


   /* @Override
    public Delivery AddRecAndAssignToMod(Delivery rm, Integer idMod) {
        Moderator moderator = moderatorRepository.findById(idMod).get();
        rm.setModerators(moderator);

        return reclamationRepository.save(rm);
    }*/

   /* @Override
    @Transactional
    public Delivery AddrecAndAssignToCusandMod(Delivery rcm, Integer idCus, Integer idMod) {
        Customer customer = customerRepository.findById(idCus).orElse(null);
        Moderator moderator = moderatorRepository.findById(idMod).orElse(null);
        rcm.setCustomers(customer);
        rcm.setModerators(moderator);

        return reclamationRepository.save(rcm);
    }*/


    @Override
    public Delivery retriveRec(Long idRec) {

        return reclamationRepository.findById(idRec).orElse(null);
    }

    @Override
    public List<Delivery> findByetat(Etat etat) {
        return reclamationRepository.findByEtat(etat);
    }

    @Override
    public List<Delivery> getbydate(Date date) {
        return reclamationRepository.getbydate(date);
    }

    @Override
    public void dd(Long idRec) {
        Delivery rt = reclamationRepository.findById(idRec).orElse(null);
        rt.setEtat(Etat.TRAITER);
        reclamationRepository.save(rt);
    }

    @Override
    public long getnbRecByuser(Integer idCus) {
        return reclamationRepository.countByCustomerId(idCus);
    }

    @Override
    public void fixReclamation(long idRec, String reply, String replays ,long idMod) throws InterruptedException {

        Delivery delivery = retriveRec(idRec);

        if (delivery == null) {
            throw new IllegalArgumentException("Invalid id");

        }

        Optional<User> optionalUser = userRepository.findById(idMod);

        if (!optionalUser.isPresent()) {
            throw new IllegalArgumentException("Invalid id");

        }

        User moderateur = optionalUser.get();

        if (!Role.moderateur.equals(moderateur.getRole())) {
            throw new InputValidationException(new ArrayList<>(Collections.singleton("Invalid User Role")));
        }

        User customer = delivery.getAcheteur();
        User livreur = delivery.getLivreur();

        String emailCustomer = customer.getEmailUser();
        String numLivreur = "+216"+ livreur.getNumTel();
        delivery.setEtat(Etat.TRAITER);
        delivery.setReponse(reply);

        delivery.setModerateur(moderateur);

        reclamationRepository.save(delivery);

        notificationService.sendMailNotification(emailCustomer,"Update Delivery", reply);
        notificationService.sendMessageNotification(numLivreur,"+15674065597", replays);
    }

    @Override
    public Delivery getById(long id, boolean read) {

        Optional<Delivery> optionalReclamation = reclamationRepository.findById(id);

        if (optionalReclamation.isPresent()) {
            Delivery delivery = optionalReclamation.get();
            if (read) {
                delivery.setReadr(true);
                reclamationRepository.save(delivery);
            }
            return delivery;
        }
        throw new EntityNotFoundException("Delivery NOT FOUND");
    }

    @Override
    public PageResponse<Delivery> findAll(int page, int size, String sortBy, String sort, boolean all) {
        Pageable pagingSort;
        if (sort.equalsIgnoreCase("desc")) {
            pagingSort = PageRequest.of(page, size, Sort.by(sortBy).descending());
        } else {
            pagingSort = PageRequest.of(page, size, Sort.by(sortBy));
        }
        Page<Delivery> reclamationPage;
        if (all) {

            reclamationPage = reclamationRepository.findAll(pagingSort);
        } else {

            reclamationPage = reclamationRepository.findAllByReadr(false ,pagingSort);
        }
        return new PageResponse<>(reclamationPage.getContent(), reclamationPage.getNumber(),
                reclamationPage.getTotalElements(),
                reclamationPage.getTotalPages());
    }

    @Override
    public DeliveryStatsResponse getStats() {
        DeliveryStatsResponse resp = new DeliveryStatsResponse();
        resp.setTotale(reclamationRepository.count());
        resp.setTraiter(reclamationRepository.countByEtat(Etat.TRAITER));
        resp.setNonTraiter(reclamationRepository.countByEtat(Etat.NONTRAITER));
        resp.setLu(reclamationRepository.countByReadr(true));
        resp.setNonLu(reclamationRepository.countByReadr(false));
        return resp;
    }



    /*
        @Override
        public List<User> rechercheDynamique(String search) {

            return userRepository.rechercheDynamique(search);
        }
          /* @Override
        public int getNombreReclamations(Long id) {

            Produit produit = produitRepository.findById(id).orElse(null);
            if (produit == null) {
                return 0;
            }
            int nombreReclamations = produit.getDelivery();
            if (nombreReclamations > 10) {
                double nouveauPrix = produit.getPrix() * 0.9;
                produit.setPrix((float) nouveauPrix);
                produitRepository.save(produit);
            }
            return nombreReclamations;
        }*/
   /* @Override
    public Iterable<Delivery> search(String description , Long idRec, Date date, Etat etat) {
    Specification<Delivery> spec = Specification.where(null);
    if (description != null && !description.isEmpty()) {
        spec = spec.and((root, query, builder) ->
                builder.like(root.get("description"), "%" + description + "%"));
    }
    if (idRec != null && !idRec.toString().isEmpty()) {
        spec = spec.and((root, query, builder) ->
                builder.like(root.get("IdRec"), "%" + idRec + "%"));
    }
    if (date != null && !date.toString().isEmpty()) {
        spec = spec.and((root, query, builder) ->
                builder.like(root.get("date"), "%" + date + "%"));
    }
    if (etat != null && !etat.toString().isEmpty()) {
        spec = spec.and((root, query, builder) ->
                builder.like(root.get("etat"), "%" + etat + "%"));
    }
    return reclamationRepository.findAll(spec);*/












}





   /* @Override
    public List<Delivery> getAllRecbyetat(Delivery ru String Etat){
        Etat etat1= Etat.NONTRAITER;
         reclamationRepository.findByEtat(etat);
        return null;
    }*/

    /*public void calcNbReclamations(Produit produit,Float Prix ,Long idProduit) {
        Produit produit1 = produitRepository.findById(idProduit).orElse(null);
        int nbReclamations = produit1.getDelivery().size();
        if (nbReclamations > 10) {
            double prix = produit.getPrix();
            prix *= 0.9;  // Applique la réduction de 10%
            produit.setPrix((float) prix);
            produitRepository.save(produit);
        }
    }
}

     */





