import DAL.Contracts.CrudRepository;
import DAL.Contracts.XmlRepository;
import DAL.DataEntities.Registers.Equipment;
import DAL.Repositories.EquipmentRepo;
import DAL.Repositories.XmlRepositoryImpl;
import Utils.ObjectFactory.Filter.XFilterImpl;
import Utils.ObjectFactory.ObjectFactoryImpl;
import Utils.XMLParser.Sax.SaxXmlParserSeqImpl;
import Utils.XMLTransformer.XTransformer;
import Utils.XMLTransformer.XTransformerImpl;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import java.io.File;
import java.util.List;

public class testXRepo {
    private static EntityManagerFactory emf;
    private static EntityManager em;

    public static void main(String[] args) {
        XmlRepository<Equipment, Long> xmlRepository = new XmlRepositoryImpl<>();

        xmlRepository.setParser(new SaxXmlParserSeqImpl<>(), new ObjectFactoryImpl<>());
        xmlRepository.setTransformer(new XTransformerImpl<>());
        xmlRepository.setXmlFile(new File("sample_file.xml"));

        emf = Persistence.createEntityManagerFactory("lab-db");
        em = emf.createEntityManager();

        CrudRepository<Equipment, Long> repository = new EquipmentRepo();
        repository.registerDataSource(em);
        xmlRepository.setSourceRepo(repository);

        System.out.println("---WRITE ENTITIES TO XML---");
        xmlRepository.writeXml();
        System.out.println();
        System.out.println();
        System.out.println("---READ ALL ENTITIES FROM XML---");
        List<Equipment> res = xmlRepository.readXml();
        for (Equipment eq: res){
            System.out.println(eq);
            System.out.println();
        }
        System.out.println();
        System.out.println();
        System.out.println("---READ ENTITIES FROM XML BY FILTER " +
                           "\"@DAL.DataEntities.Registers.Equipment/DAL.DataEntities.Dictionaries.Model/" +
                           "DAL.DataEntities.Dictionaries.Vendor[name LIKE \"Packard\"]\"---");
        List<Equipment> res2 = xmlRepository.readXml("@DAL.DataEntities.Registers.Equipment/" +
                "DAL.DataEntities.Dictionaries.Model/DAL.DataEntities.Dictionaries.Vendor[name LIKE \"Packard\"]");
        for (Equipment eq: res2){
            System.out.println(eq);
            System.out.println();
        }
        System.out.println();
        System.out.println();

        System.out.println("---READ ENTITIES FROM XML BY FILTER " +
                "\"@DAL.DataEntities.Registers.Equipment/DAL.DataEntities.Dictionaries.Model/" +
                "DAL.DataEntities.Dictionaries.Vendor[id EQ \"1002\"]\"---");
        List<Equipment> res3 = xmlRepository.readXml("@DAL.DataEntities.Registers.Equipment/" +
                "DAL.DataEntities.Dictionaries.Model/DAL.DataEntities.Dictionaries.Vendor[id EQ \"1002\"]");
        for (Equipment eq: res3){
            System.out.println(eq);
            System.out.println();
        }

        if (em != null) em.close();
        if (emf != null) emf.close();

    }
}
