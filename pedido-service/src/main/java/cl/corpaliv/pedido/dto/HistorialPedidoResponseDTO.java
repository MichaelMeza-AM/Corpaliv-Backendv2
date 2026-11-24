package cl.corpaliv.pedido.dto;

import cl.corpaliv.pedido.model.HistorialPedido;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class HistorialPedidoResponseDTO {
    private Long id;

    private Long pedidoId;

    private String tipo;

    private String descripcion;

    private String estadoAnterior;

    private String estadoNuevo;

    private long fechaRegistro;

    public static HistorialPedidoResponseDTO fromModel(HistorialPedido model) {
        if (model == null) return null;
        return new HistorialPedidoResponseDTO(
            model.id, model.pedidoId, model.tipo,
            model.descripcion, model.estadoAnterior, model.estadoNuevo,
            model.fechaRegistro
        );
    }
}
