package pe.edu.ciberteec.rueditas_frontend_b.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.RestTemplate;
import pe.edu.ciberteec.rueditas_frontend_b.dto.BusquedaResponseDTO;
import pe.edu.ciberteec.rueditas_frontend_b.viewmodel.BusquedaModel;

@Controller
@RequestMapping("/inicio")
public class BusquedaController {

    @Autowired
    private RestTemplate restTemplate;

    @GetMapping("/inicio")
    public String inicioBusqueda(Model model) {
        // Iniciar con una búsqueda vacía
        BusquedaModel busquedaModel = new BusquedaModel("", "");
        model.addAttribute("busquedaModel", busquedaModel);
        return "buscar";
    }

    @PostMapping("/resultado")
    public String buscarPlaca(@RequestParam("licensePlate") String licensePlate, Model model) {

        // Validar que la placa no esté vacía
        if (licensePlate == null || licensePlate.trim().isEmpty()) {
            BusquedaModel busquedaModel = new BusquedaModel("01", "Error: Debe ingresar una placa válida");
            model.addAttribute("busquedaModel", busquedaModel);
            return "inicio";
        }

        try {
            // Llamar al servicio REST para buscar la información del vehículo
            String endpoint = "http://localhost:8081/vehiculos/placa/" + licensePlate;
            BusquedaResponseDTO busquedaResponseDTO = restTemplate.getForObject(endpoint, BusquedaResponseDTO.class);

            // Validar si se encontró el vehículo
            if (busquedaResponseDTO != null) {
                model.addAttribute("vehiculo", busquedaResponseDTO);
                return "resultados";  // Redirigir a la vista de resultados con los datos del vehículo

            } else {
                BusquedaModel busquedaModel = new BusquedaModel("02", "Error: No se encontró un vehículo con esa placa");
                model.addAttribute("busquedaModel", busquedaModel);
                return "buscar";  // Volver a la vista de búsqueda si no se encuentra el vehículo
            }

        } catch (Exception e) {
            // Manejo de errores en caso de que falle la llamada al servicio
            BusquedaModel busquedaModel = new BusquedaModel("99", "Error: Ocurrió un problema en la búsqueda");
            model.addAttribute("busquedaModel", busquedaModel);
            System.out.println(e.getMessage());
            return "buscar";  // Volver a la vista de búsqueda en caso de error
        }
    }
}
