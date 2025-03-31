import java.util.*;

public class ProducaoVeiculos {
    public static void main(String[] args) {
        Fabrica fabrica = new Fabrica();
        List<Loja> lojas = Arrays.asList(
            new Loja(1, fabrica),
            new Loja(2, fabrica),
            new Loja(3, fabrica)
        );

        fabrica.iniciarProducao();

        lojas.forEach(Loja::iniciar);

        List<Cliente> clientes = new ArrayList<>();
        for (int i = 1; i <= 20; i++) {
            Cliente cliente = new Cliente(i, lojas);
            clientes.add(cliente);
            cliente.start();
        }
    }
}
