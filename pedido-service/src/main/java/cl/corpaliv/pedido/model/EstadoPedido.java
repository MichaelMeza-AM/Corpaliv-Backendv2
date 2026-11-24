package cl.corpaliv.pedido.model;

import java.util.Arrays;

public enum EstadoPedido {

    RECIBIDA,
    EN_REVISION,
    CONTACTADO,
    CONCRETADO,
    DESCARTADO;

    public static boolean esValido(String valor) {
        return Arrays.stream(values())
                .anyMatch(estado -> estado.name().equals(valor));
    }
}