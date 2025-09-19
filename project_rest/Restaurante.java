import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class Restaurante {
    private static ArrayList<Produto> cardapio = new ArrayList<>();
    private static ArrayList<Pedido> pedidos = new ArrayList<>();
    private static Scanner scanner = new Scanner(System.in);
    private static String[] diasSemana = {"Segunda", "Terça", "Quarta", "Quinta", "Sexta", "Sábado", "Domingo"};

    public static void main(String[] args) {
        inicializarCardapio();
        int opcao;

        do {
            System.out.println("\n=== Sistema de Restaurante ===");
            System.out.println("1 - Listar Cardápio");
            System.out.println("2 - Fazer Pedido");
            System.out.println("3 - Listar Pedidos");
            System.out.println("4 - Relatórios");
            System.out.println("5 - Sair");
            System.out.print("Escolha uma opção: ");
            opcao = scanner.nextInt();

            switch (opcao) {
                case 1 -> listarCardapio();
                case 2 -> fazerPedido();
                case 3 -> listarPedidos();
                case 4 -> gerarRelatorios();
                case 5 -> System.out.println("Saindo...");
                default -> System.out.println("Opção inválida!");
            }

        } while (opcao != 5);
    }

    private static void inicializarCardapio() {
        cardapio.add(new Produto(1, "Hambúrguer", 15.0));
        cardapio.add(new Produto(2, "Pizza Calabresa", 30.0));
        cardapio.add(new Produto(3, "Refrigerante", 5.0));
        cardapio.add(new Produto(4, "Salada", 12.0));
        cardapio.add(new Produto(5, "Combo Burguer + Refri", 18.0));
    }

    private static void listarCardapio() {
        System.out.println("\n=== Cardápio ===");
        for (Produto p : cardapio) {
            System.out.println(p);
        }
    }

    private static void fazerPedido() {
        System.out.println("\nDigite o dia da semana do pedido:");
        for (int i = 0; i < diasSemana.length; i++) {
            System.out.println((i + 1) + " - " + diasSemana[i]);
        }
        int dia = scanner.nextInt();
        String diaSemana = diasSemana[dia - 1];

        System.out.println("Tipo de pedido: 1 - Mesa / 2 - Delivery");
        TipoPedido tipo = scanner.nextInt() == 1 ? TipoPedido.MESA : TipoPedido.DELIVERY;

        System.out.println("Deseja pagar 10% de gorjeta? 1 - Sim / 2 - Não");
        boolean gorjeta = scanner.nextInt() == 1;

        Pedido pedido = new Pedido(tipo, diaSemana, gorjeta);

        int codigoProduto;
        int quantidade;
        System.out.println("Digite os códigos dos produtos (0 para finalizar):");
        do {
            System.out.print("Código do produto: ");
            codigoProduto = scanner.nextInt();
            if (codigoProduto != 0) {
                Produto produto = buscarProduto(codigoProduto);
                if (produto != null) {
                    System.out.print("Quantidade: ");
                    quantidade = scanner.nextInt();
                    pedido.adicionarProduto(produto, quantidade);
                    System.out.println(produto.getNome() + " x" + quantidade + " adicionado ao pedido.");
                } else {
                    System.out.println("Produto não encontrado.");
                }
            }
        } while (codigoProduto != 0);

        pedido.listarPedido();
        pedidos.add(pedido);
    }

    private static Produto buscarProduto(int codigo) {
        for (Produto p : cardapio) {
            if (p.getCodigo() == codigo) {
                return p;
            }
        }
        return null;
    }

    private static void listarPedidos() {
        System.out.println("\n=== Lista de Pedidos ===");
        if (pedidos.isEmpty()) {
            System.out.println("Nenhum pedido realizado.");
        } else {
            int count = 1;
            for (Pedido p : pedidos) {
                System.out.println("\nPedido #" + count);
                p.listarPedido();
                count++;
            }
        }
    }

    private static void gerarRelatorios() {
        System.out.println("\n=== Relatórios ===");

        System.out.println("\n1. Item mais pedido por dia:");
        for (String dia : diasSemana) {
            Map<String, Integer> contador = new HashMap<>();
            for (Pedido p : pedidos) {
                if (p.getDiaSemana().equals(dia)) {
                    for (Map.Entry<Produto, Integer> entry : p.getProdutos().entrySet()) {
                        contador.put(entry.getKey().getNome(),
                                contador.getOrDefault(entry.getKey().getNome(), 0) + entry.getValue());
                    }
                }
            }
            String maisPedido = contador.entrySet().stream()
                    .max(Map.Entry.comparingByValue())
                    .map(Map.Entry::getKey)
                    .orElse("Nenhum");
            System.out.println(dia + ": " + maisPedido);
        }

        double totalFaturamento = 0;
        for (Pedido p : pedidos) totalFaturamento += p.getTotal();
        double ticketMedio = pedidos.isEmpty() ? 0 : totalFaturamento / pedidos.size();
        System.out.println("\n2. Ticket médio dos clientes: R$ " + ticketMedio);

        System.out.println("\n3. Rank de movimentação dos dias:");
        Map<String, Integer> pedidosPorDia = new HashMap<>();
        for (String dia : diasSemana) pedidosPorDia.put(dia, 0);
        for (Pedido p : pedidos) pedidosPorDia.put(p.getDiaSemana(), pedidosPorDia.get(p.getDiaSemana()) + 1);
        pedidosPorDia.entrySet().stream()
                .sorted((a, b) -> b.getValue() - a.getValue())
                .forEach(e -> System.out.println(e.getKey() + ": " + e.getValue() + " pedidos"));

        Map<String, Integer> combosDelivery = new HashMap<>();
        Map<String, Integer> combosMesa = new HashMap<>();
        for (Pedido p : pedidos) {
            for (Map.Entry<Produto, Integer> entry : p.getProdutos().entrySet()) {
                if (entry.getKey().getNome().toLowerCase().contains("combo")) {
                    if (p.getTipo() == TipoPedido.DELIVERY)
                        combosDelivery.put(entry.getKey().getNome(),
                                combosDelivery.getOrDefault(entry.getKey().getNome(), 0) + entry.getValue());
                    else
                        combosMesa.put(entry.getKey().getNome(),
                                combosMesa.getOrDefault(entry.getKey().getNome(), 0) + entry.getValue());
                }
            }
        }
        String comboDelivery = combosDelivery.entrySet().stream().max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey).orElse("Nenhum");
        String comboMesa = combosMesa.entrySet().stream().max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey).orElse("Nenhum");
        System.out.println("\n4. Combo mais vendido:");
        System.out.println("Delivery: " + comboDelivery);
        System.out.println("Mesa: " + comboMesa);

        double faturamentoDelivery = 0;
        double faturamentoMesa = 0;
        for (Pedido p : pedidos) {
            if (p.getTipo() == TipoPedido.DELIVERY) faturamentoDelivery += p.getTotal();
            else faturamentoMesa += p.getTotal();
        }
        System.out.println("\n5. Faturamento:");
        System.out.println("Delivery: R$ " + faturamentoDelivery);
        System.out.println("Mesa: R$ " + faturamentoMesa);

        long clientesComGorjeta = pedidos.stream().filter(Pedido::temGorjeta).count();
        double percentualGorjeta = pedidos.isEmpty() ? 0 : (clientesComGorjeta * 100.0 / pedidos.size());
        System.out.println("\n6. Percentual de clientes que pagam 10% de gorjeta: " + percentualGorjeta + "%");
    }
}

enum TipoPedido {
    DELIVERY, MESA
}