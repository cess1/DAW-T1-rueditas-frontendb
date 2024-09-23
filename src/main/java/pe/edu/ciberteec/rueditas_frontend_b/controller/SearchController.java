package pe.edu.ciberteec.rueditas_frontend_b.controller;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.RestTemplate;
import pe.edu.ciberteec.rueditas_frontend_b.dto.BusquedaRequestDTO;
import pe.edu.ciberteec.rueditas_frontend_b.dto.BusquedaResponseDTO;
import pe.edu.ciberteec.rueditas_frontend_b.viewmodel.BusquedaModel;

@Controller
@RequestMapping("/search")
public class SearchController {
    @Autowired
    RestTemplate restTemplate;

    @GetMapping("/inicio")
    public String inicio(Model model) {
        // Inicializar campos vacíos en lugar de variables no definidas
        BusquedaModel busquedaModel = new BusquedaModel("00", "", "", "", "", 0, 0, "");
        model.addAttribute("busquedaModel", busquedaModel);
        return "inicio";
    }

    @PostMapping("/autenticar")
    public String autenticar(@RequestParam("placa") String placa, Model model) {

        // Validar el campo de entrada
        if (placa == null || placa.trim().length() == 0) {
            BusquedaModel busquedaModel = new BusquedaModel("01", "Error: Debe completar correctamente la placa", "", "", "", 0, 0, "");
            model.addAttribute("busquedaModel", busquedaModel);
            return "inicio";
        }

        try {
            // Invocar API de validación de vehículo
            String endpoint = "http://localhost:8081/autenticacion/search";
            BusquedaRequestDTO busquedaRequestDTO = new BusquedaRequestDTO(placa);
            BusquedaResponseDTO busquedaResponseDTO = restTemplate.postForObject(endpoint, busquedaRequestDTO, BusquedaResponseDTO.class);

            // Validar respuesta
            if (busquedaResponseDTO.marca().equals("Error")) {
                BusquedaModel busquedaModel = new BusquedaModel("02", "Error: Vehículo no encontrado", "", "", "", 0, 0, "");
                model.addAttribute("busquedaModel", busquedaModel);
                return "inicio";
            }

            // Si la respuesta es exitosa, mostrar los datos del vehículo
            BusquedaModel busquedaModel = new BusquedaModel("00", "",
                    placa,
                    busquedaResponseDTO.marca(),
                    busquedaResponseDTO.modelo(),
                    busquedaResponseDTO.nro_asientos(),
                    busquedaResponseDTO.precio(),
                    busquedaResponseDTO.color());
            model.addAttribute("busquedaModel", busquedaModel);
            return "resultados";

        } catch (Exception e) {
            BusquedaModel busquedaModel = new BusquedaModel("99", "Error: Ocurrió un problema en la autenticación", "", "", "", 0, 0, "");
            model.addAttribute("busquedaModel", busquedaModel);
            System.out.println(e.getMessage());
            return "inicio";
        }
    }
}
