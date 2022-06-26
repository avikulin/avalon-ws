package Services.Dto;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class EquipmentDto {
    private Long id;

    private String code;

    private String description;

    private String model;

    private String location;

    private String ipAddress;

    public EquipmentDto(){};

    public EquipmentDto(Long id, String code, String description, String model, String  location, String ipAddress) {
        this.id = id;
        this.code = code;
        this.description = description;
        this.model = model;
        this.location = location;
        this.ipAddress = ipAddress;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
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
        return "EquipmentDto{" +
                "id=" + id +
                ", code='" + code + '\'' +
                ", description='" + description + '\'' +
                ", model='" + model + '\'' +
                ", location='" + location + '\'' +
                ", ipAddress='" + ipAddress + '\'' +
                '}';
    }
}
