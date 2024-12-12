package ups.edu.ejb.view;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.List;

import ups.edu.ejb.clientee.Main;
import ups.edu.ejb.model.Producto;

public class ProductView extends JFrame {
    private JTextField txtNombre;
    private JTextField txtDescripcion;
    private JTextField txtPrecio;
    private JTextField txtCantidad;
    private JButton btnRegistrar;
    private JButton btnListar;

    private Main client;

    // Panel principal donde se mostrarán las tarjetas de productos
    private JPanel cardPanel;
    private JScrollPane scrollPane;

    public ProductView() {
        setTitle("Sistema de Gestión de Productos");
        setSize(1100, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Look and Feel Nimbus (si está disponible)
        try {
            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (Exception e) {
            // Ignorar si no se encuentra Nimbus
        }

        // Inicializar el cliente EJB
        try {
            client = new Main();
            client.initialize();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                    "<html><body><p style='width:200px;'>No se pudo establecer conexión con el servidor. Verifique la configuración.</p></body></html>",
                    "Error de Conexión", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }

        // Fondo gris muy claro
        getContentPane().setBackground(new Color(250, 250, 250));
        getContentPane().setLayout(new BorderLayout());

        // Encabezado: degradado pastel (rosa a violeta suave)
        JPanel headerPanel = new GradientPanel(new Color(255, 183, 197), new Color(215, 185, 255));
        headerPanel.setPreferredSize(new Dimension(getWidth(), 100));
        headerPanel.setLayout(new GridBagLayout());

        GridBagConstraints gbc = new GridBagConstraints();

        JLabel headerLabel = new JLabel("Sistema de Gestión de Productos");
        headerLabel.setForeground(Color.WHITE);
        headerLabel.setFont(new Font("SansSerif", Font.BOLD, 36));
        gbc.gridx = 0;
        gbc.gridy = 0;
        headerPanel.add(headerLabel, gbc);

        JLabel subHeaderLabel = new JLabel("Administre su inventario de manera sencilla y rápida");
        subHeaderLabel.setForeground(new Color(255, 255, 255, 200));
        subHeaderLabel.setFont(new Font("SansSerif", Font.PLAIN, 16));
        gbc.gridy = 1;
        gbc.insets = new Insets(5,0,0,0);
        headerPanel.add(subHeaderLabel, gbc);

        getContentPane().add(headerPanel, BorderLayout.NORTH);

        // Panel lateral (formulario)
        JPanel sidePanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                // Fondo blanco con bordes redondeados
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setColor(Color.WHITE);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);
                g2.dispose();
            }
        };
        sidePanel.setLayout(new BoxLayout(sidePanel, BoxLayout.Y_AXIS));
        sidePanel.setBorder(BorderFactory.createEmptyBorder(20,20,20,20));
        sidePanel.setOpaque(false);

        // Texto introductorio en el lateral
        JLabel welcomeLabel = new JLabel("<html><body style='width:250px;'><h2>¡Bienvenido!</h2>"
                + "<p>Aquí puedes registrar nuevos productos y gestionar su información. "
                + "Completa los datos a continuación y presiona <b>Registrar</b> para añadir un producto a tu inventario. "
                + "Luego, haz clic en <b>Listar</b> para ver el catálogo completo.</p></body></html>");
        welcomeLabel.setFont(new Font("SansSerif", Font.PLAIN, 14));
        welcomeLabel.setForeground(new Color(60,60,60));
        welcomeLabel.setBorder(BorderFactory.createEmptyBorder(0,0,15,0));
        sidePanel.add(welcomeLabel);

        sidePanel.add(createFormRow("Nombre del producto:", txtNombre = new JTextField()));
        sidePanel.add(Box.createVerticalStrut(10));
        sidePanel.add(createFormRow("Descripción:", txtDescripcion = new JTextField()));
        sidePanel.add(Box.createVerticalStrut(10));
        sidePanel.add(createFormRow("Precio:", txtPrecio = new JTextField()));
        sidePanel.add(Box.createVerticalStrut(10));
        sidePanel.add(createFormRow("Cantidad:", txtCantidad = new JTextField()));
        sidePanel.add(Box.createVerticalStrut(20));

        // Botones Registrar y Listar
        JPanel formButtonPanel = new JPanel(new GridLayout(1, 2, 15, 0));
        formButtonPanel.setOpaque(false);

        btnRegistrar = createStyledButton("Registrar", new Color(46, 204, 113));
        btnListar = createStyledButton("Listar", new Color(52, 152, 219));

        formButtonPanel.add(btnRegistrar);
        formButtonPanel.add(btnListar);

        JPanel bottomFormPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        bottomFormPanel.setOpaque(false);
        bottomFormPanel.add(formButtonPanel);
        sidePanel.add(bottomFormPanel);

        getContentPane().add(sidePanel, BorderLayout.WEST);

        // Panel central con las tarjetas de productos
        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.setBackground(new Color(250,250,250));

        // Panel vertical para tarjetas
        cardPanel = new JPanel();
        cardPanel.setLayout(new BoxLayout(cardPanel, BoxLayout.Y_AXIS));
        cardPanel.setBackground(new Color(250,250,250));
        cardPanel.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));

        scrollPane = new JScrollPane(cardPanel,
            JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
            JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(180,180,180), 1, true),
                "Catálogo de Productos",
                0,0,
                new Font("SansSerif", Font.BOLD, 16),
                new Color(60,60,60)));

        JLabel infoLabel = new JLabel("<html><body style='text-align:center;'>"
                + "<p>Presiona <b>Listar</b> para ver los productos registrados en el catálogo.</p>"
                + "</body></html>", SwingConstants.CENTER);
        infoLabel.setFont(new Font("SansSerif", Font.PLAIN, 14));
        infoLabel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        centerPanel.add(scrollPane, BorderLayout.CENTER);
        centerPanel.add(infoLabel, BorderLayout.SOUTH);

        getContentPane().add(centerPanel, BorderLayout.CENTER);

        // Eventos de los botones
        btnRegistrar.addActionListener(e -> registrarProducto());
        btnListar.addActionListener(e -> listarProductos());

        // Tooltips
        txtNombre.setToolTipText("Ingrese el nombre del producto (Ej: 'Cámara Reflex').");
        txtDescripcion.setToolTipText("Ingrese una breve descripción (Ej: 'Cámara 24MP con lente intercambiable').");
        txtPrecio.setToolTipText("Ingrese el precio en formato decimal (Ej: 549.99).");
        txtCantidad.setToolTipText("Ingrese la cantidad disponible (Ej: 10).");

        setVisible(true);
    }

    private JPanel createFormRow(String labelText, JTextField textField) {
        JPanel row = new JPanel(new BorderLayout(10, 0));
        row.setOpaque(false);

        JLabel label = new JLabel(labelText);
        label.setFont(new Font("SansSerif", Font.BOLD, 14));
        label.setForeground(new Color(50,50,50));
        label.setPreferredSize(new Dimension(150, 30));

        textField.setFont(new Font("SansSerif", Font.PLAIN, 14));
        textField.setPreferredSize(new Dimension(200, 30));
        textField.setBackground(Color.WHITE);
        textField.setBorder(BorderFactory.createLineBorder(new Color(200,200,200)));

        row.add(label, BorderLayout.WEST);
        row.add(textField, BorderLayout.CENTER);

        return row;
    }

    private JButton createStyledButton(String text, Color backgroundColor) {
        JButton button = new JButton(text);
        button.setBackground(backgroundColor);
        button.setForeground(Color.WHITE);
        button.setFont(new Font("SansSerif", Font.BOLD, 14));
        button.setFocusPainted(false);
        button.setPreferredSize(new Dimension(120, 35));
        button.setBorderPainted(false);
        button.setOpaque(true);
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        Color hoverColor = backgroundColor.brighter();
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                button.setBackground(hoverColor);
            }
            @Override
            public void mouseExited(MouseEvent e) {
                button.setBackground(backgroundColor);
            }
        });
        return button;
    }

    private void registrarProducto() {
        try {
            String nombre = txtNombre.getText().trim();
            String descripcion = txtDescripcion.getText().trim();
            Double precio = Double.parseDouble(txtPrecio.getText().trim());
            Integer cantidad = Integer.parseInt(txtCantidad.getText().trim());

            client.registerProducto(nombre, descripcion, precio, cantidad);

            JOptionPane.showMessageDialog(this,
                "<html><body><p style='width:200px;'>¡El producto <b>" + nombre + "</b> ha sido registrado exitosamente!<br>"
                + "Ahora presiona <b>Listar</b> para verlo en el catálogo.</p></body></html>",
                "Registro Exitoso", JOptionPane.INFORMATION_MESSAGE);

            // Limpiar campos
            txtNombre.setText("");
            txtDescripcion.setText("");
            txtPrecio.setText("");
            txtCantidad.setText("");
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                "<html><body><p style='width:200px;'>Ocurrió un error al registrar el producto. "
                + "Verifique los datos e inténtelo nuevamente.</p>"
                + "<p>Detalle: " + ex.getMessage() + "</p></body></html>",
                "Error", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }

    private void listarProductos() {
        try {
            List<Producto> productos = client.listarProductos();
            mostrarProductosComoTarjetas(productos);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                "<html><body><p style='width:200px;'>No se pudieron listar los productos."
                + " Revise la conexión con el servidor.</p>"
                + "<p>Detalle: " + ex.getMessage() + "</p></body></html>",
                "Error", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }

    private void mostrarProductosComoTarjetas(List<Producto> productos) {
        cardPanel.removeAll();

        for (Producto p : productos) {
            JPanel productCard = new JPanel(new BorderLayout(10,10));
            productCard.setBorder(
                BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(new Color(200,200,200), 1, true),
                    BorderFactory.createEmptyBorder(10,10,10,10)
                )
            );
            productCard.setBackground(Color.WHITE);

            JLabel lblNombre = new JLabel(p.getNombre());
            lblNombre.setFont(new Font("SansSerif", Font.BOLD, 18));
            lblNombre.setForeground(new Color(60,60,60));

            JLabel lblDesc = new JLabel("<html><body style='width:200px;'>" + p.getDescripcion() + "</body></html>");
            lblDesc.setFont(new Font("SansSerif", Font.PLAIN, 14));
            lblDesc.setForeground(new Color(80,80,80));

            JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10,0));
            bottomPanel.setOpaque(false);

            JLabel lblPrecio = new JLabel("Precio: $" + p.getPrecio());
            lblPrecio.setFont(new Font("SansSerif", Font.BOLD, 14));
            lblPrecio.setForeground(new Color(34, 153, 84));

            JLabel lblCantidad = new JLabel("Stock: " + p.getCantidadDisponible());
            lblCantidad.setFont(new Font("SansSerif", Font.PLAIN, 14));
            lblCantidad.setForeground(new Color(52, 73, 94));

            bottomPanel.add(lblPrecio);
            bottomPanel.add(lblCantidad);

            productCard.add(lblNombre, BorderLayout.NORTH);
            productCard.add(lblDesc, BorderLayout.CENTER);
            productCard.add(bottomPanel, BorderLayout.SOUTH);

            cardPanel.add(productCard);
            cardPanel.add(Box.createVerticalStrut(10));
        }

        cardPanel.revalidate();
        cardPanel.repaint();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(ProductView::new);
    }
}

/**
 * Panel con gradiente vertical suave.
 */
class GradientPanel extends JPanel {
    private final Color startColor;
    private final Color endColor;

    public GradientPanel(Color startColor, Color endColor) {
        super();
        this.startColor = startColor;
        this.endColor = endColor;
        setOpaque(false);
    }

    @Override
    protected void paintComponent(Graphics g) {
        int w = getWidth();
        int h = getHeight();
        Graphics2D g2d = (Graphics2D)g.create();
        GradientPaint gp = new GradientPaint(0,0,startColor,0,h,endColor);
        g2d.setPaint(gp);
        g2d.fillRect(0,0,w,h);
        g2d.dispose();
        super.paintComponent(g);
    }
}
