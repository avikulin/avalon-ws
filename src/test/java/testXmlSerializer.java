import DAL.Contracts.CrudRepository;
import DAL.DataEntities.Registers.Equipment;
import DAL.Repositories.EquipmentRepo;
import Utils.XMLTransformer.XTransformer;
import Utils.XMLTransformer.XTransformerImpl;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import java.io.File;
import java.io.IOException;

public class testXmlSerializer {
    private static EntityManagerFactory emf;
    private static EntityManager em;

    public static final File fileXml = new File("equipment_units_data.xml");
    static{
        try {
            if(!fileXml.exists()) fileXml.createNewFile();
        } catch (IOException ex) {
            System.out.println(ex.getMessage());;
        }
    }

    public static void main(String[] args) {
        emf = Persistence.createEntityManagerFactory("lab-db");
        em = emf.createEntityManager();

        CrudRepository<Equipment, Long> repository = new EquipmentRepo();
        repository.registerDataSource(em);

        XTransformer<Equipment, Long> transformer = new XTransformerImpl<>();
        transformer.setSource(repository);
        transformer.writeXML(fileXml);

        if (em != null) em.close();
        if (emf != null) emf.close();
    }
}
