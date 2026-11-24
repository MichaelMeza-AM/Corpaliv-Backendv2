package cl.corpaliv.producto.service;

import cl.corpaliv.producto.dto.ProductoRequestDTO;
import cl.corpaliv.producto.exception.ResourceNotFoundException;
import cl.corpaliv.producto.model.Producto;
import cl.corpaliv.producto.repository.ProductoRepository;
import java.util.Optional;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ProductoServiceTest {
    @Test
    void retirarProductoLoOcultaSinPerderDatos() {
        ProductoRepository repository = mock(ProductoRepository.class);
        Producto producto = new Producto("Bolsa", "Descripción", 42000);
        when(repository.findById(4L)).thenReturn(Optional.of(producto));
        ProductoService service = new ProductoService(repository);

        service.retirar(4L);

        assertFalse(producto.activo);
        assertEquals("Bolsa", producto.nombre);
        assertEquals(Integer.valueOf(42000), producto.precioCLP);
        verify(repository).save(producto);
        assertThrows(ResourceNotFoundException.class, () -> service.obtener(4L));
    }

    @Test
    void actualizarConservaIdentidadYEstadoDelProducto() {
        ProductoRepository repository = mock(ProductoRepository.class);
        Producto producto = new Producto("Bolsa", "Antigua", 42000);
        producto.id = 4L;
        producto.activo = false;
        when(repository.findById(4L)).thenReturn(Optional.of(producto));
        when(repository.save(producto)).thenReturn(producto);
        ProductoService service = new ProductoService(repository);

        Producto actualizado = service.actualizar(4L, new ProductoRequestDTO("Bolsa nueva", "Nueva", 45000));

        assertSame(producto, actualizado);
        assertEquals(Long.valueOf(4L), actualizado.id);
        assertEquals("Bolsa nueva", actualizado.nombre);
        assertEquals("Nueva", actualizado.descripcion);
        assertEquals(Integer.valueOf(45000), actualizado.precioCLP);
        assertFalse(actualizado.activo);
    }

    @Test
    void obtenerProductoInexistenteDevuelveError() {
        ProductoRepository repository = mock(ProductoRepository.class);
        when(repository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> new ProductoService(repository).obtener(99L));
    }
}
