package Services.Dto;

import DAL.DataEntities.Registers.Organization;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class LocationDto {
    private Long id;

    private String organization;

    private String locName;

    private String locCity;

    private String locStreet;

    private int locHouseNumber;

    private int locBuilding;

    private int locApartmentNumber;

    private String locInfo;

    public LocationDto(){};

    public LocationDto(Long id, String organization, String locName, String locCity, String locStreet,
                       int locHouseNumber, int locBuilding, int locApartmentNumber, String locInfo) {
        this.id = id;
        this.organization = organization;
        this.locName = locName;
        this.locCity = locCity;
        this.locStreet = locStreet;
        this.locHouseNumber = locHouseNumber;
        this.locBuilding = locBuilding;
        this.locApartmentNumber = locApartmentNumber;
        this.locInfo = locInfo;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getOrganization() {
        return organization;
    }

    public void setOrganization(String organization) {
        this.organization = organization;
    }

    public String getLocName() {
        return locName;
    }

    public void setLocName(String locName) {
        this.locName = locName;
    }

    public String getLocCity() {
        return locCity;
    }

    public void setLocCity(String locCity) {
        this.locCity = locCity;
    }

    public String getLocStreet() {
        return locStreet;
    }

    public void setLocStreet(String locStreet) {
        this.locStreet = locStreet;
    }

    public int getLocHouseNumber() {
        return locHouseNumber;
    }

    public void setLocHouseNumber(int locHouseNumber) {
        this.locHouseNumber = locHouseNumber;
    }

    public int getLocBuilding() {
        return locBuilding;
    }

    public void setLocBuilding(int locBuilding) {
        this.locBuilding = locBuilding;
    }

    public int getLocApartmentNumber() {
        return locApartmentNumber;
    }

    public void setLocApartmentNumber(int locApartmentNumber) {
        this.locApartmentNumber = locApartmentNumber;
    }

    public String getLocInfo() {
        return locInfo;
    }

    public void setLocInfo(String locInfo) {
        this.locInfo = locInfo;
    }

    @Override
    public String toString() {
        return "LocationDto{" +
                "id=" + id +
                ", organization='" + organization + '\'' +
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
