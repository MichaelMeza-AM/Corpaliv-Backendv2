package cl.corpaliv.producto.controller;

import cl.corpaliv.producto.assemblers.ProductoModelAssembler;
import cl.corpaliv.producto.exception.ResourceNotFoundException;
import cl.corpaliv.producto.model.Producto;
import cl.corpaliv.producto.repository.ProductoRepository;
import java.util.Optional;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ProductoControllerTest {
    @Test void retirarProductoLoOcultaSinPerderDatos() {
        ProductoRepository repo=mock(ProductoRepository.class);
        Producto p=new Producto("Bolsa","Descripción",42000);
        when(repo.findById(4L)).thenReturn(Optional.of(p));
        ProductoController controller=new ProductoController(repo,new ProductoModelAssembler());
        controller.retirar(4L);
        assertFalse(p.activo);
        verify(repo).save(p);
        assertThrows(ResourceNotFoundException.class,()->controller.obtener(4L));
    }
}
