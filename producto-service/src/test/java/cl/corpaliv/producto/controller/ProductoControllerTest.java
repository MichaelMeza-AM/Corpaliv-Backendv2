package cl.corpaliv.producto.controller;

import cl.corpaliv.producto.assemblers.ProductoModelAssembler;
import cl.corpaliv.producto.dto.ProductoRequestDTO;
import cl.corpaliv.producto.dto.ProductoResponseDTO;
import cl.corpaliv.producto.model.Producto;
import cl.corpaliv.producto.service.ProductoService;
import java.util.List;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ProductoControllerTest {
    @Test
    void listarConvierteProductosActivosEnRespuestas() {
        ProductoService service = mock(ProductoService.class);
        Producto producto = new Producto("Bolsa", "Descripción", 42000);
        producto.id = 4L;
        when(service.listar()).thenReturn(List.of(producto));

        ProductoController controller = new ProductoController(service, new ProductoModelAssembler());
        List<ProductoResponseDTO> respuesta = controller.listar();

        assertEquals(1, respuesta.size());
        assertEquals(Long.valueOf(4L), respuesta.get(0).getId());
        assertEquals("Bolsa", respuesta.get(0).getNombre());
        verify(service).listar();
    }

    @Test
    void crearDelegaEnServicioYDevuelveRespuesta() {
        ProductoService service = mock(ProductoService.class);
        ProductoRequestDTO datos = new ProductoRequestDTO("Bolsa", "Descripción", 42000);
        Producto guardado = new Producto("Bolsa", "Descripción", 42000);
        guardado.id = 4L;
        when(service.crear(datos)).thenReturn(guardado);

        ProductoController controller = new ProductoController(service, new ProductoModelAssembler());
        ProductoResponseDTO respuesta = controller.crear(datos);

        assertEquals(Long.valueOf(4L), respuesta.getId());
        assertEquals(Integer.valueOf(42000), respuesta.getPrecioCLP());
        verify(service).crear(datos);
    }
}
