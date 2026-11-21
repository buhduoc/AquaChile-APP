package com.example.AquaCheck_Backend.Service;

import com.example.AquaCheck_Backend.Model.ItemCheckList;
import com.example.AquaCheck_Backend.Repository.ItemCheckListRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class ItemCheckListService {

    private final ItemCheckListRepository repository;

    public ItemCheckListService(ItemCheckListRepository repository) {
        this.repository = repository;
    }

    //Listar todos los items checklist
    public List<ItemCheckList> listarTodos() {
        return repository.findAll();
    }

    //Buscar por ID
    public ItemCheckList buscarPorId(Long id) {
        return repository.findById(id)
                .orElseThrow(()->
                        new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                "El itemCheckList no se encontro."
                        ));

    }


    //Crear itemCheckList
    public ItemCheckList crearCheckList(ItemCheckList itemCheckList) {

        //El ID lo genera MySQL
        itemCheckList.setId(null);

        return repository.save(itemCheckList);
    }

    //Actualizar ItemCheckList
    public ItemCheckList actualizar(Long id, ItemCheckList datos) {

        ItemCheckList itemCheckList = buscarPorId(id);

        itemCheckList.setNombre(datos.getNombre());
        itemCheckList.setCritico(datos.getCritico());
        itemCheckList.setEstado(datos.getEstado());
        itemCheckList.setObservacion(datos.getObservacion());
        itemCheckList.setFoto(datos.getFoto());


        return repository.save(itemCheckList);
    }


    //Eliminar ItemCheckList
    public void eliminar(Long id) {
        ItemCheckList itemCheckList = buscarPorId(id);
        repository.delete(itemCheckList);
    }
}
