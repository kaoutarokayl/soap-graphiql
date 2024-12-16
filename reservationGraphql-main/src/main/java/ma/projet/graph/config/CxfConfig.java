package ma.projet.graph.config;
import lombok.AllArgsConstructor;
import ma.projet.graph.ws.ReservationSoapService;
import org.apache.cxf.Bus;
import org.apache.cxf.jaxws.EndpointImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@AllArgsConstructor
public class CxfConfig {
    private ReservationSoapService reservationSoapService;
    private Bus bus;

    @Bean
    public EndpointImpl endpoint() {
        EndpointImpl endpoint = new EndpointImpl(bus, reservationSoapService);
        endpoint.publish("/ws");
        return endpoint;
    }
}