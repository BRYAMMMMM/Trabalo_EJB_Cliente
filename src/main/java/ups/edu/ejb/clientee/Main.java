package ups.edu.ejb.clientee;

import java.util.Hashtable;
import java.util.List;

import javax.naming.Context;
import javax.naming.InitialContext;

import ups.edu.ejb.model.Producto;
import ups.edu.ejb.service.ProductoRegistrationRemote;

public class Main {
    private ProductoRegistrationRemote productoRegistration;

    public void initialize() throws Exception {
        // Configuración del JNDI
        Hashtable<String, Object> jndiProps = new Hashtable<>();
        jndiProps.put(Context.INITIAL_CONTEXT_FACTORY, "org.wildfly.naming.client.WildFlyInitialContextFactory");
        jndiProps.put(Context.PROVIDER_URL, "http-remoting://localhost:8080");

        try {
            // Buscar el EJB remoto en el servidor
            Context context = new InitialContext(jndiProps);
            productoRegistration = (ProductoRegistrationRemote) context.lookup(
                "ejb:/server/ProductoRegistration!ups.edu.ejb.service.ProductoRegistrationRemote"
            );
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Error en la búsqueda JNDI.");
            throw e;
        }
    }

    public void registerProducto(String nombre, String descripcion, Double precio, Integer cantidad) throws Exception {
        // Crear un nuevo producto
        Producto producto = new Producto();
        producto.setNombre(nombre);
        producto.setDescripcion(descripcion);
        producto.setPrecio(precio);
        producto.setCantidadDisponible(cantidad);

        // Registrar el producto a través del EJB remoto
        productoRegistration.register(producto);
        System.out.println("Producto registrado: " + nombre);
    }

    public List<Producto> listarProductos() throws Exception {
        // Llama al método remoto para listar productos
        return productoRegistration.listarProductos();
    }

    public static void main(String[] args) {
        try {
            Main client = new Main();
            client.initialize();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}