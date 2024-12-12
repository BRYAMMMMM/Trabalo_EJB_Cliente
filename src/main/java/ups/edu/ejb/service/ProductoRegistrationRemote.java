package ups.edu.ejb.service;

import java.util.List;

import jakarta.ejb.Remote;
import ups.edu.ejb.model.Producto;

@Remote
public interface ProductoRegistrationRemote {
    void register(Producto producto);
    List<Producto> listarProductos(); // Declara el método aquí

}