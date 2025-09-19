import java.util.HashMap;
import java.util.Map;

class Pedido {
    private Map<Produto, Integer> produtos;
    private double total;
    private TipoPedido tipo;
    private boolean gorjeta;
    private String diaSemana;

    public Pedido(TipoPedido tipo, String diaSemana, boolean gorjeta) {
        this.produtos = new HashMap<>();
        this.total = 0;
        this.tipo = tipo;
        this.gorjeta = gorjeta;
        this.diaSemana = diaSemana;
    }

    public void adicionarProduto(Produto produto, int quantidade) {
        produtos.put(produto, produtos.getOrDefault(produto, 0) + quantidade);
        total += produto.getPreco() * quantidade;
    }

    public double getTotal() {
        return gorjeta ? total * 1.1 : total;
    }

    public Map<Produto, Integer> getProdutos() {
        return produtos;
    }

    public TipoPedido getTipo() { return tipo; }

    public boolean temGorjeta() { return gorjeta; }

    public String getDiaSemana() { return diaSemana; }

    public void listarPedido() {
        System.out.println("Produtos do pedido (" + tipo + ", " + diaSemana + "):");
        for (Map.Entry<Produto, Integer> entry : produtos.entrySet()) {
            System.out.println(entry.getKey().getNome() + " x" + entry.getValue() + " - R$ " + entry.getKey().getPreco() * entry.getValue());
        }
        System.out.println("Total: R$ " + getTotal() + (gorjeta ? " (com 10% gorjeta)" : ""));
    }
}