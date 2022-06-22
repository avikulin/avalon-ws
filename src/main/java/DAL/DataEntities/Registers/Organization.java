package DAL.DataEntities.Registers;

import DAL.DataEntities.Dictionaries.Country;
import DAL.DataEntities.Dictionaries.OrgType;

import javax.persistence.*;
import javax.xml.bind.annotation.XmlTransient;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "REG_ORGANIZATIONS")
public class Organization {
    @Id
    @GeneratedValue
    @Column(name = "ORG_ID")
    private Long id;

    @OneToOne
    @JoinColumn(name="ORG_TYPE", nullable = false)
    private OrgType type;

    @Column(name = "ORG_NAME", nullable = false, unique = true, length = 200)
    private String name;

    @Column(name = "ORG_FULL_NAME", nullable = false, length = 250)
    private String fullName;

    @Column(name = "INN_CODE", nullable = false, length = 12)
    private String innCode;

    @Column(name = "KPP_CODE", nullable = false, length = 9)
    private String kppCode;

    @Column(name = "OGRN_CODE", nullable = false, length = 13)
    private String ogrnCode;

    @Column(name = "WEB_SITE", nullable = false, length = 250)
    private String webSiteUrl;

    @OneToOne
    @JoinColumn(name="COUNTRY_ID")
    private Country countryOfRegistration;

    @XmlTransient
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "organization")
    private List<Location> locations = new ArrayList<>();

    public Long getId() {
        return id;
    }

    public OrgType getType() {
        return type;
    }

    public void setType(OrgType type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public String getFullName() {
        return fullName;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getInnCode() {
        return innCode;
    }

    public void setInnCode(String innCode) {
        this.innCode = innCode;
    }

    public String getKppCode() {
        return kppCode;
    }

    public void setKppCode(String kppCode) {
        this.kppCode = kppCode;
    }

    public String getOgrnCode() {
        return ogrnCode;
    }

    public void setOgrnCode(String ogrnCode) {
        this.ogrnCode = ogrnCode;
    }

    public String getWebSiteUrl() {
        return webSiteUrl;
    }

    public void setWebSiteUrl(String webSiteUrl) {
        this.webSiteUrl = webSiteUrl;
    }

    public Country getCountryOfRegistration() {
        return countryOfRegistration;
    }

    public void setCountryOfRegistration(Country countryOfRegistration) {
        this.countryOfRegistration = countryOfRegistration;
    }

    public List<Location> getLocations() {
        return locations;
    }

    @PrePersist
    @PreUpdate
    private void setFullName(){
        this.fullName = this.name+", "+this.type.getType_id();
    }

    @Override
    public String toString() {
        return "Organization{" +
                "id=" + id +
                ", type=" + type +
                ", name='" + name + '\'' +
                ", fullName='" + fullName + '\'' +
                ", innCode='" + innCode + '\'' +
                ", kppCode='" + kppCode + '\'' +
                ", ogrnCode='" + ogrnCode + '\'' +
                ", webSiteUrl='" + webSiteUrl + '\'' +
                ", countryOfRegistration=" + countryOfRegistration +
                ", locations=" + locations +
                '}';
    }
}


