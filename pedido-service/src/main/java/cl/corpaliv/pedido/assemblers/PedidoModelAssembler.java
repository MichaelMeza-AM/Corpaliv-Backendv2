package cl.corpaliv.pedido.assemblers;

import cl.corpaliv.pedido.dto.HistorialPedidoResponseDTO;
import cl.corpaliv.pedido.dto.PedidoResponseDTO;
import cl.corpaliv.pedido.model.HistorialPedido;
import cl.corpaliv.pedido.model.Pedido;
import org.springframework.stereotype.Component;

@Component
public class PedidoModelAssembler {
    public PedidoResponseDTO toResponse(Pedido pedido) {
        return PedidoResponseDTO.fromModel(pedido);
    }

    public HistorialPedidoResponseDTO toHistorialResponse(HistorialPedido evento) {
        return HistorialPedidoResponseDTO.fromModel(evento);
    }
}
