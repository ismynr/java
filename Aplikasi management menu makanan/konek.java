
package terserah;


import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 *
 * @author ISMYNR
 */
public class konek {
    public static Connection sambung(){
        String DRIVER = "com.mysql.jdbc.Driver";
        String url = "jdbc:mysql://localhost:3306/db_makanan";
        Connection kon = null;
        try {
            Class.forName(DRIVER).newInstance();
            kon = DriverManager.getConnection(url, "root", "");
            System.out.println("sukses");
            return kon;
        } catch (ClassNotFoundException | IllegalAccessException |
                InstantiationException | SQLException e) {
            System.err.println("eror" + e.getMessage());
        }
        return null;
    }
}
