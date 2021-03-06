package DAL.DataEntities.Registers;

import DAL.DataEntities.Dictionaries.Model;

import javax.persistence.*;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
@Entity
@Table(name = "REG_EQUIPMENT_UNITS")
public class Equipment {
    @XmlAttribute
    @Id
    @GeneratedValue
    @Column(name = "UNIT_ID")
    private Long id;

    @XmlAttribute
    @Column(name = "UNIT_CODE", nullable = false, length = 50)
    private String code;

    @XmlAttribute
    @Column(name = "UNIT_DESC", nullable = true, length = 250)
    private String description;

    @XmlAttribute
    @OneToOne
    @JoinColumn(name = "MODEL_ID", nullable = false)
    private Model model;

    @XmlAttribute
    @OneToOne
    @JoinColumn(name = "LOCATION_ID", nullable = false)
    private Location location;

    @XmlAttribute
    @Column(name = "IP_ADDR", nullable = false)
    private String ipAddress;

    public Long getId() {
        return id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Model getModel() {
        return model;
    }

    public void setModel(Model model) {
        this.model = model;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    @Override
    public String toString() {
        return "Equipment{" +
                "id=" + id +
                ", code='" + code + '\'' +
                ", description='" + description + '\'' +
                ", model=" + model +
                ", location=" + location +
                ", ipAddress='" + ipAddress + '\'' +
                '}';
    }
}
