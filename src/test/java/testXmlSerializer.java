import DAL.Contracts.ReadOnlyRepository;
import DAL.DataEntities.Registers.Equipment;
import DAL.Repositories.EquipmentRepo;
import Utils.XMLTransform.XRepo;
import Utils.XMLTransform.Contracts.XTransformer;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import java.io.File;
import java.io.FileOutputStream;
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

        ReadOnlyRepository<Equipment> repository = new EquipmentRepo();
        repository.registerDataSource(em);

        XTransformer<Equipment> transformer = new XRepo<>();
        transformer.setSource(repository);

        try(FileOutputStream fileStream = new FileOutputStream(fileXml)) {
            transformer.writeXML(fileStream);
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (em != null) em.close();
        if (emf != null) emf.close();
    }
}
