import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;
import java.awt.*;
import java.util.*;
import java.io.*;
import java.text.SimpleDateFormat;

// ==================== MODELOS ====================
class Producto {
    String id, nombre;
    double precio;
    int stock;

    Producto(String id, String nombre, double precio, int stock) {
        this.id = id; this.nombre = nombre; this.precio = precio; this.stock = stock;
    }
}

class ItemCarrito {
    Producto producto;
    int cantidad;
    double subtotal;

    ItemCarrito(Producto p, int c) {
        producto = p;
        cantidad = c;
        subtotal = p.precio * c;
    }
}

class PedidoCocina {
    String id;
    String cliente;
    String registro; // Número de registro o mesa
    String items;
    double total;
    String estado;
    String hora;

    PedidoCocina(String id, String c, String reg, String i, double t) {
        this.id = id;
        this.cliente = c;
        this.registro = reg;
        this.items = i;
        this.total = t;
        this.estado = "NUEVO";
        this.hora = new SimpleDateFormat("HH:mm:ss").format(new Date());
    }
}

class Factura {
    String numero;
    String fecha;
    String hora;
    String cliente;
    String registro;
    ArrayList<ItemCarrito> items;
    double subtotal;
    double impuestos;
    double total;
    String estadoPago;
    String estadoPedido;

    Factura(String cliente, String registro) {
        this.numero = "FAC-" + (int)(Math.random() * 100000);
        this.fecha = new SimpleDateFormat("dd/MM/yyyy").format(new Date());
        this.hora = new SimpleDateFormat("HH:mm:ss").format(new Date());
        this.cliente = cliente;
        this.registro = registro;
        this.items = new ArrayList<>();
        this.estadoPago = "PENDIENTE";
        this.estadoPedido = "EN_PROCESO";
    }

    void calcularTotales() {
        subtotal = 0;
        for (ItemCarrito i : items) subtotal += i.subtotal;
        impuestos = subtotal * 0.16;
        total = subtotal + impuestos;
    }
}

// ==================== VENTANA PRINCIPAL ====================
public class CafeteriaFinal extends JFrame {

    ArrayList<Producto> catalogo = new ArrayList<>();
    ArrayList<ItemCarrito> carrito = new ArrayList<>();
    ArrayList<Factura> historialFacturas = new ArrayList<>();
    ArrayList<PedidoCocina> pedidosCocina = new ArrayList<>();

    JPanel panelMenu, panelCarrito, panelFactura, panelCocina, panelSemaforo, panelHistorial;
    CardLayout cardLayout;
    JPanel mainPanel;
    DefaultListModel<String> modeloCarrito, modeloCocina, modeloSemaforo;
    JList<String> listaCarrito, listaCocina, listaSemaforo;
    JLabel lblTotalCarrito;
    JTextArea areaFactura;

    // Campos de cliente
    JTextField txtCliente, txtRegistro;
    String clienteActual = "";
    String registroActual = "";

    // Pedido actual
    PedidoCocina pedidoActual = null;
    Factura facturaActual = null;

    // Botones semáforo
    JButton btnSemaforoUsuario, btnSemaforoCocina;
    JLabel lblEstadoUsuario, lblEstadoCocina;

    public CafeteriaFinal() {
        inicializarCatalogo();

        setTitle("Cafetería Universitaria - Sistema Completo");
        setSize(1100, 750);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);

        crearPanelMenu();
        crearPanelCarrito();
        crearPanelFactura();
        crearPanelCocina();
        crearPanelSemaforo();
        crearPanelHistorial();

        add(mainPanel);
        setVisible(true);
    }

    void inicializarCatalogo() {
        catalogo.add(new Producto("P1", "Café Americano", 2500.00, 50));
        catalogo.add(new Producto("P2", "Café Latte", 3500.00, 40));
        catalogo.add(new Producto("P3", "Cappuccino", 3200.00, 30));
        catalogo.add(new Producto("P4", "Croissant", 2800.00, 25));
        catalogo.add(new Producto("P5", "Sandwich Pavo", 4500.00, 20));
        catalogo.add(new Producto("P6", "Arepas de queso", 2200.00, 35));
        catalogo.add(new Producto("P7", "Jugo Naranja", 2000.00, 40));
        catalogo.add(new Producto("P8", "Agua Embotellada", 1500.00, 60));
    }

    // ==================== PANEL 1: MENÚ ====================
    void crearPanelMenu() {
        panelMenu = new JPanel(new BorderLayout(10, 10));
        panelMenu.setBorder(new EmptyBorder(15, 15, 15, 15));
        panelMenu.setBackground(new Color(240, 248, 255));

        // Título
        JLabel titulo = new JLabel("☕ MENÚ CAFETERÍA UNIVERSITARIA", SwingConstants.CENTER);
        titulo.setFont(new Font("Arial", Font.BOLD, 28));
        panelMenu.add(titulo, BorderLayout.NORTH);

        // Panel de datos del cliente
        JPanel panelCliente = new JPanel(new GridLayout(2, 2, 10, 5));
        panelCliente.setBorder(BorderFactory.createTitledBorder("DATOS DEL CLIENTE"));
        panelCliente.setBackground(new Color(232, 244, 248));

        panelCliente.add(new JLabel("Nombre:"));
        txtCliente = new JTextField("Juan Pérez");
        panelCliente.add(txtCliente);

        panelCliente.add(new JLabel("No. Registro / Mesa:"));
        txtRegistro = new JTextField("A-123");
        panelCliente.add(txtRegistro);

        // Grid de productos
        JPanel grid = new JPanel(new GridLayout(4, 2, 15, 15));
        grid.setBackground(new Color(240, 248, 255));

        for (Producto p : catalogo) {
            JPanel card = crearCardProducto(p);
            grid.add(card);
        }

        // Panel central combina cliente + productos
        JPanel panelCentral = new JPanel(new BorderLayout(10, 10));
        panelCentral.setBackground(new Color(240, 248, 255));
        panelCentral.add(panelCliente, BorderLayout.NORTH);
        panelCentral.add(grid, BorderLayout.CENTER);

        panelMenu.add(panelCentral, BorderLayout.CENTER);

        // Botones inferiores
        JPanel panelSur = new JPanel(new GridLayout(2, 1, 5, 5));

        JLabel lblInfo = new JLabel("Ingrese sus datos y seleccione productos",
                SwingConstants.CENTER);
        lblInfo.setFont(new Font("Arial", Font.ITALIC, 12));
        lblInfo.setForeground(Color.GRAY);

        JPanel panelBotones = new JPanel(new FlowLayout());

        JButton btnCarrito = new JButton("🛒 VER CARRITO (" + carrito.size() + " items)");
        btnCarrito.setFont(new Font("Arial", Font.BOLD, 16));
        btnCarrito.setBackground(new Color(52, 152, 219));
        btnCarrito.setForeground(Color.WHITE);
        btnCarrito.addActionListener(e -> {
            if (txtCliente.getText().trim().isEmpty() || txtRegistro.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(this,
                        "Por favor ingrese su nombre y número de registro/mesa",
                        "Datos incompletos", JOptionPane.WARNING_MESSAGE);
                return;
            }
            clienteActual = txtCliente.getText();
            registroActual = txtRegistro.getText();
            actualizarCarrito();
            cardLayout.show(mainPanel, "CARRITO");
        });

        JButton btnHistorial = new JButton("📊 HISTORIAL DEL DÍA");
        btnHistorial.setBackground(new Color(155, 89, 182));
        btnHistorial.setForeground(Color.WHITE);
        btnHistorial.setFont(new Font("Arial", Font.BOLD, 14));
        btnHistorial.addActionListener(e -> {
            actualizarTablaHistorial();
            cardLayout.show(mainPanel, "HISTORIAL");
        });

        panelBotones.add(btnCarrito);
        panelBotones.add(btnHistorial);

        panelSur.add(lblInfo);
        panelSur.add(panelBotones);

        panelMenu.add(panelSur, BorderLayout.SOUTH);
        mainPanel.add(panelMenu, "MENU");
    }

    JPanel crearCardProducto(Producto p) {
        JPanel card = new JPanel(new BorderLayout(5, 5));
        card.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(new Color(200, 200, 200), 1, true),
                new EmptyBorder(10, 10, 10, 10)
        ));
        card.setBackground(Color.WHITE);

        JPanel info = new JPanel(new GridLayout(3, 1));
        info.setBackground(Color.WHITE);

        JLabel lblNombre = new JLabel(p.nombre, SwingConstants.CENTER);
        lblNombre.setFont(new Font("Arial", Font.BOLD, 14));

        JLabel lblPrecio = new JLabel(String.format("$%.2f", p.precio), SwingConstants.CENTER);
        lblPrecio.setFont(new Font("Arial", Font.BOLD, 20));
        lblPrecio.setForeground(new Color(46, 204, 113));

        JLabel lblStock = new JLabel("Stock: " + p.stock, SwingConstants.CENTER);
        lblStock.setFont(new Font("Arial", Font.PLAIN, 11));
        lblStock.setForeground(Color.GRAY);

        info.add(lblNombre);
        info.add(lblPrecio);
        info.add(lblStock);

        JPanel controles = new JPanel(new FlowLayout());
        controles.setBackground(Color.WHITE);

        SpinnerNumberModel model = new SpinnerNumberModel(1, 1, p.stock, 1);
        JSpinner spinner = new JSpinner(model);
        spinner.setPreferredSize(new Dimension(50, 25));

        JButton btnAgregar = new JButton("➕ Agregar");
        btnAgregar.setBackground(new Color(46, 204, 113));
        btnAgregar.setForeground(Color.WHITE);
        btnAgregar.setFont(new Font("Arial", Font.BOLD, 11));

        btnAgregar.addActionListener(e -> {
            if (txtCliente.getText().trim().isEmpty() || txtRegistro.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(this,
                        "Primero ingrese sus datos arriba",
                        "Atención", JOptionPane.WARNING_MESSAGE);
                return;
            }

            int cantidad = (Integer) spinner.getValue();
            if (cantidad > p.stock) {
                JOptionPane.showMessageDialog(this,
                        "Stock insuficiente", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            carrito.add(new ItemCarrito(p, cantidad));
            p.stock -= cantidad;
            lblStock.setText("Stock: " + p.stock);

            JOptionPane.showMessageDialog(this,
                    cantidad + "x " + p.nombre + " agregado\nSubtotal: $" +
                            String.format("%.2f", p.precio * cantidad),
                    "Producto Agregado", JOptionPane.INFORMATION_MESSAGE);
        });

        controles.add(new JLabel("Cant:"));
        controles.add(spinner);
        controles.add(btnAgregar);

        card.add(info, BorderLayout.CENTER);
        card.add(controles, BorderLayout.SOUTH);

        return card;
    }

    // ==================== PANEL 2: CARRITO ====================
    void crearPanelCarrito() {
        panelCarrito = new JPanel(new BorderLayout(10, 10));
        panelCarrito.setBorder(new EmptyBorder(15, 15, 15, 15));
        panelCarrito.setBackground(new Color(232, 244, 248));

        JLabel titulo = new JLabel("🛒 MI CARRITO", SwingConstants.CENTER);
        titulo.setFont(new Font("Arial", Font.BOLD, 24));
        panelCarrito.add(titulo, BorderLayout.NORTH);

        // Info cliente
        JPanel panelInfoCliente = new JPanel(new GridLayout(1, 2));
        panelInfoCliente.setBackground(new Color(232, 244, 248));
        JLabel lblNom = new JLabel("Cliente: " + clienteActual);
        JLabel lblReg = new JLabel("Registro/Mesa: " + registroActual);
        lblNom.setFont(new Font("Arial", Font.BOLD, 14));
        lblReg.setFont(new Font("Arial", Font.BOLD, 14));
        panelInfoCliente.add(lblNom);
        panelInfoCliente.add(lblReg);
        panelCarrito.add(panelInfoCliente, BorderLayout.NORTH);

        // Lista de items
        modeloCarrito = new DefaultListModel<>();
        listaCarrito = new JList<>(modeloCarrito);
        listaCarrito.setFont(new Font("Monospaced", Font.PLAIN, 13));
        listaCarrito.setBackground(Color.WHITE);
        JScrollPane scroll = new JScrollPane(listaCarrito);
        panelCarrito.add(scroll, BorderLayout.CENTER);

        JPanel panelInf = new JPanel(new BorderLayout(10, 10));
        panelInf.setBackground(new Color(232, 244, 248));

        lblTotalCarrito = new JLabel("Total: $0.00", SwingConstants.RIGHT);
        lblTotalCarrito.setFont(new Font("Arial", Font.BOLD, 20));
        lblTotalCarrito.setForeground(new Color(231, 76, 60));

        JPanel botones = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));

        JButton btnSeguir = new JButton("← Seguir Comprando");
        btnSeguir.addActionListener(e -> cardLayout.show(mainPanel, "MENU"));

        JButton btnVaciar = new JButton("🗑️ Vaciar");
        btnVaciar.setBackground(new Color(231, 76, 60));
        btnVaciar.setForeground(Color.WHITE);
        btnVaciar.addActionListener(e -> {
            for (ItemCarrito item : carrito) {
                item.producto.stock += item.cantidad;
            }
            carrito.clear();
            actualizarCarrito();
        });

        JButton btnFacturar = new JButton("💳 Pagar y Generar Factura");
        btnFacturar.setBackground(new Color(52, 152, 219));
        btnFacturar.setForeground(Color.WHITE);
        btnFacturar.setFont(new Font("Arial", Font.BOLD, 14));
        btnFacturar.addActionListener(e -> generarFactura());

        botones.add(btnSeguir);
        botones.add(btnVaciar);
        botones.add(btnFacturar);

        panelInf.add(lblTotalCarrito, BorderLayout.NORTH);
        panelInf.add(botones, BorderLayout.SOUTH);

        panelCarrito.add(panelInf, BorderLayout.SOUTH);
        mainPanel.add(panelCarrito, "CARRITO");
    }

    void actualizarCarrito() {
        modeloCarrito.clear();
        double total = 0;

        // Actualizar info del cliente en el panel
        Component[] comps = panelCarrito.getComponents();
        for (Component c : comps) {
            if (c instanceof JPanel) {
                Component[] sub = ((JPanel) c).getComponents();
                for (Component s : sub) {
                    if (s instanceof JLabel && ((JLabel) s).getText().startsWith("Cliente:")) {
                        ((JLabel) s).setText("Cliente: " + clienteActual);
                    }
                    if (s instanceof JLabel && ((JLabel) s).getText().startsWith("Registro/Mesa:")) {
                        ((JLabel) s).setText("Registro/Mesa: " + registroActual);
                    }
                }
            }
        }

        for (ItemCarrito item : carrito) {
            String linea = String.format("%-25s x%-3d  $%7.2f  =  $%7.2f",
                    item.producto.nombre,
                    item.cantidad,
                    item.producto.precio,
                    item.subtotal);
            modeloCarrito.addElement(linea);
            total += item.subtotal;
        }

        lblTotalCarrito.setText(String.format("Subtotal: $%.2f  |  Impuestos: $%.2f  |  TOTAL: $%.2f",
                total, total * 0.16, total * 1.16));
    }

    // ==================== PANEL 3: FACTURA ====================
    void crearPanelFactura() {
        panelFactura = new JPanel(new BorderLayout(10, 10));
        panelFactura.setBorder(new EmptyBorder(15, 15, 15, 15));
        panelFactura.setBackground(new Color(255, 250, 240));

        JLabel titulo = new JLabel("🧾 FACTURA - ALERTA COCINA", SwingConstants.CENTER);
        titulo.setFont(new Font("Arial", Font.BOLD, 24));
        panelFactura.add(titulo, BorderLayout.NORTH);

        areaFactura = new JTextArea();
        areaFactura.setEditable(false);
        areaFactura.setFont(new Font("Monospaced", Font.PLAIN, 12));
        areaFactura.setBackground(Color.WHITE);
        JScrollPane scroll = new JScrollPane(areaFactura);
        panelFactura.add(scroll, BorderLayout.CENTER);

        JPanel botones = new JPanel(new GridLayout(2, 3, 10, 10));

        JButton btnDespacho = new JButton("🟡 ENVIAR A DESPACHO");
        btnDespacho.setBackground(new Color(241, 196, 15));
        btnDespacho.setFont(new Font("Arial", Font.BOLD, 12));
        btnDespacho.addActionListener(e -> enviarADespacho());

        JButton btnListoCocina = new JButton("🟢 LISTO PARA COCINA");
        btnListoCocina.setBackground(new Color(46, 204, 113));
        btnListoCocina.setForeground(Color.WHITE);
        btnListoCocina.setFont(new Font("Arial", Font.BOLD, 12));
        btnListoCocina.addActionListener(e -> enviarACocina());

        JButton btnCancelar = new JButton("🔴 CANCELAR PEDIDO");
        btnCancelar.setBackground(new Color(231, 76, 60));
        btnCancelar.setForeground(Color.WHITE);
        btnCancelar.addActionListener(e -> cancelarDesdeFactura());

        JButton btnGuardar = new JButton("💾 Guardar");
        btnGuardar.addActionListener(e -> guardarFactura());

        JButton btnSemaforo = new JButton("🚦 Ver Semáforo");
        btnSemaforo.setBackground(new Color(155, 89, 182));
        btnSemaforo.setForeground(Color.WHITE);
        btnSemaforo.addActionListener(e -> {
            actualizarSemaforo();
            cardLayout.show(mainPanel, "SEMAFORO");
        });

        JButton btnHistorial = new JButton("📊 Historial");
        btnHistorial.addActionListener(e -> {
            actualizarTablaHistorial();
            cardLayout.show(mainPanel, "HISTORIAL");
        });

        botones.add(btnDespacho);
        botones.add(btnListoCocina);
        botones.add(btnCancelar);
        botones.add(btnGuardar);
        botones.add(btnSemaforo);
        botones.add(btnHistorial);

        panelFactura.add(botones, BorderLayout.SOUTH);
        mainPanel.add(panelFactura, "FACTURA");
    }

    void generarFactura() {
        if (carrito.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Carrito vacío", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Crear factura
        facturaActual = new Factura(clienteActual, registroActual);
        StringBuilder itemsCocina = new StringBuilder();

        for (ItemCarrito item : carrito) {
            facturaActual.items.add(item);
            itemsCocina.append(item.producto.nombre).append(" x").append(item.cantidad).append(", ");
        }
        facturaActual.calcularTotales();
        facturaActual.estadoPago = "PAGADO";
        historialFacturas.add(facturaActual);

        // Crear pedido para cocina
        pedidoActual = new PedidoCocina(facturaActual.numero, clienteActual, registroActual,
                itemsCocina.toString(), facturaActual.total);
        pedidosCocina.add(pedidoActual);

        // Mostrar factura
        StringBuilder sb = new StringBuilder();
        sb.append("╔════════════════════════════════════════════════════════╗\n");
        sb.append("║     ☕ CAFETERÍA UNIVERSITARIA - FACTURA               ║\n");
        sb.append("╠════════════════════════════════════════════════════════╣\n");
        sb.append(String.format("║  Factura #: %-40s ║\n", facturaActual.numero));
        sb.append(String.format("║  Fecha: %-44s ║\n", facturaActual.fecha));
        sb.append(String.format("║  Hora: %-45s ║\n", facturaActual.hora));
        sb.append("╠════════════════════════════════════════════════════════╣\n");
        sb.append(String.format("║  CLIENTE: %-42s ║\n", facturaActual.cliente));
        sb.append(String.format("║  REGISTRO/MESA: %-36s ║\n", facturaActual.registro));
        sb.append("╠════════════════════════════════════════════════════════╣\n");
        sb.append("║  CANT  DESCRIPCIÓN              PRECIO     SUBTOTAL      ║\n");
        sb.append("╠════════════════════════════════════════════════════════╣\n");

        for (ItemCarrito item : facturaActual.items) {
            sb.append(String.format("║  %-4d %-25s $%6.2f   $%7.2f   ║\n",
                    item.cantidad,
                    item.producto.nombre.length() > 25 ?
                            item.producto.nombre.substring(0, 25) : item.producto.nombre,
                    item.producto.precio,
                    item.subtotal));
        }

        sb.append("╠════════════════════════════════════════════════════════╣\n");
        sb.append(String.format("║  Subtotal:                              $%10.2f      ║\n", facturaActual.subtotal));
        sb.append(String.format("║  Impuestos (16%%):                       $%10.2f      ║\n", facturaActual.impuestos));
        sb.append(String.format("║  TOTAL PAGADO:                          $%10.2f      ║\n", facturaActual.total));
        sb.append("╚════════════════════════════════════════════════════════╝\n");
        sb.append("\n  ✅ PAGO COMPLETADO\n");
        sb.append("  📍 Entregar en: " + registroActual + "\n");
        sb.append("  🔔 Presione 🟡 AMARILLO para enviar a despacho\n");

        areaFactura.setText(sb.toString());
        carrito.clear();

        cardLayout.show(mainPanel, "FACTURA");

        JOptionPane.showMessageDialog(this,
                "💰 PAGO EXITOSO\n\n" +
                        "Factura: " + facturaActual.numero + "\n" +
                        "Cliente: " + clienteActual + "\n" +
                        "Registro/Mesa: " + registroActual + "\n" +
                        "Total: $" + String.format("%.2f", facturaActual.total),
                "Pago Completado", JOptionPane.INFORMATION_MESSAGE);
    }

    void enviarADespacho() {
        if (pedidoActual == null) return;
        pedidoActual.estado = "EN_DESPACHO";

        JOptionPane.showMessageDialog(this,
                "🟡 PEDIDO EN DESPACHO\n\n" +
                        "Factura: " + pedidoActual.id + "\n" +
                        "Cliente: " + pedidoActual.cliente + "\n" +
                        "Registro/Mesa: " + pedidoActual.registro + "\n\n" +
                        "📦 El pedido está siendo preparado",
                "Despacho", JOptionPane.WARNING_MESSAGE);

        areaFactura.append("\n\n🟡 ESTADO: EN DESPACHO");
        actualizarSemaforo();
    }

    void enviarACocina() {
        if (pedidoActual == null) return;
        pedidoActual.estado = "LISTO_COCINA";

        JDialog alerta = new JDialog(this, "🚨 ALERTA COCINA - NUEVO PEDIDO", true);
        alerta.setSize(500, 400);
        alerta.setLocationRelativeTo(this);

        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));
        panel.setBackground(new Color(46, 204, 113));

        JLabel lblTitulo = new JLabel("🟢 PEDIDO LISTO PARA COCINA", SwingConstants.CENTER);
        lblTitulo.setFont(new Font("Arial", Font.BOLD, 22));
        lblTitulo.setForeground(Color.WHITE);

        JTextArea area = new JTextArea();
        area.setEditable(false);
        area.setFont(new Font("Monospaced", Font.BOLD, 14));
        area.setText(
                "\n  📋 FACTURA: " + pedidoActual.id +
                        "\n\n  👤 CLIENTE: " + pedidoActual.cliente +
                        "\n  📍 REGISTRO/MESA: " + pedidoActual.registro +
                        "\n\n  🍽️ ITEMS:\n  " + pedidoActual.items +
                        "\n\n  💰 TOTAL: $" + String.format("%.2f", pedidoActual.total) +
                        "\n\n  ⚡ PREPARAR INMEDIATAMENTE\n" +
                        "\n  📍 ENTREGAR EN: " + pedidoActual.registro + "\n"
        );

        JButton btnRecibido = new JButton("✅ RECIBIDO - INICIAR PREPARACIÓN");
        btnRecibido.setFont(new Font("Arial", Font.BOLD, 16));
        btnRecibido.addActionListener(e -> {
            alerta.dispose();
            pedidoActual.estado = "PREPARANDO";
            areaFactura.append("\n\n🟢 ESTADO: EN COCINA - PREPARANDO");
            actualizarSemaforo();
            cardLayout.show(mainPanel, "COCINA");
        });

        panel.add(lblTitulo, BorderLayout.NORTH);
        panel.add(new JScrollPane(area), BorderLayout.CENTER);
        panel.add(btnRecibido, BorderLayout.SOUTH);

        alerta.add(panel);
        alerta.setVisible(true);
    }

    void cancelarDesdeFactura() {
        if (pedidoActual == null) return;

        String motivo = JOptionPane.showInputDialog(this, "Motivo de cancelación:");
        if (motivo == null) return;

        pedidoActual.estado = "CANCELADO";
        facturaActual.estadoPedido = "CANCELADO";

        // Devolver stock
        for (ItemCarrito i : facturaActual.items) {
            i.producto.stock += i.cantidad;
        }

        JOptionPane.showMessageDialog(this,
                "❌ PEDIDO CANCELADO\n\n" +
                        "Factura: " + pedidoActual.id + "\n" +
                        "Motivo: " + motivo + "\n" +
                        "Reembolso: $" + String.format("%.2f", pedidoActual.total),
                "Cancelación", JOptionPane.ERROR_MESSAGE);

        areaFactura.append("\n\n🔴 CANCELADO - MOTIVO: " + motivo);
        actualizarSemaforo();
    }

    void guardarFactura() {
        try {
            String nombre = "Factura_" + pedidoActual.id + ".txt";
            FileWriter w = new FileWriter(nombre);
            w.write(areaFactura.getText());
            w.close();
            JOptionPane.showMessageDialog(this, "Guardado: " + nombre);
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
        }
    }

    // ==================== PANEL 4: COCINA ====================
    void crearPanelCocina() {
        panelCocina = new JPanel(new BorderLayout(10, 10));
        panelCocina.setBorder(new EmptyBorder(15, 15, 15, 15));
        panelCocina.setBackground(new Color(254, 249, 231));

        JLabel titulo = new JLabel("👨‍🍳 COCINA - PREPARACIÓN", SwingConstants.CENTER);
        titulo.setFont(new Font("Arial", Font.BOLD, 24));
        panelCocina.add(titulo, BorderLayout.NORTH);

        // Info del pedido actual
        JPanel panelInfo = new JPanel(new GridLayout(3, 1));
        panelInfo.setBackground(new Color(254, 249, 231));
        panelInfo.setBorder(BorderFactory.createTitledBorder("PEDIDO ACTUAL"));

        JLabel lblPedido = new JLabel("Factura: --");
        JLabel lblCliente = new JLabel("Cliente: --");
        JLabel lblMesa = new JLabel("Registro/Mesa: --");

        lblPedido.setFont(new Font("Arial", Font.BOLD, 14));
        lblCliente.setFont(new Font("Arial", Font.BOLD, 14));
        lblMesa.setFont(new Font("Arial", Font.BOLD, 16));
        lblMesa.setForeground(new Color(231, 76, 60));

        panelInfo.add(lblPedido);
        panelInfo.add(lblCliente);
        panelInfo.add(lblMesa);

        // Actualizar labels cuando se muestre
        panelCocina.addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentShown(java.awt.event.ComponentEvent e) {
                if (pedidoActual != null) {
                    lblPedido.setText("Factura: " + pedidoActual.id);
                    lblCliente.setText("Cliente: " + pedidoActual.cliente);
                    lblMesa.setText("📍 ENTREGAR EN: " + pedidoActual.registro);
                }
            }
        });

        JPanel panelCentro = new JPanel(new BorderLayout());
        panelCentro.add(panelInfo, BorderLayout.NORTH);

        modeloCocina = new DefaultListModel<>();
        listaCocina = new JList<>(modeloCocina);
        listaCocina.setFont(new Font("Monospaced", Font.PLAIN, 14));
        JScrollPane scroll = new JScrollPane(listaCocina);
        panelCentro.add(scroll, BorderLayout.CENTER);

        panelCocina.add(panelCentro, BorderLayout.CENTER);

        JPanel botones = new JPanel(new GridLayout(1, 4, 10, 10));

        JButton btnIniciar = new JButton("▶️ INICIAR");
        btnIniciar.setBackground(new Color(52, 152, 219));
        btnIniciar.setForeground(Color.WHITE);
        btnIniciar.addActionListener(e -> {
            modeloCocina.addElement("[" + pedidoActual.hora + "] INICIANDO PREPARACIÓN...");
        });

        JButton btnListo = new JButton("✅ PEDIDO LISTO");
        btnListo.setBackground(new Color(46, 204, 113));
        btnListo.setForeground(Color.WHITE);
        btnListo.setFont(new Font("Arial", Font.BOLD, 14));
        btnListo.addActionListener(e -> {
            pedidoActual.estado = "LISTO_ENTREGA";
            modeloCocina.addElement("[" + new SimpleDateFormat("HH:mm:ss").format(new Date()) + "] ✅ LISTO");

            JOptionPane.showMessageDialog(this,
                    "📱 NOTIFICACIÓN ENVIADA AL CLIENTE\n\n" +
                            pedidoActual.cliente + ", su pedido está listo\n" +
                            "📍 Recoger en: " + pedidoActual.registro,
                    "Notificación", JOptionPane.INFORMATION_MESSAGE);

            actualizarSemaforo();
            cardLayout.show(mainPanel, "SEMAFORO");
        });

        JButton btnCancelar = new JButton("❌ CANCELAR");
        btnCancelar.setBackground(new Color(231, 76, 60));
        btnCancelar.setForeground(Color.WHITE);
        btnCancelar.addActionListener(e -> cancelarDesdeCocina());

        JButton btnSemaforo = new JButton("🚦 Semáforo");
        btnSemaforo.addActionListener(e -> cardLayout.show(mainPanel, "SEMAFORO"));

        botones.add(btnIniciar);
        botones.add(btnListo);
        botones.add(btnCancelar);
        botones.add(btnSemaforo);

        panelCocina.add(botones, BorderLayout.SOUTH);
        mainPanel.add(panelCocina, "COCINA");
    }

    void cancelarDesdeCocina() {
        String motivo = JOptionPane.showInputDialog(this, "Motivo de cancelación en cocina:");
        if (motivo == null) return;

        pedidoActual.estado = "CANCELADO";
        facturaActual.estadoPedido = "CANCELADO";

        for (ItemCarrito i : facturaActual.items) {
            i.producto.stock += i.cantidad;
        }

        JOptionPane.showMessageDialog(this,
                "❌ CANCELADO EN COCINA\nMotivo: " + motivo,
                "Cancelación", JOptionPane.ERROR_MESSAGE);

        modeloCocina.addElement("❌ CANCELADO: " + motivo);
        actualizarSemaforo();
    }

    // ==================== PANEL 5: SEMÁFORO ====================
    void crearPanelSemaforo() {
        panelSemaforo = new JPanel(new BorderLayout(10, 10));
        panelSemaforo.setBorder(new EmptyBorder(20, 20, 20, 20));
        panelSemaforo.setBackground(new Color(240, 240, 240));

        JLabel titulo = new JLabel("🚦 PANEL DE CONTROL - SEMÁFORO", SwingConstants.CENTER);
        titulo.setFont(new Font("Arial", Font.BOLD, 28));
        panelSemaforo.add(titulo, BorderLayout.NORTH);

        // Info del pedido
        JPanel panelInfo = new JPanel(new GridLayout(4, 1));
        panelInfo.setBackground(new Color(240, 240, 240));
        panelInfo.setBorder(BorderFactory.createTitledBorder("INFORMACIÓN DEL PEDIDO"));

        JLabel lblFact = new JLabel("Factura: --");
        JLabel lblCli = new JLabel("Cliente: --");
        JLabel lblReg = new JLabel("Registro/Mesa: --");
        JLabel lblHor = new JLabel("Hora: --");

        panelInfo.add(lblFact);
        panelInfo.add(lblCli);
        panelInfo.add(lblReg);
        panelInfo.add(lblHor);

        // Actualizar al mostrar
        panelSemaforo.addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentShown(java.awt.event.ComponentEvent e) {
                if (pedidoActual != null) {
                    lblFact.setText("Factura: " + pedidoActual.id);
                    lblCli.setText("Cliente: " + pedidoActual.cliente);
                    lblReg.setText("Registro/Mesa: " + pedidoActual.registro);
                    lblHor.setText("Hora pedido: " + pedidoActual.hora);
                }
            }
        });

        JPanel panelCentro = new JPanel(new BorderLayout());
        panelCentro.add(panelInfo, BorderLayout.NORTH);

        // Panel de semáforos
        JPanel panelSemaforos = new JPanel(new GridLayout(1, 2, 20, 20));

        // Semáforo Usuario
        JPanel semUsuario = new JPanel(new BorderLayout());
        semUsuario.setBorder(BorderFactory.createTitledBorder("PARA EL CLIENTE"));
        semUsuario.setBackground(Color.WHITE);

        btnSemaforoUsuario = new JButton("⚪ ESPERANDO");
        btnSemaforoUsuario.setFont(new Font("Arial", Font.BOLD, 18));
        btnSemaforoUsuario.setBackground(Color.LIGHT_GRAY);

        lblEstadoUsuario = new JLabel("Sin pedidos activos", SwingConstants.CENTER);

        semUsuario.add(btnSemaforoUsuario, BorderLayout.CENTER);
        semUsuario.add(lblEstadoUsuario, BorderLayout.SOUTH);

        // Semáforo Cocina
        JPanel semCocina = new JPanel(new BorderLayout());
        semCocina.setBorder(BorderFactory.createTitledBorder("PARA COCINA"));
        semCocina.setBackground(Color.WHITE);

        btnSemaforoCocina = new JButton("⚪ SIN PEDIDOS");
        btnSemaforoCocina.setFont(new Font("Arial", Font.BOLD, 18));
        btnSemaforoCocina.setBackground(Color.LIGHT_GRAY);

        lblEstadoCocina = new JLabel("Esperando", SwingConstants.CENTER);

        semCocina.add(btnSemaforoCocina, BorderLayout.CENTER);
        semCocina.add(lblEstadoCocina, BorderLayout.SOUTH);

        panelSemaforos.add(semUsuario);
        panelSemaforos.add(semCocina);

        panelCentro.add(panelSemaforos, BorderLayout.CENTER);
        panelSemaforo.add(panelCentro, BorderLayout.CENTER);

        // Panel inferior
        JPanel panelSur = new JPanel(new BorderLayout());

        JPanel panelLeyenda = new JPanel(new GridLayout(4, 1));
        panelLeyenda.setBackground(new Color(240, 240, 240));
        panelLeyenda.add(new JLabel("🟡 AMARILLO = En despacho / En camino", SwingConstants.CENTER));
        panelLeyenda.add(new JLabel("🟢 VERDE = Listo para recoger / Preparando", SwingConstants.CENTER));
        panelLeyenda.add(new JLabel("🔴 ROJO = Cancelado", SwingConstants.CENTER));
        panelLeyenda.add(new JLabel("⚪ GRIS = Esperando", SwingConstants.CENTER));

        JPanel panelBotones = new JPanel(new FlowLayout());

        JButton btnNuevo = new JButton("🆕 NUEVO PEDIDO");
        btnNuevo.setBackground(new Color(52, 152, 219));
        btnNuevo.setForeground(Color.WHITE);
        btnNuevo.setFont(new Font("Arial", Font.BOLD, 14));
        btnNuevo.addActionListener(e -> {
            clienteActual = "";
            registroActual = "";
            pedidoActual = null;
            txtCliente.setText("");
            txtRegistro.setText("");
            cardLayout.show(mainPanel, "MENU");
        });

        JButton btnHistorial = new JButton("📊 HISTORIAL DEL DÍA");
        btnHistorial.setBackground(new Color(155, 89, 182));
        btnHistorial.setForeground(Color.WHITE);
        btnHistorial.addActionListener(e -> {
            actualizarTablaHistorial();
            cardLayout.show(mainPanel, "HISTORIAL");
        });

        panelBotones.add(btnNuevo);
        panelBotones.add(btnHistorial);

        panelSur.add(panelLeyenda, BorderLayout.CENTER);
        panelSur.add(panelBotones, BorderLayout.SOUTH);

        panelSemaforo.add(panelSur, BorderLayout.SOUTH);
        mainPanel.add(panelSemaforo, "SEMAFORO");
    }

    void actualizarSemaforo() {
        if (pedidoActual == null) return;

        switch (pedidoActual.estado) {
            case "NUEVO":
            case "PENDIENTE":
                btnSemaforoUsuario.setText("⚪ ESPERANDO PAGO");
                btnSemaforoUsuario.setBackground(Color.LIGHT_GRAY);
                lblEstadoUsuario.setText("Complete el pago");
                btnSemaforoCocina.setText("⚪ SIN PEDIDOS");
                btnSemaforoCocina.setBackground(Color.LIGHT_GRAY);
                break;
            case "EN_DESPACHO":
                btnSemaforoUsuario.setText("🟡 EN DESPACHO");
                btnSemaforoUsuario.setBackground(new Color(241, 196, 15));
                lblEstadoUsuario.setText("Su pedido está siendo preparado");
                btnSemaforoCocina.setText("🟡 EN DESPACHO");
                btnSemaforoCocina.setBackground(new Color(241, 196, 15));
                lblEstadoCocina.setText("Pedido en camino");
                break;
            case "LISTO_COCINA":
            case "PREPARANDO":
                btnSemaforoUsuario.setText("🟢 EN PREPARACIÓN");
                btnSemaforoUsuario.setBackground(new Color(46, 204, 113));
                btnSemaforoUsuario.setForeground(Color.WHITE);
                lblEstadoUsuario.setText("Cocina preparando su orden");
                btnSemaforoCocina.setText("🟢 LISTO - PREPARAR");
                btnSemaforoCocina.setBackground(new Color(46, 204, 113));
                btnSemaforoCocina.setForeground(Color.WHITE);
                lblEstadoCocina.setText("Nuevo pedido pendiente");
                break;
            case "LISTO_ENTREGA":
                btnSemaforoUsuario.setText("🟢 LISTO PARA RECOGER");
                btnSemaforoUsuario.setBackground(new Color(46, 204, 113));
                lblEstadoUsuario.setText("📍 " + pedidoActual.registro);
                btnSemaforoCocina.setText("✅ ENTREGADO");
                btnSemaforoCocina.setBackground(new Color(149, 165, 166));
                lblEstadoCocina.setText("Completado");
                break;
            case "CANCELADO":
                btnSemaforoUsuario.setText("🔴 CANCELADO");
                btnSemaforoUsuario.setBackground(new Color(231, 76, 60));
                btnSemaforoUsuario.setForeground(Color.WHITE);
                lblEstadoUsuario.setText("Reembolso procesado");
                btnSemaforoCocina.setText("🔴 CANCELADO");
                btnSemaforoCocina.setBackground(new Color(231, 76, 60));
                btnSemaforoCocina.setForeground(Color.WHITE);
                lblEstadoCocina.setText("No preparar");
                break;
        }
    }

    // ==================== PANEL 6: HISTORIAL DEL DÍA ====================
    void crearPanelHistorial() {
        panelHistorial = new JPanel(new BorderLayout(10, 10));
        panelHistorial.setBorder(new EmptyBorder(15, 15, 15, 15));
        panelHistorial.setBackground(new Color(245, 238, 248));

        JLabel titulo = new JLabel("📊 HISTORIAL DE VENTAS DEL DÍA", SwingConstants.CENTER);
        titulo.setFont(new Font("Arial", Font.BOLD, 24));
        panelHistorial.add(titulo, BorderLayout.NORTH);

        // Resumen superior
        JPanel panelResumen = new JPanel(new GridLayout(1, 4, 10, 10));
        panelResumen.setBorder(BorderFactory.createTitledBorder("RESUMEN DEL DÍA"));

        JLabel lblTotalVentas = new JLabel("Ventas: $0", SwingConstants.CENTER);
        JLabel lblTotalPedidos = new JLabel("Pedidos: 0", SwingConstants.CENTER);
        JLabel lblCompletados = new JLabel("Completados: 0", SwingConstants.CENTER);
        JLabel lblCancelados = new JLabel("Cancelados: 0", SwingConstants.CENTER);

        lblTotalVentas.setFont(new Font("Arial", Font.BOLD, 16));
        lblTotalVentas.setForeground(new Color(46, 204, 113));
        lblTotalPedidos.setFont(new Font("Arial", Font.BOLD, 16));
        lblCompletados.setFont(new Font("Arial", Font.BOLD, 14));
        lblCompletados.setForeground(new Color(52, 152, 219));
        lblCancelados.setFont(new Font("Arial", Font.BOLD, 14));
        lblCancelados.setForeground(new Color(231, 76, 60));

        panelResumen.add(lblTotalVentas);
        panelResumen.add(lblTotalPedidos);
        panelResumen.add(lblCompletados);
        panelResumen.add(lblCancelados);

        // Actualizar al mostrar
        panelHistorial.addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentShown(java.awt.event.ComponentEvent e) {
                double totalVentas = 0;
                int completados = 0;
                int cancelados = 0;

                for (Factura f : historialFacturas) {
                    if (f.estadoPedido.equals("CANCELADO")) {
                        cancelados++;
                    } else {
                        totalVentas += f.total;
                        completados++;
                    }
                }

                lblTotalVentas.setText(String.format("Ventas: $%.2f", totalVentas));
                lblTotalPedidos.setText("Pedidos: " + historialFacturas.size());
                lblCompletados.setText("Completados: " + completados);
                lblCancelados.setText("Cancelados: " + cancelados);
            }
        });

        JPanel panelNorte = new JPanel(new BorderLayout());
        panelNorte.add(panelResumen, BorderLayout.NORTH);
        panelHistorial.add(panelNorte, BorderLayout.NORTH);

        // Tabla de historial
        String[] columnas = {"Hora", "Factura", "Cliente", "Registro/Mesa", "Total", "Estado"};
        DefaultTableModel modeloTabla = new DefaultTableModel(columnas, 0);
        JTable tabla = new JTable(modeloTabla);
        tabla.setFont(new Font("Arial", Font.PLAIN, 12));
        tabla.setRowHeight(25);

        // Guardar referencia para actualizar
        tabla.setName("tablaHistorial");

        JScrollPane scroll = new JScrollPane(tabla);
        panelHistorial.add(scroll, BorderLayout.CENTER);

        // Botones
        JPanel panelBotones = new JPanel(new FlowLayout());

        JButton btnActualizar = new JButton("🔄 Actualizar");
        btnActualizar.addActionListener(e -> actualizarTablaHistorial());

        JButton btnDetalle = new JButton("📋 Ver Detalle");
        btnDetalle.addActionListener(e -> {
            int row = tabla.getSelectedRow();
            if (row >= 0) {
                String factura = (String) tabla.getValueAt(row, 1);
                mostrarDetalleFactura(factura);
            }
        });

        JButton btnVolver = new JButton("← Volver al Menú");
        btnVolver.setBackground(new Color(52, 152, 219));
        btnVolver.setForeground(Color.WHITE);
        btnVolver.addActionListener(e -> cardLayout.show(mainPanel, "MENU"));

        panelBotones.add(btnActualizar);
        panelBotones.add(btnDetalle);
        panelBotones.add(btnVolver);

        panelHistorial.add(panelBotones, BorderLayout.SOUTH);
        mainPanel.add(panelHistorial, "HISTORIAL");
    }

    void actualizarTablaHistorial() {
        // Encontrar la tabla en el panel
        Component[] comps = panelHistorial.getComponents();
        for (Component c : comps) {
            if (c instanceof JScrollPane) {
                JTable tabla = (JTable) ((JScrollPane) c).getViewport().getView();
                DefaultTableModel modelo = (DefaultTableModel) tabla.getModel();
                modelo.setRowCount(0);

                for (Factura f : historialFacturas) {
                    modelo.addRow(new Object[]{
                            f.hora,
                            f.numero,
                            f.cliente,
                            f.registro,
                            String.format("$%.2f", f.total),
                            f.estadoPedido
                    });
                }
            }
        }
    }

    void mostrarDetalleFactura(String numFactura) {
        for (Factura f : historialFacturas) {
            if (f.numero.equals(numFactura)) {
                StringBuilder sb = new StringBuilder();
                sb.append("FACTURA: ").append(f.numero).append("\n");
                sb.append("Fecha: ").append(f.fecha).append(" ").append(f.hora).append("\n");
                sb.append("Cliente: ").append(f.cliente).append("\n");
                sb.append("Registro/Mesa: ").append(f.registro).append("\n\n");
                sb.append("ITEMS:\n");
                for (ItemCarrito i : f.items) {
                    sb.append("- ").append(i.producto.nombre)
                            .append(" x").append(i.cantidad)
                            .append(" = $").append(i.subtotal).append("\n");
                }
                sb.append("\nTotal: $").append(f.total);
                sb.append("\nEstado: ").append(f.estadoPedido);

                JTextArea area = new JTextArea(sb.toString());
                area.setEditable(false);
                area.setFont(new Font("Monospaced", Font.PLAIN, 12));

                JOptionPane.showMessageDialog(this, new JScrollPane(area),
                        "Detalle Factura", JOptionPane.INFORMATION_MESSAGE);
                return;
            }
        }
    }

    // ==================== MAIN ====================
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new CafeteriaFinal());
    }

}
