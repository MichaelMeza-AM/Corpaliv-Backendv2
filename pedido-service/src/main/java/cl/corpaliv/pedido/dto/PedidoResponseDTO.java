package cl.corpaliv.pedido.dto;

import cl.corpaliv.pedido.model.Pedido;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PedidoResponseDTO {
    private Long id;

    private Long productoId;

    private String productoNombre;

    private int precioUnitarioCLP;

    private String nombreRetira;

    private int cantidad;

    private long fechaRetiro;

    private boolean envolturaRegalo;

    private boolean aceptaCondiciones;

    private long totalCLP;

    private long fechaRegistro;

    private String estado;

    private String correo;

    private String notas;

    public static PedidoResponseDTO fromModel(Pedido model) {
        if (model == null) return null;
        return new PedidoResponseDTO(
            model.id, model.productoId, model.productoNombre,
            model.precioUnitarioCLP, model.nombreRetira, model.cantidad,
            model.fechaRetiro, model.envolturaRegalo, model.aceptaCondiciones,
            model.totalCLP, model.fechaRegistro, model.estado,
            model.correo, model.notas
        );
    }
}
