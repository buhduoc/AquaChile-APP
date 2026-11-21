package com.example.AquaCheck_Backend.Service;

import com.example.AquaCheck_Backend.Model.Estado;
import com.example.AquaCheck_Backend.Model.ItemCheckList;
import com.example.AquaCheck_Backend.Model.PreChequeo;
import com.example.AquaCheck_Backend.Repository.PreChequeoRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;

@Service
public class PreChequeoService {

    private final PreChequeoRepository repository;

    public PreChequeoService(PreChequeoRepository repository) {
        this.repository = repository;
    }


    //Listar
    public List<PreChequeo> listar() {
        return repository.findAll();
    }


    //Buscar por ID
    public PreChequeo buscarPorId(Long id) {
        return repository.findById(id)
                .orElseThrow(()->
                        new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                "PreChequeo no encontrado")
                );

    }


    //Crear preChequo
    public PreChequeo crearPreChequeo(PreChequeo preChequeo) {

        //El ID lo genera MySQL
        preChequeo.setId(null);


        validarPreChequeo(preChequeo);
        return repository.save(preChequeo);
    }

    //Actualizar PreChequeo
    public PreChequeo actualizar(
            Long id,
            PreChequeo datos
    ) {

        PreChequeo preChequeo =
                buscarPorId(id);

        preChequeo.setCentro(datos.getCentro());
        preChequeo.setFecha(datos.getFecha());
        preChequeo.setBuzo(datos.getBuzo());
        preChequeo.setSupervisor(datos.getSupervisor());
        preChequeo.setSistolica(datos.getSistolica());
        preChequeo.setDiastolica(datos.getDiastolica());
        preChequeo.setSaturacion(datos.getSaturacion());
        preChequeo.setObservaciones(datos.getObservaciones());
        preChequeo.setEdad(datos.getEdad());
        preChequeo.setPesoKg(datos.getPesoKg());
        preChequeo.setAlturaCm(datos.getAlturaCm());
        preChequeo.setExamenPreocupacionalVigente(datos.getExamenPreocupacionalVigente());
        preChequeo.setTestEsfuerzoVigente(datos.getTestEsfuerzoVigente());
        preChequeo.setPuntajeEscuelaCalbuco(datos.getPuntajeEscuelaCalbuco());


        // Actualizar checklist
        preChequeo.getItems().clear();

        if (datos.getItems() != null) {
            preChequeo.getItems()
                    .addAll(datos.getItems());
        }


        // Actualizar fotos
        preChequeo.getFotos().clear();

        if (datos.getFotos() != null) {
            preChequeo.getFotos()
                    .addAll(datos.getFotos());
        }

        // Actualizar alertas
        preChequeo.getAlertas().clear();

        if (datos.getAlertas() != null) {
            preChequeo.getAlertas()
                    .addAll(datos.getAlertas());
        }


        validarPreChequeo(preChequeo);

        return repository.save(preChequeo);
    }



    //Eliminar
    public void eliminar(Long id) {
        PreChequeo preChequeo = buscarPorId(id);

        repository.delete(preChequeo);
    }

    //Validaciones del servidor
    private void validarPreChequeo(PreChequeo preChequeo) {

        List<String> motivosBloqueo =
                new ArrayList<>();

        List<String> advertencias =
                new ArrayList<>();


        // =====================================================
        // 1. IMC
        // =====================================================

        if (preChequeo.getPesoKg() != null &&
                preChequeo.getAlturaCm() != null) {

            double alturaM =
                    preChequeo.getAlturaCm() > 0
                            ? preChequeo.getAlturaCm() / 100.0
                            : 1.70;


            double rawImc =
                    alturaM > 0
                            ? preChequeo.getPesoKg()
                            / (alturaM * alturaM)
                            : 22.0;


            double imc =
                    Math.round(rawImc * 10.0) / 10.0;


            String categoria;

            if (imc < 18.5) {

                categoria = "Bajo peso";

            } else if (imc < 25.0) {

                categoria = "Peso normal";

            } else if (imc < 30.0) {

                categoria = "Sobrepeso";

            } else if (imc < 35.0) {

                categoria = "Obesidad Grado I";

            } else {

                categoria = "Obesidad Severa";
            }


            if (imc >= 35.0) {

                motivosBloqueo.add(
                        "IMC crítico de " +
                                imc +
                                " (" + categoria + ")" +
                                " excede el límite seguro para buceo"
                );

            } else if (imc >= 25.0) {

                advertencias.add(
                        "IMC en rango de " +
                                categoria +
                                " (" + imc + " kg/m²)"
                );
            }
        }


        // =====================================================
        // 2. EDAD + TEST DE ESFUERZO
        // =====================================================

        if (preChequeo.getEdad() != null) {

            if (
                    preChequeo.getEdad() > 45 &&
                            !Boolean.TRUE.equals(
                                    preChequeo.getTestEsfuerzoVigente()
                            )
            ) {

                motivosBloqueo.add(
                        "Buzo de " +
                                preChequeo.getEdad() +
                                " años (mayor de 45) sin Test de Esfuerzo vigente"
                );

            } else if (
                    preChequeo.getEdad() > 40 &&
                            !Boolean.TRUE.equals(
                                    preChequeo.getTestEsfuerzoVigente()
                            )
            ) {

                advertencias.add(
                        "Buzo de " +
                                preChequeo.getEdad() +
                                " años requiere renovar pronto su Test de Esfuerzo"
                );
            }
        }


        // =====================================================
        // 3. EXAMEN PREOCUPACIONAL
        // =====================================================

        if (
                !Boolean.TRUE.equals(
                        preChequeo.getExamenPreocupacionalVigente()
                )
        ) {

            motivosBloqueo.add(
                    "Examen preocupacional no vigente o no presentado"
            );
        }


        // =====================================================
        // 4. PUNTAJE ESCUELA CALBUCO
        // =====================================================

        if (
                preChequeo.getPuntajeEscuelaCalbuco() != null
        ) {

            if (
                    preChequeo.getPuntajeEscuelaCalbuco() < 70
            ) {

                motivosBloqueo.add(
                        "Puntaje en Escuela de Buceo de Calbuco insuficiente (" +
                                preChequeo.getPuntajeEscuelaCalbuco() +
                                "/100 pts, mín. 70)"
                );

            } else if (
                    preChequeo.getPuntajeEscuelaCalbuco() < 85
            ) {

                advertencias.add(
                        "Puntaje regular en Escuela de Buceo Calbuco (" +
                                preChequeo.getPuntajeEscuelaCalbuco() +
                                "/100 pts)"
                );
            }
        }


        // =====================================================
        // 5. SATURACIÓN
        // =====================================================

        if (preChequeo.getSaturacion() != null) {

            if (preChequeo.getSaturacion() < 90) {

                motivosBloqueo.add(
                        "Saturación de oxígeno crítica (" +
                                preChequeo.getSaturacion() +
                                "%, mín. 90%)"
                );

            } else if (
                    preChequeo.getSaturacion() < 95
            ) {

                advertencias.add(
                        "Saturación de O₂ en nivel de atención (" +
                                preChequeo.getSaturacion() +
                                "%)"
                );
            }
        }


        // =====================================================
        // 6. PRESIÓN ARTERIAL
        // =====================================================

        if (
                preChequeo.getSistolica() != null &&
                        preChequeo.getDiastolica() != null
        ) {

            if (
                    preChequeo.getSistolica() >= 160 ||
                            preChequeo.getDiastolica() >= 100
            ) {

                motivosBloqueo.add(
                        "Presión arterial en crisis hipertensiva (" +
                                preChequeo.getSistolica() +
                                "/" +
                                preChequeo.getDiastolica() +
                                " mmHg)"
                );

            } else if (
                    preChequeo.getSistolica() >= 140 ||
                            preChequeo.getDiastolica() >= 90
            ) {

                motivosBloqueo.add(
                        "Presión arterial en nivel hipertensivo de riesgo (" +
                                preChequeo.getSistolica() +
                                "/" +
                                preChequeo.getDiastolica() +
                                " mmHg)"
                );

            } else if (
                    preChequeo.getSistolica() >= 130 ||
                            preChequeo.getDiastolica() >= 85
            ) {

                advertencias.add(
                        "Presión arterial algo elevada (" +
                                preChequeo.getSistolica() +
                                "/" +
                                preChequeo.getDiastolica() +
                                " mmHg)"
                );
            }
        }


        // =====================================================
        // 7. ALERTAS DE SALUD
        // =====================================================

        if (
                preChequeo.getAlertas() != null &&
                        !preChequeo.getAlertas().isEmpty()
        ) {

            advertencias.add(
                    preChequeo.getAlertas().size() +
                            " alerta(s) médica(s) registrada(s) en encuesta preventiva"
            );
        }


        // =====================================================
        // 8. CHECKLIST
        // =====================================================

        if (
                preChequeo.getItems() == null ||
                        preChequeo.getItems().isEmpty()
        ) {

            motivosBloqueo.add(
                    "El pre-chequeo debe contener al menos un ítem"
            );

        } else {

            int observados = 0;


            for (ItemCheckList item :
                    preChequeo.getItems()) {


                if (
                        Boolean.TRUE.equals(
                                item.getCritico()
                        ) &&
                                item.getEstado() ==
                                        Estado.NO_CUMPLE
                ) {

                    motivosBloqueo.add(
                            "Ítem crítico en 'No cumple': " +
                                    item.getNombre()
                    );
                }


                if (
                        item.getEstado() ==
                                Estado.OBSERVADO
                ) {

                    observados++;
                }
            }


            if (observados > 0) {

                advertencias.add(
                        observados +
                                " ítem(s) de seguridad en estado 'Observado'"
                );
            }
        }


        // =====================================================
        // RESULTADO FINAL
        // =====================================================

        if (!motivosBloqueo.isEmpty()) {

            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Pre-chequeo bloqueado: " +
                            String.join(
                                    " | ",
                                    motivosBloqueo
                            )
            );
        }


        // Las advertencias NO bloquean.
        // Solo las mostramos en consola por ahora.

        if (!advertencias.isEmpty()) {

            System.out.println(
                    "===== ADVERTENCIAS PRE-CHEQUEO ====="
            );

            advertencias.forEach(
                    System.out::println
            );
        }
    }
}
