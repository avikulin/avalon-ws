package DAL.DataEntities.Registers;

import javax.persistence.*;

@Entity
@Table(name = "reg_org_locations", uniqueConstraints = {
        @UniqueConstraint(name = "org_locations_uc", columnNames = {"ORGANIZATION_ID", "LOC_NAME"})
})
public class Location {
    @Id
    @GeneratedValue
    @Column(name = "LOC_ID")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ORGANIZATION_ID")
    private Organization organization;

    @Column(name = "LOC_NAME", nullable = false, length = 50)
    private String locName;

    @Column(name = "LOC_CITY", nullable = false, length = 100)
    private String locCity;

    @Column(name = "LOC_STREET", nullable = false, length = 100)
    private String locStreet;

    @Column(name = "LOC_HOUSE_NUM", nullable = false)
    private int locHouseNumber;

    @Column(name = "LOC_BUILD_NUM", nullable = false)
    private int locBuilding;

    @Column(name = "LOC_APP_NUM", nullable = false)
    private int locApartmentNumber;

    @Column(name = "LOC_INFO", length = 200)
    private String locInfo;

    public Location(){}

    public Location(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public String getLocName() {
        return locName;
    }

    public void setLocName(String locName) {
        this.locName = locName;
    }

    public String getLocationCity() {
        return locCity;
    }

    public void setLocationCity(String locCity) {
        this.locCity = locCity;
    }

    public String getLocationStreet() {
        return locStreet;
    }

    public void setStreet(String locStreet) {
        this.locStreet = locStreet;
    }

    public int getHouseNumber() {
        return locHouseNumber;
    }

    public void setHouseNumber(int locHouseNumber) {
        this.locHouseNumber = locHouseNumber;
    }

    public int getBuilding() {
        return locBuilding;
    }

    public void setBuilding(int locBuilding) {
        this.locBuilding = locBuilding;
    }

    public int getApartmentNumber() {
        return locApartmentNumber;
    }

    public void setApartmentNumber(int locApartmentNumber) {
        this.locApartmentNumber = locApartmentNumber;
    }

    public String getInfo() {
        return locInfo;
    }

    public void setInfo(String locInfo) {
        this.locInfo = locInfo;
    }

    public void setOrganization(Organization organization) {
        this.organization = organization;
    }

    public Organization getOrganization() {
        return organization;
    }

    @Override
    public String toString() {
        return "Location{" +
                "id=" + id +
                ", organization=" + organization +
                ", locName='" + locName + '\'' +
                ", locCity='" + locCity + '\'' +
                ", locStreet='" + locStreet + '\'' +
                ", locHouseNumber=" + locHouseNumber +
                ", locBuilding=" + locBuilding +
                ", locApartmentNumber=" + locApartmentNumber +
                ", locInfo='" + locInfo + '\'' +
                '}';
    }
}
