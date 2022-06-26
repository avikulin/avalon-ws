package DAL.Repositories;

import DAL.Contracts.CrudRepository;
import DAL.Contracts.XmlRepository;
import Utils.ObjectFactory.Filter.XFilterImpl;
import Utils.ObjectFactory.ObjectFactory;
import Utils.XMLParser.XParser;
import Utils.XMLTransformer.XTransformer;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import java.io.File;
import java.util.List;
import java.util.Objects;

@Stateless
public class XmlRepositoryImpl<T, K> implements XmlRepository<T,K> {
    @EJB(beanName = "EquipmentRepo")
    private CrudRepository<T, K> repository;

    @EJB
    private XTransformer<T, K> transformer;

    @EJB(beanName = "SaxXmlParserSeqImpl")
    private XParser<T> parser;

    @EJB
    private ObjectFactory<T> objectFactory;

    private File xmlFile;

    @PostConstruct
    private void init(){
        this.transformer.setSource(repository);
        this.parser.setObjectFactory(this.objectFactory);
    }

    @Override
    public void setSourceRepo(CrudRepository<T, K> repo) {
        Objects.requireNonNull(repo, "Ссылка на репозиторий сущностей БД не должна быть пустой");
        this.repository = repo;
    }

    @Override
    public void setParser(XParser<T> parser, ObjectFactory<T> objectFactory) {
        Objects.requireNonNull(parser, "Ссылка на объект-парсер не должна быть пустой");
        Objects.requireNonNull(objectFactory, "Ссылка на фабрику объектов не должна быть пустой");
        this.parser = parser;
        this.objectFactory = objectFactory;
        this.parser.setObjectFactory(this.objectFactory);
    }

    @Override
    public void setTransformer(XTransformer<T, K> transformer) {
        Objects.requireNonNull(transformer, "Ссылка на объект-трансформер не должна быть пустой");
        this.transformer = transformer;
    }

    @Override
    public void setXmlFile(File xmlFile) {
        Objects.requireNonNull(xmlFile, "Ссылка на XML-файл не должна быть пустой");
        this.xmlFile = xmlFile;
        Objects.requireNonNull(parser, "Ссылка на объект-парсер не должна быть пустой");
        this.parser.setSource(this.xmlFile);
    }

    @Override
    public void writeXml() {
        Objects.requireNonNull(this.transformer, "Ссылка на объект-трансформер не инициализирована");
        Objects.requireNonNull(this.repository, "Ссылка на объект-репозиторий не инициализирована");
        this.transformer.setSource(this.repository);
        this.transformer.writeXML(this.xmlFile);
    }

    @Override
    public List<T> readXml(String filterExpr) {
        Objects.requireNonNull(filterExpr, "Ссылка на объект фильтра не должна быть пустой");
        Objects.requireNonNull(this.objectFactory, "\"Не инициализирована фабрика объектов");
        Objects.requireNonNull(this.parser, "Ссылка на объект-парсер не задана");
        Objects.requireNonNull(this.xmlFile, "XML-файл не задан");
        this.objectFactory.setFilter(new XFilterImpl(filterExpr));
        this.parser.setObjectFactory(this.objectFactory);
        this.parser.setSource(this.xmlFile);
        return this.parser.getData();
    }

    @Override
    public List<T> readXml() {
        Objects.requireNonNull(this.objectFactory, "Не инициализирована фабрика объектов");
        Objects.requireNonNull(this.parser, "Ссылка на объект-парсер не должна быть пустой");
        Objects.requireNonNull(this.xmlFile, "XML-файл не задан");
        this.objectFactory.setFilter(null);
        this.parser.setSource(this.xmlFile);
        return this.parser.getData();
    }
}
