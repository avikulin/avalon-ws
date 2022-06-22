package Utils.XMLParser.Contracts;

import org.w3c.dom.Element;

public interface ObjectFactory {
    Object createItem(Element xmlData) throws IllegalStateException, NullPointerException;
}
