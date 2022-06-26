package DAL.DataEntities.Dictionaries;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlAttribute;

@Entity
@Table(name = "DICT_COUNTRIES")
public class Country {
    @XmlAttribute
    @Id
    @Column(name = "COUNTRY_ID")
    private String id;

    @XmlAttribute
    @Column(name = "COUNTRY_CODE", nullable = false, unique = true)
    private String code;

    @XmlAttribute
    @Column(name = "SHORT_NAME", nullable = false, unique = true)
    private String shortName;

    @XmlAttribute
    @Column(name = "FULL_NAME", nullable = false)
    private String fullName;

    @XmlAttribute
    @Column(name = "REGION", nullable = false)
    private String region;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getShortName() {
        return shortName;
    }

    public void setShortName(String shortName) {
        this.shortName = shortName;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    @Override
    public String toString() {
        return "Country{" +
                "id='" + id + '\'' +
                ", code='" + code + '\'' +
                ", shortName='" + shortName + '\'' +
                ", fullName='" + fullName + '\'' +
                ", region='" + region + '\'' +
                '}';
    }
}
