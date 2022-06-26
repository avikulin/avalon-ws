package Views;

import DAL.Contracts.XmlRepository;
import DAL.DataEntities.Registers.Equipment;

import javax.ejb.EJB;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@WebServlet(name = "XmlRequestView", urlPatterns = "/data")
public class XmlRequestView extends HttpServlet {
    private static final String DATA_ERRORS = "LIST_ERRORS";
    private static final String DATA_XML_RESPONSE = "TEXT_XML_CONTENT";
    private static final String DATA_XML_REQUEST = "TEXT_XML_QUERY";
    private static final String DATA_EQUIPMENT_TABLE = "TABLE_EQUIPMENT_UNITS";
    private static final File file = new File("equipment_units_data.xml");
    @EJB
    XmlRepository<Equipment, Long> repository;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        resp.setCharacterEncoding("UTF-8");
        List<Equipment> eqUnits = null;
        List<String> errors = new ArrayList<>();
        String xmlContent = null;
        String query = null;
        try {
            query = req.getParameter(DATA_XML_REQUEST);
            repository.setXmlFile(file);
            repository.writeXml();
            xmlContent = Files.lines(file.toPath()).collect(Collectors.joining(System.lineSeparator()));
            if (query != null && !query.isEmpty()) {
                eqUnits = repository.readXml(query);
            } else {
                eqUnits = repository.readXml();
            }

        } catch (Exception e) {
            String msg = String.format("Ошибка в приложении. %s", e.getMessage());
            errors.add(msg);
            req.setAttribute(DATA_ERRORS, errors);
            req.getRequestDispatcher("/errors/error500.jsp").forward(req, resp);
        }

        req.setAttribute(DATA_XML_REQUEST, query);
        req.setAttribute(DATA_XML_RESPONSE, xmlContent);
        req.setAttribute(DATA_EQUIPMENT_TABLE, eqUnits);
        req.setAttribute(DATA_ERRORS, errors);

        req.getRequestDispatcher("xml_viewer.jsp").forward(req, resp);
        return;
    }
}
