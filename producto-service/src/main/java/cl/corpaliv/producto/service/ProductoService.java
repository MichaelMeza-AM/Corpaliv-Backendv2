package cl.corpaliv.producto.service;

import cl.corpaliv.producto.dto.ProductoRequestDTO;
import cl.corpaliv.producto.exception.ResourceNotFoundException;
import cl.corpaliv.producto.model.Producto;
import cl.corpaliv.producto.repository.ProductoRepository;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ProductoService {
    private final ProductoRepository repository;

    public ProductoService(ProductoRepository repository) {
        this.repository = repository;
    }

    @Transactional(readOnly = true)
    public List<Producto> listar() {
        return repository.findByActivoTrueOrderByIdAsc();
    }

    @Transactional(readOnly = true)
    public Producto obtener(Long id) {
        Producto producto = buscar(id);
        if (!producto.activo) {
            throw new ResourceNotFoundException("Producto no encontrado");
        }
        return producto;
    }

    @Transactional
    public Producto crear(ProductoRequestDTO datos) {
        return repository.save(new Producto(datos.getNombre(), datos.getDescripcion(), datos.getPrecioCLP()));
    }

    @Transactional
    public Producto actualizar(Long id, ProductoRequestDTO datos) {
        Producto producto = buscar(id);
        producto.nombre = datos.getNombre();
        producto.descripcion = datos.getDescripcion();
        producto.precioCLP = datos.getPrecioCLP();
        return repository.save(producto);
    }

    @Transactional
    public void retirar(Long id) {
        Producto producto = buscar(id);
        producto.activo = false; // Los pedidos anteriores conservan nombre y precio.
        repository.save(producto);
    }

    private Producto buscar(Long id) {
        return repository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado"));
    }
}
