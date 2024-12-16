package ma.projet.graph.controllers;

import lombok.AllArgsConstructor;
import ma.projet.graph.entities.Chambre;
import ma.projet.graph.entities.Client;
import ma.projet.graph.entities.Reservation;
import ma.projet.graph.repositories.ChambreRepository;
import ma.projet.graph.repositories.ClientRepository;
import ma.projet.graph.repositories.ReservationRepository;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Controller
@AllArgsConstructor
public class ReservationGraphQLController {

    private final ReservationRepository reservationRepository;
    private final ClientRepository clientRepository;
    private final ChambreRepository chambreRepository;

    // Récupérer toutes les réservations
    @QueryMapping
    public List<Reservation> getReservations() {
        return reservationRepository.findAll();
    }

    // Récupérer une réservation par ID
    @QueryMapping
    public Optional<Reservation> getReservation(@Argument Long id) {
        return reservationRepository.findById(id);
    }

    // Créer une réservation
    @MutationMapping
    public Reservation createReservation(@Argument Long client,
                                         @Argument Long chambre,
                                         @Argument String dateDebut,
                                         @Argument String dateFin,
                                         @Argument String preferences) throws ParseException {

        Optional<Client> optionalClient = clientRepository.findById(client);
        Optional<Chambre> optionalChambre = chambreRepository.findById(chambre);

        if (optionalClient.isPresent() && optionalChambre.isPresent()) {
            Client clientEntity = optionalClient.get();
            Chambre chambreEntity = optionalChambre.get();

            // Conversion des dates de String à Date avec SimpleDateFormat
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            Date dateDebutDate = dateFormat.parse(dateDebut);
            Date dateFinDate = dateFormat.parse(dateFin);

            Reservation reservation = new Reservation(null, clientEntity, chambreEntity, dateDebutDate, dateFinDate, preferences);
            return reservationRepository.save(reservation);
        }
        throw new IllegalArgumentException("Client ou Chambre non trouvé");
    }

    // Mettre à jour une réservation
    @MutationMapping
    public Reservation updateReservation(@Argument Long id,
                                         @Argument Long client,
                                         @Argument Long chambre,
                                         @Argument String dateDebut,
                                         @Argument String dateFin,
                                         @Argument String preferences) throws ParseException {

        Optional<Reservation> optionalReservation = reservationRepository.findById(id);

        if (optionalReservation.isPresent()) {
            Reservation reservation = optionalReservation.get();

            // Mise à jour du client
            if (client != null) {
                Optional<Client> optionalClient = clientRepository.findById(client);
                optionalClient.ifPresent(reservation::setClient);
            }

            // Mise à jour de la chambre
            if (chambre != null) {
                Optional<Chambre> optionalChambre = chambreRepository.findById(chambre);
                optionalChambre.ifPresent(reservation::setChambre);
            }

            // Mise à jour des dates (conversion des chaînes en objets Date)
            if (dateDebut != null) {
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                reservation.setDateDebut(dateFormat.parse(dateDebut));
            }
            if (dateFin != null) {
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                reservation.setDateFin(dateFormat.parse(dateFin));
            }

            // Mise à jour des préférences
            if (preferences != null) {
                reservation.setPreferences(preferences);
            }

            return reservationRepository.save(reservation);
        }
        throw new IllegalArgumentException("Réservation non trouvée");
    }

    // Supprimer une réservation
    @MutationMapping
    public boolean deleteReservation(@Argument Long id) {
        try {
            reservationRepository.deleteById(id);
            return true;
        } catch (Exception e) {
            // Vous pouvez ajouter un log ou un message d'erreur ici
            return false;
        }
    }
}
