package Utils.XMLParser.Sax;

import Utils.ObjectFactory.ObjectFactory;
import Utils.XMLParser.XParser;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Objects;

@Stateless
public class SaxXmlParserSeqImpl<T> implements XParser<T> {
    private File xmlFile;
    private SAXParser parser;
    private SaxXmlContentHandler<T> contentHandler;

    @EJB
    private ObjectFactory<T> objectFactory;

    @PostConstruct
    private void init(){
        this.contentHandler = new SaxXmlContentHandler<>(this.objectFactory);
    }

    public SaxXmlParserSeqImpl() {
        SAXParserFactory spf = SAXParserFactory.newInstance();
        spf.setNamespaceAware(true);
        try {
            this.parser = spf.newSAXParser();
        } catch (ParserConfigurationException | SAXException e) {
            throw new IllegalStateException("Ошибка инициализации SAXParser");
        }
    }

    @Override
    public void setSource(File inputFile){
        if (inputFile == null){
            throw new IllegalStateException("Передана пустая строка с именем файла");
        }
        this.xmlFile = inputFile;
    }

    @Override
    public void setObjectFactory(ObjectFactory<T> objectFactory){
        Objects.requireNonNull(objectFactory, "Передана пустая ссылка (null) на фабрику объектов");
        this.objectFactory = objectFactory;
        if (this.contentHandler == null){
            this.contentHandler = new SaxXmlContentHandler<>(objectFactory);
        } else {
        this.contentHandler.setObjectFactory(objectFactory);
        }
    }

    @Override
    public List<T> getData() throws IllegalStateException {
        try {
            this.parser.parse(this.xmlFile, this.contentHandler);
        } catch (IOException | SAXException e) {
            e.printStackTrace();
        }
        return this.objectFactory.getData();
    }
}
