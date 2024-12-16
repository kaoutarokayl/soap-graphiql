package ma.projet.graph.ws;

import ma.projet.graph.entities.Reservation;
import ma.projet.graph.repositories.ReservationRepository;
import ma.projet.graph.entities.Client;
import ma.projet.graph.entities.Chambre;
import ma.projet.graph.repositories.ClientRepository;
import ma.projet.graph.repositories.ChambreRepository;
import jakarta.jws.WebMethod;
import jakarta.jws.WebParam;
import jakarta.jws.WebService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Component
@WebService(serviceName = "ReservationWS")
public class ReservationSoapService {

    @Autowired
    private ReservationRepository reservationRepository;

    @Autowired
    private ClientRepository clientRepository;

    @Autowired
    private ChambreRepository chambreRepository;

    // Récupérer toutes les réservations
    @WebMethod
    public List<Reservation> getAllReservations() {
        return reservationRepository.findAll();
    }

    // Récupérer une réservation par ID
    @WebMethod
    public Reservation getReservationById(@WebParam(name = "id") Long id) {
        return reservationRepository.findById(id).orElse(null);
    }

    // Créer une réservation
    @WebMethod
    public Reservation createReservation(@WebParam(name = "dateDebut") Date dateDebut,
                                         @WebParam(name = "dateFin") Date dateFin,
                                         @WebParam(name = "clientId") Long clientId,
                                         @WebParam(name = "chambreId") Long chambreId,
                                         @WebParam(name = "preferences") String preferences) {
        Optional<Client> clientOpt = clientRepository.findById(clientId);
        Optional<Chambre> chambreOpt = chambreRepository.findById(chambreId);

        if (clientOpt.isPresent() && chambreOpt.isPresent()) {
            Reservation reservation = new Reservation();
            reservation.setDateDebut(dateDebut);
            reservation.setDateFin(dateFin);
            reservation.setPreferences(preferences);
            reservation.setClient(clientOpt.get());
            reservation.setChambre(chambreOpt.get());

            return reservationRepository.save(reservation);
        }
        return null; // Retourne null si client ou chambre introuvable
    }

    // Mettre à jour une réservation
    @WebMethod
    public Reservation updateReservation(@WebParam(name = "id") Long id,
                                         @WebParam(name = "dateDebut") Date dateDebut,
                                         @WebParam(name = "dateFin") Date dateFin,
                                         @WebParam(name = "preferences") String preferences) {
        Optional<Reservation> optionalReservation = reservationRepository.findById(id);

        if (optionalReservation.isPresent()) {
            Reservation reservation = optionalReservation.get();
            reservation.setDateDebut(dateDebut);
            reservation.setDateFin(dateFin);
            reservation.setPreferences(preferences);

            return reservationRepository.save(reservation);
        }
        return null; // Retourne null si la réservation est introuvable
    }

    // Supprimer une réservation
    @WebMethod
    public boolean deleteReservation(@WebParam(name = "id") Long id) {
        if (reservationRepository.existsById(id)) {
            reservationRepository.deleteById(id);
            return true;
        }
        return false; // Retourne false si la réservation n'existe pas
    }
}
